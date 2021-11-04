package no.kristiania;

import no.kristiania.database.Question;
import no.kristiania.database.QuestionDao;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;


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
    }

    public static Question exampleQuestion() {
        Question question = new Question();
        question.setTitle(TestData.pickOne("Food <3", "Cats <3"));
        question.setDescription(TestData.pickOne("Are you hungry?","Do you like cats?"));

        return question;
    }

}
