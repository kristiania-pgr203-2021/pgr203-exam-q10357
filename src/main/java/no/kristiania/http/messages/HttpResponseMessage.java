package no.kristiania.http.messages;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseMessage extends HttpMessage {
    public final int responseCode;

    private final HashMap<Integer, String> responseCodeTexts = new HashMap<>() {{
        put(200, "OK");
        put(303, "See Other");
        put(400, "Bad Request");
    }};

    public HttpResponseMessage(Socket socket) throws IOException {
        super(socket);
        responseCode = Integer.parseInt(startLine[1]);
    }

    public HttpResponseMessage(int responseCode, String messageBody) {
        this.messageBody = messageBody;
        this.responseCode = responseCode;
        this.headers.put("Content-Type", "text/plain");
        this.headers.put("Content-Length", String.valueOf(messageBody.length()));
        this.headers.put("Connection", "close");
    }

    public HttpResponseMessage(int responseCode, String contentType, String messageBody) {
        this.messageBody = messageBody;
        this.responseCode = responseCode;
        this.headers.put("Content-Type", contentType);
        this.headers.put("Content-Length", String.valueOf(messageBody.length()));
        this.headers.put("Connection", "close");
    }

    public String getResponseText() throws IOException {
        return String.format("HTTP/1.1 %d %s\r\n" +
                getResponseHeaders() +
                "\r\n" +
                messageBody, responseCode, getResponseCodeText(responseCode));
    }

    private String getResponseCodeText(int responseCode) throws IOException {
        if (responseCodeTexts.containsKey(responseCode)) {
            return responseCodeTexts.get(responseCode);
        }

        throw new IOException("The response code " + responseCode + " does not exist.");
    }

    private String getResponseHeaders() {
        String responseHeaders = "";
        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHeaders += header.getKey() + ":" + header.getValue() + "\r\n";
        }
        return responseHeaders;
    }
}
