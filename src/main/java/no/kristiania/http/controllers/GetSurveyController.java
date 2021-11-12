package no.kristiania.http.controllers;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.Survey;
import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.database.daos.SurveyDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.List;

public class GetSurveyController implements HttpController {
    private AnswerOptionDao answerOptionDao;
    private final QuestionDao questionDao;
    private final SurveyDao surveyDao;

    public GetSurveyController(SurveyDao surveyDao, QuestionDao questionDao, AnswerOptionDao answerOptionDao) {
        this.surveyDao = surveyDao;
        this.questionDao = questionDao;
        this.answerOptionDao = answerOptionDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String target = request.getRequestTarget();
        String responseText = "";

        try {
            if(request.queries.isEmpty()){
                System.out.println("Empty queries");
                responseText = getSurveys();
            } else {
                System.out.println("Non empty queries");
                responseText = getSurveyAndQuestions(Long.parseLong(request.queries.get("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpResponseMessage(200, responseText);
    }

    private String getSurveys() throws SQLException {
        String response = "";

        for(Survey s : surveyDao.listAll()){
            response += "<li value=" + s.getId() + ">" +
                    "<a href=\"/answerSurvey.html?id=" + s.getId() +"\">" + s.getName() + "</a>" +
                    "</li>";
        }
        System.out.println(response);

        return response;
    }

    private String getSurveyAndQuestions(long id) throws SQLException {
        Survey survey = surveyDao.retrieve(id);
        String response = "<h1 class=\"title is-1\">" + survey.getName() + "</h1>";

        for (Question question: questionDao.listAll(survey)) {
            List<AnswerOption> answerOptions = answerOptionDao.listAll(question.getId());
            question.setAnswerOptions(answerOptions);
            response +=
                "<section>" +
                    "<h3 class=\"title is-3\">" + question.getTitle() + "</h3>" +
                    "<h3 class=\"subtitle is-4\">" + question.getDescription() + "</h2>" +
                    getAnswerOptions(question) +
                "</section>";
        }

        return response;
    }

    private String getAnswerOptions(Question question) {
        String response = "";

        for (AnswerOption answerOption: question.getAnswerOptions()) {
            response +=
                    "<div style=\"margin-bottom:20px;\">" +
                            "<h4 class= \"subtitle is-5\" style=\"margin-bottom: 5px\">" + answerOption.getText() + "</h4>" +
                            "<div style=\"display:flex;\" id=\"answerOption-" + answerOption.getId() + "\" class=\"control\">" +
                            "<div style=\"margin-right: 10px\">" + question.getLowLabel() + "</div>" +
                            getAnswerLabels(answerOption.getId()) +
                            "<div style=\"margin-left: 10px\">" + question.getHighLabel() + "</div>" +
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
