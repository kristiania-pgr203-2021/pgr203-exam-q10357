package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAnswerDaoTest {
    private static final DataSource dataSource = TestData.testDataSource(UserAnswerDaoTest.class.getName());
    private static QuestionDao questionDao;
    private static AnswerOptionDao answerOptionDao;
    private static SurveyDao surveyDao;
    private static UserAnswerDao userAnswerDao;
    private static SessionUserDao sessionUserDao;

    private static final Survey survey = TestData.exampleSurvey();
    private static final SessionUser user = new SessionUser();
    private static Question question;
    private static SessionUser sessionUser;

    @BeforeAll
    private static void setupDatabase() throws SQLException {
        surveyDao = TestData.fillSurveyTable(dataSource);
        sessionUserDao = TestData.fillSessionUserTable(dataSource);
        questionDao = TestData.fillQuestionTable(dataSource, surveyDao);
        answerOptionDao = TestData.fillOptionTable(dataSource, questionDao);
        userAnswerDao = TestData.fillUserAnswerTable(dataSource, answerOptionDao, sessionUserDao);

        sessionUser = TestData.exampleSessionsUser();

    }

    @Test
    public void shouldListAllSavedAnswers() throws SQLException {
        List<UserAnswer> userAnswers = userAnswerDao.listAll();

        // assert all answer options are saved
        assertThat(userAnswers)
                .extracting(UserAnswer::getAnswerOptionId)
                .containsAll(userAnswers.stream().distinct().map(ua -> ua.getAnswerOptionId()).collect(Collectors.toList()));
    }

    @Test
    public void shouldListAllSavedAnswersWithGivenSessionUser() throws SQLException {
        long randomNumber = TestData.generateRandomNumber(1, sessionUserDao.listAll().size());
        sessionUser = sessionUserDao.retrieve(randomNumber);
        List<UserAnswer> userAnswers = userAnswerDao.listAll(sessionUser.getId());

        assertThat(userAnswers).isNotEmpty();
    }
}
