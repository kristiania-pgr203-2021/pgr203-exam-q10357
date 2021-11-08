package no.kristiania.http.controllers;

import no.kristiania.database.Question;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;

public class ListQuestionController implements HttpController {
    private final QuestionDao qDao;

    public ListQuestionController(QuestionDao questionDao) {
        this.qDao = questionDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String target = request.getRequestTarget();
        String responseText = "";

        try {
            for(Question q : qDao.listAll()){
                responseText += "<option value=" + q.getId() + ">" + q.getDescription() + "</option>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpResponseMessage(200, responseText);
    }
}
