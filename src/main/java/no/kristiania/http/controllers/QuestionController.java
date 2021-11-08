package no.kristiania.http.controllers;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.Map;

public class QuestionController implements  HttpController{
    private final QuestionDao questionDao;
    private String location;
    private String responseTxt = "";

    public QuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }


    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        System.out.println("Now we in handle");
        Map<String, String> queries =QueryHandler.handleQueries(request.queries);
        System.out.println(queries);

        String apiTarget = request.getRequestTarget().split("/")[2];

        if(!validateQueries(queries))
            return new HttpResponseMessage(400, responseTxt);

        String description = queries.get("text");
        String title = queries.get("title");

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        System.out.println(apiTarget);

        switch(apiTarget){
            case "newQuestion":
                question.setHighLabel(queries.get("high_label"));
                question.setLowLabel(queries.get("low_label"));
                addQuestionToDatabase(question);
                break;
            case "updateQuestion":
                System.out.println("In update");
                question.setId(Long.parseLong(queries.get("questions")));
                updateExistingQuestion(question);
                break;
        }

        return new HttpResponseMessage(303, location);
    }


    private void updateExistingQuestion(Question question) {
        try {
            questionDao.update(question);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(question.getDescription());
        location = "/updateQuestion.html";
    }

    private void addQuestionToDatabase(Question question) {
        try {
            questionDao.save(question);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        location = "/newQuestion.html";
    }


    private boolean validateQueries(Map<String, String> queries) {
        if (!queries.containsKey("text") || !queries.containsKey("title")
                || queries.get("text").length() == 0 || queries.get("title").length() == 0) {
            responseTxt = "Bad request - the post request must include valid title and text";
            return false;
        }
        return true;
    }
}
