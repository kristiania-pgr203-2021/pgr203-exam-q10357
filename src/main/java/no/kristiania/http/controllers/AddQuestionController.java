package no.kristiania.http.controllers;

import no.kristiania.database.QuestionDao;
import no.kristiania.http.HttpMessage;

import java.util.Map;

public class AddQuestionController implements  HttpController{
    private final QuestionDao questionDao;

    public AddQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }


    @Override
    public HttpMessage handle(HttpMessage request) {
        String responseTxt = "";
        Map<String, String> queries = request.queries;

        if (!queries.containsKey("question")) {
            responseTxt = "Bad request - the post request must include category and productName";
            return new HttpMessage("HTTP/1.1 400 Bad Request", responseTxt);
        }
        System.out.println("I am here");
        return null;
    }
}
