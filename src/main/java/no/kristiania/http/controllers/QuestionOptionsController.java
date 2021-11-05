package no.kristiania.http.controllers;

import no.kristiania.database.Question;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.http.HttpMessage;

import java.sql.SQLException;

public class QuestionOptionsController implements HttpController {
    private final QuestionDao qDao;

    public QuestionOptionsController(QuestionDao questionDao) {
        this.qDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) {
        String target = request.getRequestTarget();
        String responseText = "";

        try {
            for(Question q : qDao.listAll()){
                responseText += "<option value=" + q.getId() + ">" + q.getDescription() + "</option>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}
