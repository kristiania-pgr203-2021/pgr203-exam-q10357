package no.kristiania.http.controllers;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.List;

public class ListQuestionController implements HttpController {
    private final QuestionDao qDao;
    private AnswerOptionDao answerOptionDao;

    public ListQuestionController(QuestionDao questionDao) {
        this.qDao = questionDao;
    }

    public ListQuestionController(QuestionDao questionDao, AnswerOptionDao answerOptionDao) {
        this.qDao = questionDao;
        this.answerOptionDao = answerOptionDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String target = request.getRequestTarget();
        String responseText = "";

        try {
            if(request.queries.isEmpty()){
                responseText = getQuestionsForSurvey();
            } else {
                responseText = getQuestionAndAnswerOptions(Long.parseLong(request.queries.get("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpResponseMessage(303, responseText);
    }

    private String getQuestionsForSurvey() throws SQLException {
        String response = "";

        for(Question q : qDao.listAll()){
            response += "<li value=" + q.getId() + ">" +
                        "<a href=\"/answerQuestion.html?id=" + q.getId() +"\">" + q.getTitle() + "</a>" +
                    "</li>";
        }

        return response;
    }

    private String getQuestionAndAnswerOptions(long id) throws SQLException {
        String response = "";
        Question question = qDao.retrieve(id);
        List<AnswerOption> answerOptions = answerOptionDao.listAll(question.getId());
        question.setAnswerOptions(answerOptions);

        response +=
            "<h1 class=\"title is-1\">" + question.getTitle() + "</h1>" +
            "<h3 class=\"subtitle is-4\">" + question.getDescription() + "</h2>" +
            getAnswerOptions(question);

        return response;
    }

    private String getAnswerOptions(Question question) {
        String response = "";

        for (AnswerOption answerOption: question.getAnswerOptions()) {
            response +=
                "<div>" +
                    "<div style=\"display:flex;\" id=\"answerOption-" + answerOption.getId() + "\" class=\"control\">" +
                        "<div>" + question.getLowLabel() + "</div>" +
                        getAnswerLabels(answerOption.getId()) +
                        "<div>" + question.getHighLabel() + "</div>" +
                    "</div>" +
                "</div>";
        }
        return response;
    }

    private String getAnswerLabels(long answerOptionId) {
        String response = "";

        for(int i = 1; i <= 5; i++) {
            response +=
                "<label class=\"radio\">" +
                    "<input type=\"radio\" name=\"answer-" + answerOptionId + "\">" + i + "</input>" +
                "</label>";
        }

        return response;
    }
}
