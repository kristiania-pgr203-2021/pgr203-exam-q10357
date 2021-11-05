package no.kristiania.http.controllers;

import no.kristiania.database.daos.QuestionDao;
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
        System.out.println(queries);

        if (!queries.containsKey("text") || !queries.containsKey("title")) {
            responseTxt = "Bad request - the post request must include title and text";
            return new HttpMessage("HTTP/1.1 400 Bad Request", responseTxt);
        }
        System.out.println("I am here");
        String questionTxt = queries.get("text");
        String questionTitle = queries.get("title");

        responseTxt = "Successfully added new question " + questionTitle + " ";

        return null;
    }
}
