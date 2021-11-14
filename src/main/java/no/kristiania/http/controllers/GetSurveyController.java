package no.kristiania.http.controllers;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.Survey;
import no.kristiania.database.UserAnswer;
import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.database.daos.SurveyDao;
import no.kristiania.database.daos.UserAnswerDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GetSurveyController implements HttpController {
    private final AnswerOptionDao answerOptionDao;
    private final QuestionDao questionDao;
    private final SurveyDao surveyDao;
    private final UserAnswerDao userAnswerDao;

    public GetSurveyController(SurveyDao surveyDao, QuestionDao questionDao, AnswerOptionDao answerOptionDao, UserAnswerDao userAnswerDao) {
        this.surveyDao = surveyDao;
        this.questionDao = questionDao;
        this.answerOptionDao = answerOptionDao;
        this.userAnswerDao = userAnswerDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String target = request.getRequestTarget();
        String responseText = "";

        try {
            if(request.queries.isEmpty()){
                String htmlSite = getHtmlSite(target);
                System.out.println("Empty queries");
                responseText = getSurveys(htmlSite);
            } else {
                System.out.println("Non empty queries");
                responseText = getSurveyAndQuestions(Long.parseLong(request.queries.get("id")), target);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpResponseMessage(200, responseText);
    }

    private String getHtmlSite(String target) {
        if(target.equals("/api/surveys")){
            return "answerSurvey";
        }else if(target.equals("/api/surveyResults")){
            return "listSurveyAnswers";
        }
        return "Invalid target";
    }

    private String getSurveys(String site) throws SQLException {
        String response = "";

        for(Survey s : surveyDao.listAll()){
            response += "<li value=" + s.getId() + ">" +
                    "<a href=\"" + site +".html?id=" + s.getId() +"\">" + s.getName() + "</a>" +
                    "</li>";
        }
        System.out.println(response);

        return response;
    }

    private String getSurveyAndQuestions(long id, String target) throws SQLException {
        Survey survey = surveyDao.retrieve(id);
        String response = "<h1 class=\"title is-1\">" + survey.getName() + "</h1>" +
                "<input type=\"hidden\" id=\"surveyId\" name=\"surveyId\" value=\"" + id + "\">";

        for (Question question: questionDao.listAll(survey)) {
            List<AnswerOption> answerOptions = answerOptionDao.listAll(question.getId());
            question.setAnswerOptions(answerOptions);
            response +=
                "<section>" +
                    "<h3 class=\"title is-3\">" + question.getTitle() + "</h3>" +
                    "<h3 class=\"subtitle is-4\">" + question.getDescription() + "</h2>" +
                    getAnswerOptions(question, target) +
                "</section>";
        }

        return response;
    }

    private String getAnswerOptions(Question question, String target) throws SQLException {
        String response = "";

        for (AnswerOption answerOption: question.getAnswerOptions()) {
            if(target.equals("/api/surveys")){
                response +=
                        "<div style=\"margin-bottom:20px;\">" +
                                "<h4 class= \"subtitle is-5\" style=\"margin-bottom: 5px\">" + answerOption.getText() + "</h4>" +
                                "<div style=\"display:flex;\"" + "\" class=\"control\">" +
                                "<div style=\"margin-right: 10px\">" + question.getLowLabel() + "</div>" +
                                getScaleValues(answerOption.getId()) +
                                "<div style=\"margin-left: 10px\">" + question.getHighLabel() + "</div>" +
                                "</div>" +
                                "</div>";
            }else if(target.equals("/api/surveyResults")){
                response +=
                        "<div style=\"margin-bottom:20px;\">" +
                                "<div style=\"margin-right: 10px\">" + "<h4 class = \"subtitle is-5\" style=\"margin-bottom: 5px\">Labels:</h4> " +
                                "<p>Low: " +  question.getLowLabel() + " / High: " + question.getHighLabel() + "</p></div><br>" +
                                "<h4 class= \"subtitle is-5\" style=\"margin-bottom: 5px\">" + answerOption.getText() + "</h4>" +
                                "<div" + "\" class=\"control\">" +
                                "<ul>" + getListOfAnswers(answerOption)+ "</ul>" +
                                "</div>" +
                        "</div>";

            }
        }
        return response;
    }

    private String getListOfAnswers(AnswerOption answerOption) throws SQLException {
        String list = "";

        List<UserAnswer> answers = userAnswerDao.listAllAnswersToOption(answerOption.getId());
        List<Integer> values = answers.stream().map(a -> a.getValue()).collect(Collectors.toList());
        for(Integer v : values){
            list += "<li>" + v + "</li>";
        }

        return list;
    }

    private String getScaleValues(long answerOptionId) {
        String response = "";

        for(int value = 1; value <= 5; value++) {
            response +=
                    "<label class=\"radio\">" +
                        "<input type=\"radio\" name=\"answerOption_" + answerOptionId + "\" value=\"" + value + "\">" +
                            value +
                        "</input>" +
                    "</label>";
        }

        return response;
    }
}
