package no.kristiania.database.daos;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T>{
    void save(T object) throws SQLException;

    T retrieve(long id) throws SQLException;
}
