package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;

public interface HttpController {
    HttpMessage handle(HttpMessage request);
}
