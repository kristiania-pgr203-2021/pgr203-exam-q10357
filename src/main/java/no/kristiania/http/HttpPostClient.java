package no.kristiania.http;

import no.kristiania.http.messages.HttpRequestMessage;

import java.io.IOException;
import java.net.Socket;

//This class is a helper for making tests with POST method
public class HttpPostClient {

    private final String host;
    private final int port;
    private final String requestTarget;

    private int responseCode;
    public HttpRequestMessage responseMessage;

    public HttpPostClient(String host, int port, String requestTarget, String contentBody) throws IOException {
        this.host = host;
        this.port = port;
        this.requestTarget = requestTarget;

        Socket socket = new Socket(host, port);
        String request = "POST " + requestTarget + " HTTP/1.1\r\n" +
                "Host:" + host + "\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + contentBody.length() + "\r\n" +
                "\r\n" +
                contentBody;
        socket.getOutputStream().write(request.getBytes());

        responseMessage = new HttpRequestMessage(socket);
        String[] startLine = responseMessage.getStartLine();
        responseCode = Integer.valueOf(startLine[1]);
    }


    public int getResponseCode() {
        return responseCode;
    }


}

