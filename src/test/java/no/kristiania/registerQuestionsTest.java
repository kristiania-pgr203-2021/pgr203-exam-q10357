package no.kristiania;

import no.kristiania.database.Question;
import no.kristiania.database.QuestionDao;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class registerQuestionsTest {
    private static final DataSource dataSource = TestData.testDataSource("QuestionDaoTest");
    private final QuestionDao dao = new QuestionDao(dataSource);
    @Test
    void shouldRegisterQuestion(){
        Question question = new Question();
    }

    @Test
    void shouldRetrieveSavedQuestion() throws SQLException {
        Question question = exampleQuestion();
        System.out.println(question);
        dao.save(question);

        assertThat(dao.retrieve(question.getId()))
                .usingRecursiveComparison()
                .isEqualTo(question);
    }

    void shouldListAllQuestions(){
        Question q1;
    }

    public static Question exampleQuestion() {
        Question question = new Question();
        question.setTitle(TestData.pickOne("Food", "Cats", "Test", "Fails..", "Pass!", "Give us an A?"));
        question.setDescription(TestData.pickOne("Are you hungry?","Do you like cats?", "Do you like A's?",
                "Why are you failing?", "Have you heard about ENJIN coin?"));

        return question;
    }



}
