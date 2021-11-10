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
    private boolean isUpdate;


    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Question question) throws SQLException {
        long id = save(question, "insert into question (title, description, minanswerlabel, maxanswerlabel) values (?, ?, ?, ?)");
        question.setId(id);
    }

    public void update(Question question) throws SQLException {
        isUpdate = true;
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
        if(isUpdate == true){
            prepareStatementForUpdate(question, statement);
            return;
        }
        statement.setString(1, question.getTitle());
        statement.setString(2, question.getDescription());
        statement.setString(3, question.getLowLabel());
        statement.setString(4, question.getHighLabel());
    }

    protected void prepareStatementForUpdate(Question question, PreparedStatement statement) throws SQLException {
        statement.setString(1, question.getTitle());
        statement.setString(2, question.getDescription());
        isUpdate = false;
    }

    @Override
    protected Question mapFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setTitle(rs.getString("title"));
        question.setDescription(rs.getString("description"));
        question.setLowLabel(rs.getString("minAnswerLabel"));
        question.setHighLabel(rs.getString("maxAnswerLabel"));

        return question;
    }

    public List<Question> listAll() throws SQLException {
        return listAll("select * from question");
    }
}
