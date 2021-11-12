package no.kristiania.http.controllers;

import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

public class RegisterUserAnswerController implements  HttpController{
    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        System.out.println(request.queries);
        return null;
    }
}
