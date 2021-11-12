package no.kristiania.http.controllers;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.Map;

public class AddOptionController implements HttpController {
    private final AnswerOptionDao answerOptionDao;
    private String responseTxt = "";

    public AddOptionController(AnswerOptionDao answerOptionDao) {
        this.answerOptionDao = answerOptionDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        System.out.println("are we here?");
        String responseTxt = "";
        Map<String, String> queries = QueryHandler.handleQueries(request.queries);
        System.out.println(queries);

        if (!validateQueries(queries)) {
            responseTxt = "Bad request - the post request must include title and text";
            return new HttpResponseMessage(400, responseTxt);
        }

        addOptionToDatabase(queries);
        return new HttpResponseMessage(303, "/addOption.html");
    }

    private void addOptionToDatabase(Map<String, String> queries) {
        String optionText = queries.get("option");
        Long qId = Long.parseLong(queries.get("question"));

        AnswerOption ao = new AnswerOption();
        ao.setQuestionId(qId);
        ao.setText(optionText);

        try {
            answerOptionDao.save(ao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean validateQueries(Map<String, String> queries) {
        if (!queries.containsKey("option") || !queries.containsKey("question") || !QueryHandler.checkAllQueries(queries)) {
            responseTxt = "Bad request - the post request must include valid option";
            return false;
        }
        return true;
    }
}
