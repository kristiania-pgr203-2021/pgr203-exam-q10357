package no.kristiania.database;


import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public abstract class Datasource {
    public static DataSource createDataSource() throws IOException {
        DataSource dataSource = getDataSource();
        //be flyway om å migrere dataene til den siste versjonen av mine tablet funksjoner
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();

        return dataSource;
    }

    private static DataSource getDataSource() throws IOException {
        Properties dbProperties = getDatabaseProperties();

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(dbProperties.getProperty("dbUrl"));
        dataSource.setUser(dbProperties.getProperty("dbUser"));
        dataSource.setPassword(dbProperties.getProperty("dbPassword"));

        return dataSource;
    }

    public static Properties getDatabaseProperties() throws IOException {
        File propertyFile = new File("pgr203.properties");
        InputStream dbConfigFile = new FileInputStream(propertyFile);
        Properties dbProperties = new Properties();

        if (dbConfigFile != null) {
            dbProperties.load(dbConfigFile);
        }

        return dbProperties;
    }
}
