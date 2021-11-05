package no.kristiania.http.controllers;

import no.kristiania.database.Question;
import no.kristiania.database.QuestionDao;
import no.kristiania.http.HttpMessage;

import java.sql.SQLException;
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

        if (!queries.containsKey("text")) {
            responseTxt = "Bad request - the post request must include title and text";
            return new HttpMessage("HTTP/1.1 400 Bad Request", responseTxt);
        }
        System.out.println("I am here");
        String description = queries.get("text").replaceAll("\\+", " ");
        String title = queries.get("title");


        responseTxt = "Successfully added new question, with title:" + title + " and text:  " + description;
        
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);

        try {
            questionDao.save(question);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpMessage("HTTP/1.1 200 OK", responseTxt);
    }
}
