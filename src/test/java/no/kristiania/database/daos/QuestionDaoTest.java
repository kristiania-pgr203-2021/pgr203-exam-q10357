package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private static final DataSource dataSource = TestData.testDataSource("QuestionDaoTest");
    private static final QuestionDao qDao = new QuestionDao(dataSource);
    private static final AnswerOptionDao aoDao = new AnswerOptionDao(dataSource);
    private static final UserAnswerDao uaDao = new UserAnswerDao(dataSource);
    private static final SessionUser user = new SessionUser();
    private static final SessionUserDao userDao = new SessionUserDao(dataSource);
    private static final SurveyDao sDao = new SurveyDao(dataSource);
    private static Survey survey;
    private static Question question;

    @BeforeAll
    private static void setupDatabase() throws SQLException {
        survey = TestData.exampleSurvey();
        sDao.save(survey);

        question = TestData.exampleQuestion(survey);
        qDao.save(question);

        // Add 4 answer options to the question
        ArrayList<AnswerOption> answerOptions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            AnswerOption answerOption = TestData.exampleOption(question);
            aoDao.save(answerOption);
            answerOptions.add(answerOption);
        }

        question.setAnswerOptions(answerOptions);

        user.setCookieId("testId");
        userDao.save(user);
    }

    @Test
    void shouldRetrieveSavedQuestion() throws SQLException {
        assertThat(qDao.retrieve(question.getId()))
                .extracting(Question::getDescription)
                .isEqualTo(question.getDescription());
    }

    @Test
    void shouldSaveQuestionWithoutSqlInjection() throws SQLException {
        Question question = TestData.sqlInjectionAttempt(survey);
        qDao.save(question);

        assertThat(qDao.retrieve(question.getId()).getDescription())
                .isEqualTo(question.getDescription());

    }

    /*@Test
    void shouldGetErrorMessageForNonExistingID() throws SQLException {
        qDao.retrieve(1000000000);
    }*/

    @Test
    void shouldUpdateQuestion() throws SQLException {
        String update = "Which colors do you like?";
        question.setDescription(update);
        question.setTitle("Colors");


        //Generating userAnswer and testing to see it deletes as we update question
        UserAnswer answer = TestData.exampleUserAnswer(question, question.getAnswerOptions().get(0), user);
        uaDao.save(answer);

        qDao.update(question);

        assertThat(uaDao.listAll(question.getId(), user.getId()).isEmpty());
        assertThat(qDao.retrieve(question.getId()).getDescription()).isEqualTo(update);

    }

}
