package no.kristiania.http.controllers;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.SessionUser;
import no.kristiania.database.UserAnswer;
import no.kristiania.database.daos.AnswerOptionDao;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.database.daos.SessionUserDao;
import no.kristiania.database.daos.UserAnswerDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RegisterUserAnswerController implements HttpController {
    private final AnswerOptionDao answerOptionDao;
    private final SessionUserDao sessionUserDao;
    private final UserAnswerDao userAnswerDao;
    private final QuestionDao questionDao;

    public RegisterUserAnswerController(AnswerOptionDao answerOptionDao, SessionUserDao sessionUserDao, UserAnswerDao userAnswerDao, QuestionDao questionDao) {
        this.answerOptionDao = answerOptionDao;
        this.sessionUserDao = sessionUserDao;
        this.userAnswerDao = userAnswerDao;
        this.questionDao = questionDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        System.out.println(request.queries);
        String responseText = "";

        //here we handle the request headers to retrieve the cookie information neede
        //To connect sessionUser to UserAnswer
        String cookieInfo = request.getHeaders().get("Cookie");
        cookieInfo = cookieInfo.substring(cookieInfo.indexOf("cookieName="));

        String cookieName = cookieInfo.substring(cookieInfo.indexOf("=") + 1);
        System.out.println(cookieName);

        long sessionUserId = Long.valueOf(cookieName);


        if (!request.queries.containsKey("surveyId")) {
            return new HttpResponseMessage(400, "The request must include the query surveyId");
        }

        try {
            List<AnswerOption> answerOptions = answerOptionDao
                    .listAllBySurveyId(Long.parseLong(request.queries.get("surveyId")));

            if (!hasUserAnsweredAll(answerOptions, request.queries)) {
                return new HttpResponseMessage(400, "The user must answer all questions and options");
            }

            SessionUser user = createUser();
            SaveUserAnswers(user, answerOptions, request.queries);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HttpResponseMessage(500, "Error");
        }

        return new HttpResponseMessage(303, "/index.html");
    }

    private Boolean hasUserAnsweredAll(List<AnswerOption> answerOptions, Map<String, String> queries) {
        for (AnswerOption answerOption : answerOptions) {
            if (!queries.containsKey("answerOption_" + answerOption.getId())) {
                return false;
            }
        }
        return true;
    }

    private SessionUser createUser() throws SQLException {
        SessionUser user = new SessionUser();
        user.setCookieId("notImplemented");
        sessionUserDao.save(user);
        return user;
    }

    private void SaveUserAnswers(SessionUser user, List<AnswerOption> answerOptions, Map<String, String> queries) throws SQLException {
        for (AnswerOption answerOption : answerOptions) {
            int answerValue = Integer.parseInt(queries.get("answerOption_" + answerOption.getId()));

            Integer trueValue = checkLabels(answerOption, answerValue);
            if(trueValue != null){
                answerValue = trueValue;
            }

            UserAnswer answer = new UserAnswer();
            answer.setAnswerOptionId(answerOption.getId());
            answer.setValue(answerValue);
            answer.setSessionUserId(user.getId());
            userAnswerDao.save(answer);
        }
    }

    //This method is very flawed, but is used to make more sense of answers where labels are of integer, but not 1-5
    private Integer checkLabels(AnswerOption answerOption, int answer) throws SQLException {
        Integer trueValue = null;
        System.out.println(answer);
        long questionId  = answerOption.getQuestionId();
        Question question = questionDao.retrieve(questionId);

        Integer low = QueryHandler.tryParse(question.getLowLabel());
        Integer high = QueryHandler.tryParse(question.getHighLabel());

        if(low != null && high != null){
            if(answer == 1){
                return low;
            }else if(answer == 5){
                return high;
            }
            if(low == 1){
                low = 0;
            }

            int diff = (int) Math.floor((high - low) / 5);

            //Tried a lot of different things, but couldn't find the correct mathematical formula
            //Approximate with strange low and high values, but works somewhat
            trueValue = (int) Math.ceil((low + (diff * answer)));
        }
        return trueValue;
    }
}
