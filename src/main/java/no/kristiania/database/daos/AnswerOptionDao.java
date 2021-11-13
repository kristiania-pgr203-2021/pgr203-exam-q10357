package no.kristiania.database.daos;

import no.kristiania.database.AnswerOption;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AnswerOptionDao extends AbstractDao<AnswerOption>{
    public AnswerOptionDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(AnswerOption answerOption) throws SQLException {
        long id = save(answerOption, "insert into answerOption (question_id, text) values (?, ?)");
        answerOption.setId(id);
    }

    @Override
    protected void prepareStatement(AnswerOption answerOption, PreparedStatement statement) throws SQLException {
        statement.setLong(1, answerOption.getQuestionId());
        statement.setString(2, answerOption.getText());
    }

    public AnswerOption retrieve(long id) throws SQLException {
        return retrieve(id, "select * from answerOption where id = ?");
    }

    @Override
    protected AnswerOption mapFromResultSet(ResultSet rs) throws SQLException {
        AnswerOption answerOption = new AnswerOption();
        answerOption.setId(rs.getLong("id"));
        answerOption.setQuestionId(rs.getLong("question_id"));
        answerOption.setText(rs.getString("text"));
        return answerOption;
    }

    public List<AnswerOption> listAll(Long questionId) throws SQLException {
        return listAll(
        "select * from answerOption where question_id = ?",
            statement -> {
                try {
                    statement.setLong(1, questionId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
    }

    public List<AnswerOption> listAllBySurveyId(Long surveyId) throws SQLException {
        return listAll(
                "select ao.id, ao.question_id, ao.text from answerOption ao " +
                    "inner join question q " +
                    "ON ao.question_id = q.id " +
                    "where q.survey_id = ?",
                statement -> {
                    try {
                        statement.setLong(1, surveyId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }
}
