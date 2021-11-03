package no.kristiania;

import no.kristiania.http.HttpServer;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer( 3000);
        server.setRoot(Paths.get("src/main/resources"));
        server.start();

    }
}
