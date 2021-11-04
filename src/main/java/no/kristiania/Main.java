package no.kristiania;

import no.kristiania.database.Datasource;
import no.kristiania.database.Question;
import no.kristiania.database.QuestionDao;
import no.kristiania.http.HttpServer;
import no.kristiania.http.controllers.AddQuestionController;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        DataSource dataSource = Datasource.createDataSource();
        HttpServer server = new HttpServer( 3000, dataSource);
        QuestionDao qDao = new QuestionDao(dataSource);
        server.setRoot(Paths.get("src/main/resources"));
        server.addController("/api/newQuestion", new AddQuestionController(qDao));
        server.start();

    }
}
