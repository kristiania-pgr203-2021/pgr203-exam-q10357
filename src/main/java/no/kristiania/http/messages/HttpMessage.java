package no.kristiania.http.messages;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpMessage {
    protected String[] startLine;

    protected int responseCode;

    protected final Map<String, String> headers = new HashMap<>();

    public String messageBody;

    public HttpMessage() {
        startLine = null;
    }

    public HttpMessage(Socket socket) throws IOException {
        startLine = readLine(socket).split(" ");
        readHeadersToHaspMap(socket);
    }

    private static String readLine(Socket socket) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStream in = socket.getInputStream();

        int c;
        while ((c = in.read()) != -1 && c != '\r') {
            result.append((char)c);
        }
        in.read();
        return result.toString();
    }

    private void readHeadersToHaspMap(Socket socket) throws IOException {
        //In this method we will call readLine method
        //The Headers of our response will be put into a Hashpmap
        String headerLine;
        while(!(headerLine = readLine(socket)).isBlank()){
            int colonPos = headerLine.indexOf(":");
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();
            headers.put(headerName, headerValue);
        }
        //we can now retrieve header data through the created hashmap
    }

    protected static String readCharacters(Socket socket, int contentLength) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStream in = socket.getInputStream();

        for (int i = 0; i < contentLength; i++) {
            result.append((char) in.read());
        }

        return result.toString();
    }

    public int getContentLength() {
        //This method makes it possible to read message body, returns length
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        return contentLength;
    }

    public String[] getStartLine() {
        return startLine;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
