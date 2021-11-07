package no.kristiania.http.controllers;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.Map;

public class AddOptionController implements HttpController {
    private final AnswerOptionDao answerOptionDao;

    public AddOptionController(AnswerOptionDao answerOptionDao) {
        this.answerOptionDao = answerOptionDao;
    }


    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String responseTxt = "";
        Map<String, String> queries = request.queries;
        System.out.println(queries);

        if (!queries.containsKey("option+") || !queries.containsKey("questions")) {
            responseTxt = "Bad request - the post request must include title and text";
            return new HttpResponseMessage(400, responseTxt);
        }

        addOptionToDatabase(queries);
        return new HttpResponseMessage(303, responseTxt);
    }

    private void addOptionToDatabase(Map<String, String> queries) {
        String optionText = queries.get("option+").replaceAll("\\+", " ");
        Long qId = Long.parseLong(queries.get("questions"));

        AnswerOption ao = new AnswerOption();
        ao.setQuestionId(qId);
        ao.setText(optionText);

        try {
            answerOptionDao.save(ao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
