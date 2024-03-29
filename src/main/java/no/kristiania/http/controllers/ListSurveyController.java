package no.kristiania.http.controllers;

import no.kristiania.database.Survey;
import no.kristiania.database.daos.SurveyDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;

public class ListSurveyController implements HttpController{
    private final SurveyDao surveyDao;

    public ListSurveyController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String responseText = "";

        try {
            for(Survey s : surveyDao.listAll()){
                responseText += "<option value=" + s.getId() + ">" + s.getName() + "</option>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new HttpResponseMessage(500,"error");
        }

        return new HttpResponseMessage(200, responseText);
    }
}
