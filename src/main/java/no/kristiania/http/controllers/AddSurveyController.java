package no.kristiania.http.controllers;

import no.kristiania.database.Survey;
import no.kristiania.database.daos.SurveyDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.Map;

public class AddSurveyController implements HttpController {
    private final SurveyDao surveyDao;
    private String responseTxt = "";

    public AddSurveyController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        Map<String, String> queries = QueryHandler.handleQueries(request.queries);
        System.out.println(queries);

        if (!validateQueries(queries)) {
            responseTxt = "Bad request - the post request must include name";
            return new HttpResponseMessage(400, responseTxt);
        }

        try {
            addSurveyToDatabase(queries);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HttpResponseMessage(500,"error");
        }


        return new HttpResponseMessage(303, "/newSurvey.html");
    }

    private void addSurveyToDatabase(Map<String, String> queries) throws SQLException {
        String surveyName = queries.get("name");

        Survey survey = new Survey();
        survey.setName(surveyName);
        surveyDao.save(survey);
    }

    private boolean validateQueries(Map<String, String> queries) {
        if (!queries.containsKey("name") || !QueryHandler.checkAllQueries(queries)) {
            responseTxt = "Bad request - the post request must include valid title and text";
            return false;
        }
        return true;
    }
}
