package no.kristiania.http.controllers;

import no.kristiania.database.Question;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.http.HttpMessage;

import java.sql.SQLException;
import java.util.Map;

public class QuestionController implements  HttpController{
    private final QuestionDao questionDao;
    String responseTxt = "";

    public QuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }


    @Override
    public HttpMessage handle(HttpMessage request) {
        Map<String, String> queries = request.queries;
        String apiTarget = request.getRequestTarget().split("/")[2];
        System.out.println(apiTarget);
        System.out.println(queries);

        if (!queries.containsKey("text") || !queries.containsKey("title")) {
            responseTxt = "Bad request - the post request must include title and text";
            return new HttpMessage("HTTP/1.1 400 Bad Request", responseTxt);
        }

        String description = queries.get("text").replaceAll("\\+", " ");
        String title = queries.get("title");

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);

        switch(apiTarget){
            case "newQuestion":
                responseTxt = addQuestionToDatabase(question);
                break;
            case "updateQuestion":
                question.setId(Long.parseLong(queries.get("questions")));
                responseTxt = updateExistingQuestion(question);
                break;
        }

        return new HttpMessage("HTTP/1.1 200 OK", responseTxt);
    }


    private String updateExistingQuestion(Question question) {
        try {
            questionDao.update(question);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return responseTxt = "Successfully updated question, to title:" + question.getTitle() + " and text:  " + question.getDescription();
    }

    private String addQuestionToDatabase(Question question) {
        try {
            questionDao.save(question);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return responseTxt = "Successfully added new question, with title:" + question.getTitle() + " and text:  " + question.getDescription();
    }
}
