package no.kristiania.HttpTest;

import no.kristiania.TestData;
import no.kristiania.database.Question;
import no.kristiania.database.Survey;
import no.kristiania.database.daos.*;
import no.kristiania.http.HttpClient;
import no.kristiania.http.HttpServer;
import no.kristiania.http.controllers.ListQuestionController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    private static HttpServer server;
    private static final DataSource dataSource = TestData.testDataSource("HttpServerTest");
    private static SurveyDao surveyDao;
    private static QuestionDao questionDao;
    private static AnswerOptionDao answerOptionDao;
    private static UserAnswerDao userAnswerDao;

    @BeforeAll
    public static void fillDataBase() throws IOException {
        surveyDao = TestData.fillSurveyTable(dataSource);
        questionDao = TestData.fillQuestionTable(dataSource, surveyDao);
        answerOptionDao = TestData.fillOptionTable(dataSource, questionDao);

        server = new HttpServer(0, dataSource);
        server.start();
        server.setRoot(Paths.get("src/main/resources"));
    }

    @Test
    void shouldReturnQuestionFromServer() throws IOException, SQLException {
        String expected = "";
        long id = 1;

        server.addController("/api/questionOptions", new ListQuestionController(questionDao));
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
    void requestShouldReturn200() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/index.html");
        //remember to call executeRequest
        assertEquals(200, client.getResponseCode());
    }

    @Test
    void notFoundRequestTargetShouldReturn404NotFound() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/api/request/notFound");
        assertEquals(500, client.getResponseCode());
    }


}
