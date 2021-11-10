package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.SessionUser;
import no.kristiania.database.UserAnswer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAnswerDaoTest {
    private static final DataSource dataSource = TestData.testDataSource(UserAnswerDaoTest.class.getName());
    private static final QuestionDao questionDao = new QuestionDao(dataSource);
    private static final AnswerOptionDao optionDao = new AnswerOptionDao(dataSource);
    private static final SessionUserDao userDao = new SessionUserDao(dataSource);
    private static final UserAnswerDao answerDao = new UserAnswerDao(dataSource);
    private static final Question question = new Question();
    private static final SessionUser user = new SessionUser();

    @BeforeAll
    private static void setupDatabase() throws SQLException {
        question.setTitle("Question Test");
        question.setDescription("Test");
        question.setLowLabel("low");
        question.setHighLabel("high");
        questionDao.save(question);

        // Add 4 answer options to the question
        ArrayList<AnswerOption> answerOptions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            AnswerOption answerOption = new AnswerOption();
            answerOption.setText("Option " + i);
            answerOption.setQuestionId(question.getId());
            optionDao.save(answerOption);
            answerOptions.add(answerOption);
        }

        question.setAnswerOptions(answerOptions);

        user.setCookieId("testId");
        userDao.save(user);
    }


    @Test
    public void shouldListAllSavedAnswers() throws SQLException {
        List<AnswerOption> answerOptions = question.getAnswerOptions();
        int answerValue = 1;

        for (AnswerOption answerOption : answerOptions) {
            UserAnswer answer = new UserAnswer();

            answer.setQuestionId(question.getId());
            answer.setAnswerOptionId(answerOption.getId());
            answer.setSessionUserId(user.getId());
            answer.setValue(answerValue++);

            answerDao.save(answer);
        }

        List<UserAnswer> userAnswers = answerDao.listAll(question.getId(), user.getId());

        // assert all answer options are saved
        assertThat(userAnswers)
                .extracting(UserAnswer::getAnswerOptionId)
                .contains((long) 1, (long) 2, (long) 3, (long) 4);

        // assert all answer values are saved
        assertThat(userAnswers)
                .extracting(UserAnswer::getValue)
                .contains(1, 2, 3, 4);

        // assert all answers are connected to one question
        assertThat(userAnswers)
                .extracting(UserAnswer::getQuestionId)
                .allMatch(id -> Objects.equals(id, question.getId()));

        // assert all answers are connected to one user
        assertThat(userAnswers)
                .extracting(UserAnswer::getSessionUserId)
                .allMatch(id -> Objects.equals(id, user.getId()));
    }
}
