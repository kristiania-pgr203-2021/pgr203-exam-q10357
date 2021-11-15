package no.kristiania.http.controllers;

import no.kristiania.database.SessionUser;
import no.kristiania.database.daos.SessionUserDao;
import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

import java.sql.SQLException;
import java.util.Map;

public class SessionUserController implements HttpController{
    private final SessionUserDao sessionUserDao;

    public SessionUserController(SessionUserDao sessionUserDao){
        this.sessionUserDao = sessionUserDao;

    }
    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        String responseTxt = "";
        Map<String, String> queries = QueryHandler.handleQueries(request.queries);
        if(!queries.containsKey("name") || queries.get("name").length() < 1 || queries.get("name").equals("Unknown")){
            return new HttpResponseMessage(400, "Name must be submitted, name must not equal 'Unknown'");
        }

        SessionUser sessionUser = new SessionUser();
        sessionUser.setCookieId(queries.get("name"));
        try {
            sessionUserDao.save(sessionUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpResponseMessage(303, "/surveys.html", sessionUser.getId());
    }
}
