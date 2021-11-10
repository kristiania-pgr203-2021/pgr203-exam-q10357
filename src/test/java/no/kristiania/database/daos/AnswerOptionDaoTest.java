package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerOptionDaoTest {
    private static final DataSource dataSource = TestData.testDataSource(AnswerOptionDaoTest.class.getName());
    private static final QuestionDao questionDao = new QuestionDao(dataSource);
    private static final AnswerOptionDao optionDao = new AnswerOptionDao(dataSource);
    private static final Question question = new Question();

    @BeforeAll
    private static void setupDatabase() throws SQLException {
        question.setTitle("Question Test");
        question.setDescription("Test");
        question.setLowLabel("low");
        question.setHighLabel("high");
        questionDao.save(question);
    }

    @Test
    void saveNewAnswerOptionShouldHaveId() throws SQLException {
        AnswerOption answerOption = new AnswerOption();
        answerOption.setText("TestOption");
        answerOption.setQuestionId(question.getId());

        optionDao.save(answerOption);

        assertThat(answerOption.getId())
                .isNotZero()
                .isNotNegative();
    }

    @Test
    public void shouldRetrieveSavedAnswerOption() throws SQLException {
        AnswerOption answerOption = new AnswerOption();
        answerOption.setText("TestOption");
        answerOption.setQuestionId(question.getId());

        optionDao.save(answerOption);
        AnswerOption retrievedOption = optionDao.retrieve(answerOption.getId());

        assertThat(answerOption)
                .usingRecursiveComparison()
                .isEqualTo(retrievedOption);
    }

    @Test
    public void shouldListAllAnswerOptions() throws SQLException {
        AnswerOption answerOption = new AnswerOption();
        answerOption.setText("TestOption");
        answerOption.setQuestionId(question.getId());

        optionDao.save(answerOption);
        List<AnswerOption> options = optionDao.listAll(question.getId());

        assertThat(options).isNotEmpty();
        assertThat(options)
                .extracting(AnswerOption::getId)
                .contains(answerOption.getId());
    }
}
