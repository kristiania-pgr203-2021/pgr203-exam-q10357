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

    public AddOptionController(AnswerOptionDao answerOptionDao) {
        this.answerOptionDao = answerOptionDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String responseTxt = "";
        Map<String, String> queries = QueryHandler.handleQueries(request.queries);

        if (!validateQueries(queries)) {
            responseTxt = "Bad request - the post request must include title and text";
            return new HttpResponseMessage(400, responseTxt);
        }

        try {
            addOptionToDatabase(queries);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HttpResponseMessage(500,"error");
        }

        return new HttpResponseMessage(303, "/addOption.html");
    }

    private void addOptionToDatabase(Map<String, String> queries) throws SQLException {
        String optionText = queries.get("option");
        Long qId = Long.parseLong(queries.get("question"));

        AnswerOption ao = new AnswerOption();
        ao.setQuestionId(qId);
        ao.setText(optionText);
        answerOptionDao.save(ao);
    }

    private boolean validateQueries(Map<String, String> queries) {
        if (!queries.containsKey("option") || !queries.containsKey("question") || !QueryHandler.checkAllQueries(queries)) {
            return false;
        }
        return true;
    }
}
