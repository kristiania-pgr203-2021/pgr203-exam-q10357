package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.Question;
import no.kristiania.database.Survey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private static final DataSource dataSource = TestData.testDataSource(QuestionDaoTest.class.getName());
    private static QuestionDao questionDao;
    private static AnswerOptionDao answerOptionDao;
    private static SurveyDao surveyDao;
    private static UserAnswerDao userAnswerDao;

    private static List<Survey> surveys;
    private static List<Question> questions;

    //Variables for generating numbers
    private final int low = 1;

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
    void shouldSaveQuestionWithProperEncoding() throws SQLException {
        Question question = TestData.exampleQuestion(surveys.get(1));
        question.setTitle("æøå");

        questionDao.save(question);
        assert(questionDao.retrieve(question.getId()).getTitle().equals("æøå"));
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

        questionDao.update(question);

        //assert that answers are deleted upon update
        assertThat(answerOptionDao.listAll(
                "select * from answeroption where question_id = ?",
                statement -> {
                    try{
                        statement.setLong(1, question.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })).isEmpty();

        assertThat(questionDao.retrieve(question.getId()).getDescription()).isEqualTo(update);
    }

    @Test
    void shouldListAllQuestions() {
        assert(!questions.isEmpty());
        assertThat(questions)
                .extracting(Question::getId)
                .containsAll(questions.stream().map(q -> q.getId()).collect(Collectors.toList()));
    }


}
