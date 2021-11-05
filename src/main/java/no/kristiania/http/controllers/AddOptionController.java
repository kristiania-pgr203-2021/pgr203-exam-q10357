package no.kristiania.http.controllers;

import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.http.HttpMessage;

import java.util.Map;

public class AddOptionController implements HttpController {
    private final AnswerOptionDao answerOptionDao;

    public AddOptionController(AnswerOptionDao answerOptionDao) {
        this.answerOptionDao = answerOptionDao;
    }


    @Override
    public HttpMessage handle(HttpMessage request) {
        String responseTxt = "";
        Map<String, String> queries = request.queries;
        System.out.println(queries);
        return null;
    }
}
