package no.kristiania.HttpTest;

import no.kristiania.TestData;
import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.Survey;
import no.kristiania.database.daos.*;
import no.kristiania.http.HttpClient;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import no.kristiania.http.controllers.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    private static HttpServer server;
    private static final DataSource dataSource = TestData.testDataSource("HttpServerTest");
    private static SurveyDao surveyDao;
    private static QuestionDao questionDao;
    private static AnswerOptionDao answerOptionDao;
    private static UserAnswerDao userAnswerDao;
    private static SessionUserDao sessionUserDao;

    @BeforeAll
    public static void fillDataBase() throws IOException, SQLException {
        surveyDao = TestData.fillSurveyTable(dataSource);
        sessionUserDao = TestData.fillSessionUserTable(dataSource);
        questionDao = TestData.fillQuestionTable(dataSource, surveyDao);
        answerOptionDao = TestData.fillOptionTable(dataSource, questionDao);
        userAnswerDao = TestData.fillUserAnswerTable(dataSource, answerOptionDao, sessionUserDao);

        //Adding controllers before all tests run
        server = new HttpServer(0, dataSource);
        server.setRoot(Paths.get("src/main/resources"));
        server.addController("/api/newQuestion", new QuestionController(questionDao));
        server.addController("/api/updateQuestion", new QuestionController(questionDao));
        server.addController("/api/questionOptions", new ListQuestionController(questionDao));
        server.addController("/api/alternativeAnswers", new AddOptionController(answerOptionDao));
        server.addController("/api/newSurvey", new AddSurveyController(surveyDao));
        server.addController("/api/surveyOptions", new ListSurveyController(surveyDao));
        server.addController("/api/surveys", new GetSurveyController(surveyDao, questionDao, answerOptionDao, userAnswerDao));
        server.addController("/api/answerSurvey", new RegisterUserAnswerController(answerOptionDao, sessionUserDao, userAnswerDao, questionDao));
        server.addController("/api/surveyResults", new GetSurveyController(surveyDao, questionDao, answerOptionDao, userAnswerDao));

        server.start();
    }

    @Test
    void requestShouldReturn200() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/index.html");
        //remember to call executeRequest
        assertEquals(200, client.getResponseCode());
    }

    @Test
    void invalidApiRequestShouldReturn500() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/api/&&this=notValid");
        assertEquals(500, client.getResponseCode());
    }

    @Test
    void notFoundRequestTargetShouldReturn404NotFound() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/notFound.html");
        assertEquals(404, client.getResponseCode());
    }

    @Test
    void addSurveyShouldReturn303() throws IOException {
        server.addController("/api/newSurvey", new AddSurveyController(surveyDao));
        Survey survey = TestData.exampleSurvey();

        HttpPostClient client = new HttpPostClient(
                "localhost",
                server.getActualPort(),
                "/api/newSurvey",
                "name=" + survey.getName());

        System.out.println();

        assertEquals(303, client.getResponseCode());
    }

    @Test
    void shouldBeAbleToPostWithEncoding() throws IOException, SQLException {
        HttpPostClient client = new HttpPostClient(
                "localhost",
                server.getActualPort(),
                "/api/newSurvey",
                "name=%C3%A6%C3%B8%C3%A5");

        assertEquals(303, client.getResponseCode());
        assertThat(surveyDao.listAll())
            .anySatisfy(p -> assertThat(p.getName()).isEqualTo("æøå"));
    }

    @Test
    void getQuestionInSurveyShouldReturn200() throws IOException, SQLException {
        Survey survey = surveyDao.retrieve(TestData.generateRandomNumber(1, 5));

        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/api/surveys?id=" + survey.getId());
        assertEquals(200, client.getResponseCode());
    }

    @Test
    void shouldCreateNewQuestion() throws IOException, SQLException {
        HttpPostClient client = new HttpPostClient(
                "localhost",
                server.getActualPort(),
                "/api/newQuestion",
                "survey=1&title=Test&text=testText&low_label=1&high_label=10"
        );

        assertEquals(303, client.getResponseCode());
        assertThat(questionDao.listAll())
                .anySatisfy(p -> {
                    assertThat(p.getDescription()).isEqualTo("testText");
                });
    }

    @Test
    void shouldReturnQuestionFromServer() throws IOException, SQLException {
        String expected = "";
        long id = 1;

        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/api/questionOptions");

        for(Question q : questionDao.listAll()){
            expected += "<option value=" + questionDao.retrieve(id).getId() + ">" + questionDao.retrieve(id).getDescription() + "</option>";
            id++;
        }
        assertEquals(
                expected,
                client.responseMessage.messageBody
        );
    }

    @Test
    void shouldReturn400ForIncompatibleLabelTypes() throws IOException {
        HttpPostClient client = new HttpPostClient(
                "localhost",
                server.getActualPort(),
                "/api/newQuestion",
                "survey=1&title=Test&text=AnotherTestText&low_label=1&high_label=Totally+Agree"
        );

        assertEquals(400, client.getResponseCode());
    }

    @Test
    void shouldReturn200GetSurveys() throws IOException {
        HttpClient client = new HttpClient(
                "localhost",
                server.getActualPort(),
                "/api/surveys"
        );

        assertEquals(200, client.getResponseCode());
    }

    @Test
    void shouldReturn200ForSurveyResults() throws IOException, SQLException {
        Survey survey = surveyDao.listAll().stream().findFirst().get();
        HttpClient client = new HttpClient(
                "localhost",
                server.getActualPort(),
                "/api/surveyResults?id=" + survey.getId()
        );

        assertEquals(200, client.getResponseCode());
    }

    @Test
    void shouldReturn400ForAnswerSurveyWithNoId() throws IOException, SQLException {
        HttpPostClient client = new HttpPostClient(
                "localhost",
                server.getActualPort(),
                "/api/answerSurvey",
                "noId=-1"
        );

        assertEquals(400, client.getResponseCode());
        assertEquals("The request must include the query surveyId", client.responseMessage.messageBody);
    }

    @Test
    void shouldReturn400ForNotAllOptionAnswered() throws IOException, SQLException {
        Survey survey = surveyDao.listAll().stream().findFirst().get();

        HttpPostClient client = new HttpPostClient(
                "localhost",
                server.getActualPort(),
                "/api/answerSurvey",
                "surveyId=" + survey.getId()
        );

        assertEquals(400, client.getResponseCode());
        assertEquals("The user must answer all questions and options", client.responseMessage.messageBody);
    }

    @Test
    void shouldReturn303ForAnswerSurvey() throws IOException, SQLException {
        Survey survey = surveyDao.listAll().stream().findFirst().get();
        List<AnswerOption> answerOptions = answerOptionDao.listAllBySurveyId(survey.getId());
        String requestBody = "surveyId=" + survey.getId();
        int minScaleValue = 1;
        int maxScaleValue = 5;
        for (AnswerOption answerOption: answerOptions) {
            requestBody += "&answerOption_" + answerOption.getId().toString() + "=" +
                    ThreadLocalRandom.current().nextInt(minScaleValue, maxScaleValue);
        }

        HttpPostClient client = new HttpPostClient(
                "localhost",
                server.getActualPort(),
                "/api/answerSurvey",
                requestBody
        );

        assertEquals(303, client.getResponseCode());
    }
}
