package no.kristiania;

import no.kristiania.database.Datasource;
import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.database.daos.SurveyDao;
import no.kristiania.http.HttpServer;
import no.kristiania.http.controllers.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        DataSource dataSource = Datasource.createDataSource();
        HttpServer server = new HttpServer( 3000, dataSource);
        System.out.println(server.getActualPort());
        QuestionDao qDao = new QuestionDao(dataSource);
        SurveyDao sDao = new SurveyDao(dataSource);
        AnswerOptionDao aoDao = new AnswerOptionDao(dataSource);
        server.setRoot(Paths.get("src/main/resources"));
        server.addController("/api/newQuestion", new QuestionController(qDao));
        server.addController("/api/updateQuestion", new QuestionController(qDao));
        server.addController("/api/questionOptions", new ListQuestionController(qDao));
        server.addController("/api/alternativeAnswers", new AddOptionController(aoDao));
        server.addController("/api/newSurvey", new AddSurveyController(sDao));
        server.addController("/api/surveyOptions", new ListSurveyController(sDao));
        server.addController("/api/surveys", new GetSurveyController(sDao, qDao, aoDao));
        server.start();

    }
}
