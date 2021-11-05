package no.kristiania.database.daos;

import no.kristiania.database.SessionUser;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SessionUserDao extends AbstractDao<SessionUser> {
    public SessionUserDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(SessionUser user) throws SQLException {
        long id = save(user, "insert into sessionUser (cookie_id) values (?)");
        user.setId(id);
    }

    @Override
    protected void prepareStatement(SessionUser user, PreparedStatement statement) throws SQLException {
        statement.setString(1, user.getCookieId());
    }

    @Override
    public SessionUser retrieve(long id) throws SQLException {
        return retrieve(id, "select * from sessionUser where id = ?");
    }

    @Override
    protected SessionUser mapFromResultSet(ResultSet rs) throws SQLException {
        SessionUser user = new SessionUser();
        user.setId(rs.getLong("id"));
        user.setCookieId(rs.getString("cookie_id"));
        return user;
    }

    public List<SessionUser> listAll() throws SQLException {
        return null;
    }
}
