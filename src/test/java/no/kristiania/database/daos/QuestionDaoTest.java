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
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private static final DataSource dataSource = TestData.testDataSource("QuestionDaoTest");
    private static final QuestionDao qDao = new QuestionDao(dataSource);
    private static final AnswerOptionDao aoDao = new AnswerOptionDao(dataSource);
    private static final UserAnswerDao uaDao = new UserAnswerDao(dataSource);
    private static final SessionUser user = new SessionUser();
    private static final SessionUserDao userDao = new SessionUserDao(dataSource);
    private static Question question = TestData.exampleQuestion();

    @BeforeAll
    private static void setupDatabase() throws SQLException {
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
                .usingRecursiveComparison()
                .isEqualTo(question);
    }

    @Test
    void shouldSaveQuestionWithoutSqlInjection() throws SQLException {
        Question question = TestData.sqlInjectionAttempt();
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
        question.setDescription("Do you like pink?");
        question.setTitle("Colors");

        //Generating userAnswer and testing to see it deletes as we update question
        UserAnswer answer = TestData.exampleUserAnswer(question, question.getAnswerOptions().get(0), user);
        uaDao.save(answer);

        qDao.update(question);

        assertThat(uaDao.listAll(question.getId(), user.getId()).isEmpty());
        assertThat(qDao.retrieve(question.getId()).getDescription()).isEqualTo("Do you like pink?");

    }

    @Test
    void shouldListAllQuestions() throws SQLException {
        Collection<Question> questions = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            questions.add(TestData.exampleQuestion());
            qDao.save(question);
        }

        assertThat(qDao.listAll())
                .extracting(Question::getDescription)
                .containsAll((Iterable)questions);
    }



}
