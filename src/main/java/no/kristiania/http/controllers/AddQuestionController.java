package no.kristiania.http.controllers;

import no.kristiania.database.Question;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.http.HttpMessage;

import java.sql.SQLException;
import java.util.Map;

public class AddQuestionController implements  HttpController{
    private final QuestionDao questionDao;
    String responseTxt = "";

    public AddQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }


    @Override
    public HttpMessage handle(HttpMessage request) {
        Map<String, String> queries = request.queries;

        if (!queries.containsKey("text")) {
            responseTxt = "Bad request - the post request must include title and text";
            return new HttpMessage("HTTP/1.1 400 Bad Request", responseTxt);
        }

        responseTxt = addQuestionToDatabase(queries);

        return new HttpMessage("HTTP/1.1 200 OK", responseTxt);
    }

    private String addQuestionToDatabase(Map<String, String> queries) {
        String description = queries.get("text").replaceAll("\\+", " ");
        String title = queries.get("title");

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        try {
            questionDao.save(question);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return responseTxt = "Successfully added new question, with title:" + title + " and text:  " + description;
    }
}
