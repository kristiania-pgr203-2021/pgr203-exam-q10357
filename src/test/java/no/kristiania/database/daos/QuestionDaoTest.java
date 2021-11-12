package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.*;
import no.kristiania.http.HttpServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private static final DataSource dataSource = TestData.testDataSource("QuestionDaoTest");
    private static QuestionDao questionDao;
    private static AnswerOptionDao answerOptionDao;
    private static UserAnswerDao userAnswerDao;
    private static SessionUser user;
    private static SessionUserDao sessionUserDao;
    private static SurveyDao surveyDao;

    private static List<Survey> surveys;
    private static List<Question> questions;

    //Variables for generating numbers
    private int low = 1;

    @BeforeAll
    public static void fillDataBase() throws SQLException {
        surveyDao = TestData.fillSurveyTable(dataSource);
        questionDao = TestData.fillQuestionTable(dataSource, surveyDao);
        answerOptionDao = TestData.fillOptionTable(dataSource, questionDao);

        surveys = surveyDao.listAll();
        questions = questionDao.listAll();
    }

    @Test
    void shouldRetrieveSavedQuestion() throws SQLException {
        Question question = TestData.exampleQuestion(surveys.get(TestData.generateRandomNumber(low, surveys.size())));
        questionDao.save(question);

        assertThat(questionDao.retrieve(question.getId()))
                .extracting(Question::getDescription)
                .isEqualTo(question.getDescription());
    }

    @Test
    void shouldSaveQuestionWithoutSqlInjection() throws SQLException {
        Question question = TestData.sqlInjectionAttempt(surveyDao.retrieve(TestData.generateRandomNumber(low, surveys.size())));
        questionDao.save(question);

        assertThat(questionDao.retrieve(question.getId()).getDescription())
                .isEqualTo(question.getDescription());

    }



    @Test
    void shouldUpdateQuestion() throws SQLException {
        Question question = questionDao.retrieve(TestData.generateRandomNumber(low, questions.size()));
        String update = "Which colors do you like?";
        question.setDescription(update);
        question.setTitle("Colors");


        //Generating userAnswer and testing to see it deletes as we update question

        questionDao.update(question);

        assertThat(questionDao.retrieve(question.getId()).getDescription()).isEqualTo(update);

    }

}
