package no.kristiania.database.daos;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.UserAnswer;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserAnswerDao extends AbstractDao<UserAnswer> {
    public UserAnswerDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(UserAnswer answer) throws SQLException {
        long id = save(answer,
                "insert into userAnswer (question_id, answerOption_id, sessionUser_id, value) values (?,?,?,?)");
    }

    @Override
    protected void prepareStatement(UserAnswer answer, PreparedStatement statement) throws SQLException {
        statement.setLong(1, answer.getQuestionId());
        statement.setLong(2, answer.getAnswerOptionId());
        statement.setLong(3, answer.getSessionUserId());
        statement.setInt(4, answer.getValue());
    }

    public List<UserAnswer> listAll(Long questionId, Long userId) throws SQLException {
        return listAll(
            "select ua.question_id, answerOption_id, sessionUser_id, value, ao.text " +
                "from userAnswer as ua " +
                "inner join answerOption as ao on ua.answerOption_id = ao.id " +
                "where ua.question_id = ? and sessionUser_id = ?",
                statement -> {
                    try {
                        statement.setLong(1, questionId);
                        statement.setLong(2, userId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    @Override
    protected UserAnswer mapFromResultSet(ResultSet rs) throws SQLException {
        UserAnswer answer = new UserAnswer();
        answer.setQuestionId(rs.getLong("question_id"));
        answer.setSessionUserId(rs.getLong("sessionUser_id"));
        answer.setValue(rs.getInt("value"));

        AnswerOption answerOption = new AnswerOption();
        answerOption.setId(rs.getLong("answerOption_id"));
        answerOption.setText(rs.getString("text"));
        answer.setAnswerOption(answerOption);

        return answer;
    }
}
