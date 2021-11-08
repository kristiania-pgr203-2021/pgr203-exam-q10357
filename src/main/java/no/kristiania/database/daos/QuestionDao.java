package no.kristiania.database.daos;

import no.kristiania.database.Question;
import no.kristiania.database.UserAnswer;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestionDao extends AbstractDao<Question>{
    private final UserAnswerDao uaDao = new UserAnswerDao(this.dataSource);
    private final AnswerOptionDao aoDao = new AnswerOptionDao(this.dataSource);


    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Question question) throws SQLException {
        long id = save(question, "insert into question (title, description) values (?, ?)");
        question.setId(id);
    }

    public void update(Question question) throws SQLException {
        update(question, "update question " +
                "set title  = ?, description = ? " +
                 "where id = " + question.getId());
        //When editing a question, all associated answers and options will be deleted to ensure integrity of application
        delete("delete from useranswer where question_id =" + question.getId());
        delete("delete from answeroption where question_id =" + question.getId());

    }

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
