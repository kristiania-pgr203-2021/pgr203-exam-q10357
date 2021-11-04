package no.kristiania.http.controllers;

import no.kristiania.http.HttpMessage;

public interface HttpController {
    HttpMessage handle(HttpMessage request);
}
