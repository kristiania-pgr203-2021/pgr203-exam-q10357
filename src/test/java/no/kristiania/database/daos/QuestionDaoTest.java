package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.daos.QuestionDao;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private static final DataSource dataSource = TestData.testDataSource("QuestionDaoTest");
    private final QuestionDao qDao = new QuestionDao(dataSource);
    private final AnswerOptionDao aoDao = new AnswerOptionDao(dataSource);
    @Test
    void shouldRegisterQuestion(){
        Question question = new Question();
    }

    @Test
    void shouldRetrieveSavedQuestion() throws SQLException {
        Question question = exampleQuestion();
        System.out.println(question);
        qDao.save(question);

        assertThat(qDao.retrieve(question.getId()))
                .usingRecursiveComparison()
                .isEqualTo(question);
    }

    @Test
    void shouldUpdateQuestion() throws SQLException {
        Question question = exampleQuestion();
        qDao.save(question);
        question.setDescription("Do you like pink?");
        AnswerOption option = null;
        question.setTitle("Colors");
        qDao.update(question);

        assertThat(qDao.retrieve(question.getId()).getDescription()).isEqualTo("Do you like pink?");
    }

    void shouldListAllQuestions(){
        Question q1;
    }

    public static AnswerOption exampleOption(Question question){
        AnswerOption option = new AnswerOption();
        option.setQuestionId(question.getId());
        return null;
    }

    public static Question exampleQuestion() {
        Question question = new Question();
        question.setTitle(TestData.pickOne("Food", "Cats", "Test", "Fails..", "Pass!", "Give us an A?"));
        question.setDescription(TestData.pickOne("Are you hungry?","Do you like cats?", "Do you like A's?",
                "Why are you failing?", "Have you heard about ENJIN coin?"));

        return question;
    }



}
