package no.kristiania.http.controllers;

import no.kristiania.database.AnswerOption;
import no.kristiania.database.SessionUser;
import no.kristiania.database.UserAnswer;
import no.kristiania.database.daos.AnswerOptionDao;
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

    public RegisterUserAnswerController(AnswerOptionDao answerOptionDao, SessionUserDao sessionUserDao, UserAnswerDao userAnswerDao) {
        this.answerOptionDao = answerOptionDao;
        this.sessionUserDao = sessionUserDao;
        this.userAnswerDao = userAnswerDao;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String responseText = "";
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

        return new HttpResponseMessage(200, "Thank you for participating.");
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
            UserAnswer answer = new UserAnswer();
            answer.setAnswerOptionId(answerOption.getId());
            answer.setValue(answerValue);
            answer.setSessionUserId(user.getId());
            userAnswerDao.save(answer);
        }
    }
}
