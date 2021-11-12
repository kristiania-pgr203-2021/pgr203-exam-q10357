package no.kristiania.database.daos;

import no.kristiania.database.Question;
import no.kristiania.database.Survey;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SurveyDao extends AbstractDao<Survey>{
    public SurveyDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Survey survey) throws SQLException {
        long id = save(survey, "insert into questionsurvey (name) values (?)");
        survey.setId(id);
    }

    public Survey retrieve(long id) throws SQLException {
        return retrieve(id, "select * from survey where id = ?");
    }

    public List<Survey> listAll() throws SQLException {
        return listAll("select * from questionsurvey");
    }

    @Override
    protected void prepareStatement(Survey survey, PreparedStatement statement) throws SQLException {
        statement.setString(1, survey.getName());
    }

    @Override
    protected Survey mapFromResultSet(ResultSet rs) throws SQLException {
        Survey survey = new Survey();
        survey.setId(rs.getLong("id"));
        survey.setName(rs.getString("name"));

        return survey;
    }
}
