package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AnswerOptionDao extends AbstractDao<AnswerOption>{
    private final QuestionDao questionDao;

    public AnswerOptionDao(DataSource dataSource, QuestionDao questionDao) {
        super(dataSource);
        this.questionDao = questionDao;
    }

    @Override
    public void save(AnswerOption answerOption) throws SQLException {

    }

    @Override
    public AnswerOption retrieve(long id) throws SQLException {
        return null;
    }

    @Override
    public List<AnswerOption> listAll() throws SQLException {
        return null;
    }

    @Override
    protected void prepareStatement(AnswerOption element, PreparedStatement statement) throws SQLException {

    }

    @Override
    protected AnswerOption mapFromResultSet(ResultSet rs) throws SQLException {
        return null;
    }
}
