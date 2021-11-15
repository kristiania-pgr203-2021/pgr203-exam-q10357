package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerOptionDaoTest {
    private static final DataSource dataSource = TestData.testDataSource(AnswerOptionDaoTest.class.getName());
    private static QuestionDao questionDao;
    private static AnswerOptionDao answerOptionDao;
    private static SurveyDao surveyDao;
    private static final int low = 1;

    private static Question question;
    private List<AnswerOption> options = new ArrayList<>();

    @BeforeAll
    public static void fillDataBase() throws SQLException {
        surveyDao = TestData.fillSurveyTable(dataSource);
        questionDao = TestData.fillQuestionTable(dataSource, surveyDao);
        answerOptionDao = TestData.fillOptionTable(dataSource, questionDao);

        question = questionDao.retrieve(TestData.generateRandomNumber(low, questionDao.listAll().size()));
    }

    @Test
    void saveNewAnswerOptionShouldHaveId() throws SQLException {
        AnswerOption answerOption = new AnswerOption();
        answerOption.setText("TestOption");
        answerOption.setQuestionId(question.getId());

        answerOptionDao.save(answerOption);

        assertThat(answerOption.getId())
                .isNotZero()
                .isNotNegative();
    }

    @Test
    public void shouldRetrieveSavedAnswerOption() throws SQLException {
        AnswerOption answerOption = new AnswerOption();
        answerOption.setText("TestOption");
        answerOption.setQuestionId(question.getId());

        answerOptionDao.save(answerOption);
        AnswerOption retrievedOption = answerOptionDao.retrieve(answerOption.getId());

        assertThat(answerOption)
                .usingRecursiveComparison()
                .isEqualTo(retrievedOption);
    }

    @Test
    public void shouldSaveOptionWithProperEncodings(){
        AnswerOption answerOption = new AnswerOption();
        answerOption.setText("æøå");
        answerOption.setQuestionId(question.getId());

        assert(answerOption.getText().equals("æøå"));
    }

    @Test
    public void shouldListAllAnswerOption() throws SQLException {
        AnswerOption answerOption = new AnswerOption();
        answerOption.setText("TestOption");
        answerOption.setQuestionId(question.getId());

        answerOptionDao.save(answerOption);
        options = answerOptionDao.listAll(question.getId());

        assertThat(options).isNotEmpty();
        assertThat(options)
                .extracting(AnswerOption::getId)
                .contains(answerOption.getId());
    }

    @Test
    public void shouldListAllAnswerOptionsInSurvey() throws SQLException {
        long randomNumber = TestData.generateRandomNumber(low, surveyDao.listAll().size());
        options = answerOptionDao.listAllBySurveyId(randomNumber);
        options = answerOptionDao.listAllBySurveyId(randomNumber);
        List<Question> questions = questionDao.listAll(
                "select * from question where survey_id = ?",
                statement -> {
                    try{
                        statement.setLong(1, randomNumber);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                });


        assertThat(options)
                .extracting(AnswerOption::getQuestionId)
                .containsAll(questions.stream().distinct().map(q -> q.getId()).collect(Collectors.toList()));
    }
}
