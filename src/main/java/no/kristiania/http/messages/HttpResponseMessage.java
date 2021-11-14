package no.kristiania.http.messages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseMessage extends HttpMessage {
    private final HashMap<Integer, String> responseCodeTexts = new HashMap<>() {{
        put(200, "OK");
        put(303, "See Other");
        put(400, "Bad Request");
        put(404, "Not Found");
        put(500, "Internal Server Error");
    }};

    public HttpResponseMessage(int responseCode, String location, long cookieId){
        this.responseCode = responseCode;
        this.headers.put("Set-Cookie", "cookieName="+cookieId);
        this.headers.put("Location", location);
        System.out.println(getResponseHeaders());
    }

    public HttpResponseMessage(int responseCode, String messageBody) {
        System.out.println(responseCode);
        this.responseCode = responseCode;
        if(responseCode == 303){
            String location = messageBody;
            this.headers.put("Location", location);
            return;
        }
        this.messageBody = messageBody;
        this.headers.put("Content-Type", "text/html");
        this.headers.put("Content-Length", String.valueOf(messageBody.length()));
        this.headers.put("Connection", "close");
    }


    public HttpResponseMessage(int responseCode, String contentType, String messageBody) {
        this.responseCode = responseCode;
        this.headers.put("Content-Type", contentType);
        this.headers.put("Content-Length", String.valueOf(messageBody.length()));
        this.headers.put("Connection", "close");
        this.messageBody = messageBody;
    }


    public String getResponseText() throws IOException {
        return "HTTP/1.1 " + responseCode + " " + getResponseCodeText(responseCode) + "\r\n" +
                getResponseHeaders() +
                "\r\n" +
                messageBody;
    }

    private String getResponseCodeText(int responseCode) throws IOException {
        if (responseCodeTexts.containsKey(responseCode)) {
            setResponseCode(responseCode);
            return responseCodeTexts.get(responseCode);
        }

        throw new IOException("The response code " + responseCode + " does not exist.");
    }

    private String getResponseHeaders() {
        String responseHeaders = "";
        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHeaders += header.getKey() + ": " + header.getValue() + "\r\n";
        }
        return responseHeaders;
    }

    public Map<String, String> getHeaders(){
        return headers;
    }
    public int getResponseCode() {
        return responseCode;
    }

}
