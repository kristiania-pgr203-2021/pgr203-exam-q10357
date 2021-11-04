package no.kristiania.database;


import javax.sql.DataSource;
import java.io.IOException;


public abstract class Datasource {
    public static DataSource createDataSource() throws IOException {
        return null;
    }
}
