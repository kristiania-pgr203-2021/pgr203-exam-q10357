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
        if(!queries.containsKey("name")){
            return new HttpResponseMessage(400, "Queries must include name");
        }

        SessionUser sessionUser = new SessionUser();
        sessionUser.setCookieId(queries.get("name"));
        try {
            sessionUserDao.save(sessionUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpResponseMessage(303, "/index.html", sessionUser.getId());
    }
}
