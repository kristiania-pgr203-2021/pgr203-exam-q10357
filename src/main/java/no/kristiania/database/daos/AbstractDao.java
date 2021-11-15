package no.kristiania.database.daos;

import no.kristiania.database.Survey;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractDao<T> {
    protected final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected T retrieve(long id, String sql) throws SQLException { //long kan ikke bli null, Long kan settes som null
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    sql
            )) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return mapFromResultSet(rs);
                }
            }
        }
    }

    protected void delete(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()){
                statement.executeUpdate(sql);
            }
        }
    }

    protected void update(T element, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)){
                prepareStatement(element, statement);
                statement.executeUpdate();
            }
        }
    }

    protected long save(T element, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS
            )){
                prepareStatement(element, statement);
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    return rs.getLong("id");
                }
            }
        }
    }

    protected List<T> listAll(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                return listAll(statement);
            }
        }
    }

    protected List<T> listAll(String sql, Function<PreparedStatement, Void> fn) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                fn.apply(statement);
                return listAll(statement);
            }
        }
    }

    private List<T> listAll(PreparedStatement statement) throws SQLException {
        try (ResultSet rs = statement.executeQuery()) {
            ArrayList<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapFromResultSet(rs));
            }
            return result;
        }
    }

    protected abstract void prepareStatement(T element, PreparedStatement statement) throws SQLException;

    protected abstract T mapFromResultSet(ResultSet rs) throws SQLException;
}
