package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserAnswerDao extends AbstractDao<UserAnswer>{
    private final QuestionDao questionDao;

    public UserAnswerDao(DataSource dataSource, QuestionDao questionDao) {
        super(dataSource);
        this.questionDao = questionDao;
    }

    @Override
    public void save(UserAnswer userAnswer) throws SQLException {
        long id = save(userAnswer, "insert into userAnswer () values ?");
    }

    @Override
    public UserAnswer retrieve(long id) throws SQLException {
        return null;
    }

    @Override
    protected void prepareStatement(UserAnswer element, PreparedStatement statement) throws SQLException {

    }

    @Override
    protected UserAnswer mapFromResultSet(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public List<UserAnswer> listAll() throws SQLException {
        return null;
    }
}
