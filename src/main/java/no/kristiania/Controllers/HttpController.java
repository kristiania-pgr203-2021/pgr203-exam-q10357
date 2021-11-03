package no.kristiania.Controllers;

import no.kristiania.http.HttpMessage;

public interface HttpController {
    HttpMessage handle(HttpMessage request);
}
