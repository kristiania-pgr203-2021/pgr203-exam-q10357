package no.kristiania.database.daos;

import no.kristiania.database.Question;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestionDao extends AbstractDao<Question>{
   private Question question;

    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(Question question) throws SQLException {
        long id = save(question, "insert into question (title, description) values (?, ?)");
        question.setId(id);
    }

    @Override
    public Question retrieve(long id) throws SQLException {
        return retrieve(id, "select * from question where id = ?");
    }

    @Override
    protected void prepareStatement(Question question, PreparedStatement statement) throws SQLException {
        statement.setString(1, question.getTitle());
        statement.setString(2, question.getDescription());
    }

    @Override
    protected Question mapFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setTitle(rs.getString("title"));
        question.setDescription(rs.getString("description"));

        return question;
    }

    public List<Question> listAll() throws SQLException {
        return listAll("select * from question");
    }
}
