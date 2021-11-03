package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;


public class HttpClient {

    private final String host;
    private final int port;
    private final String requestTarget;

    private int responseCode;
    public HttpMessage responseMessage;

    public HttpClient(String host, int port, String requestTarget) throws IOException {
        this.host = host;
        this.port = port;
        this.requestTarget = requestTarget;

        Socket socket = new Socket(host, port);
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host:" + host + "\r\n" +
                "Connection:close\r\n" + //necessary??
                "\r\n";
        socket.getOutputStream().write(request.getBytes());

        responseMessage = new HttpMessage(socket);
        String[] startLine = responseMessage.getStartLineArray();
        responseCode = Integer.valueOf(startLine[1]);
    }



    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public int getResponseCode() {
        return responseCode;
    }


}

