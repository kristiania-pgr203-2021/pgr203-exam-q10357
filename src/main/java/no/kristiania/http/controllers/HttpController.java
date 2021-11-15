package no.kristiania.http.controllers;

import no.kristiania.http.messages.HttpRequestMessage;
import no.kristiania.http.messages.HttpResponseMessage;

public interface HttpController {
    HttpResponseMessage handle(HttpRequestMessage request);
}
