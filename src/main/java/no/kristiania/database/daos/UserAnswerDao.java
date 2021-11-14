package no.kristiania.database.daos;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
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
                "insert into userAnswer (answerOption_id, sessionUser_id, value) values (?,?,?)");
    }

    @Override
    protected void prepareStatement(UserAnswer answer, PreparedStatement statement) throws SQLException {
        statement.setLong(1, answer.getAnswerOptionId());
        statement.setLong(2, answer.getSessionUserId());
        statement.setInt(3, answer.getValue());
    }

    public List<UserAnswer> listAll() throws SQLException {
        return(listAll("select * from useranswer"));
    }


    public List<UserAnswer> listAll(Long userId) throws SQLException {
        return listAll(
            "select answerOption_id, sessionUser_id, value, ao.text " +
                "from userAnswer as ua " +
                "inner join answerOption as ao on ua.answerOption_id = ao.id " +
                "where sessionUser_id = ?",
                statement -> {
                    try {
                        statement.setLong(1, userId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    public List<UserAnswer> listAllAnswersToOption(Long optionID) throws SQLException {
        return listAll(
                "select answerOption_id, sessionUser_id, value, ao.text " +
                        "from userAnswer as ua " +
                        "inner join answerOption as ao on ua.answerOption_id = ao.id " +
                        "where ao.id = ?",
                statement -> {
                    try {
                        statement.setLong(1, optionID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    @Override
    protected UserAnswer mapFromResultSet(ResultSet rs) throws SQLException {
        UserAnswer answer = new UserAnswer();
        answer.setSessionUserId(rs.getLong("sessionUser_id"));
        answer.setValue(rs.getInt("value"));
        answer.setAnswerOptionId(rs.getLong("answeroption_id"));

        return answer;
    }
}
