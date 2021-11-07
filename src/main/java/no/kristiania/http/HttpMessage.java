package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    private String[] startLineArray;
    private String startLine;

    public final HashMap<String, String> headers = new HashMap<>();
    public final Map<String, String> queries = new HashMap<>();
    public String messageBody;

    public HttpMessage(Socket socket) throws IOException {
        startLine = HttpMessage.readLine(socket);
        startLineArray = startLine.split(" ");
        readHeadersToHaspMap(socket);
        String requestTarget = getRequestTarget();

        //hvis vi har content length kan vi parse messagebody, hvis ikke kan den være tom
        // check if message has a body, that is for example post requests
        if (headers.containsKey("Content-Length")) {
            messageBody = HttpMessage.readCharacters(socket, getContentLength());
            setQueries(messageBody);
            // the queries from a GET request. example - /api?category=Sports&productName=Football
        } else if(requestTarget.contains("?")){
            setQueries(requestTarget);
        }
    }

    public HttpMessage(String startLine, String messageBody){
        this.startLine = startLine;
        this.messageBody = messageBody;
    }

    static String readLine(Socket socket) throws IOException {
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

    private static String readCharacters(Socket socket, int contentLength) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStream in = socket.getInputStream();

        for (int i = 0; i < contentLength; i++) {
            result.append((char) in.read());
        }

        return result.toString();
    }

    private void setQueries(String query) {
        if (query == null || query.isBlank()) {
            return;
        }
        if(query.contains("?")){
            query = query.substring(query.indexOf("?") + 1);
        }

        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('='); //finne hvor "=" er
            if(equalsPos != -1) {
                String parameterName = queryParameter.substring(0, equalsPos); //fra start til =
                String parameterValue = queryParameter.substring(equalsPos + 1);
                queries.put(parameterName, parameterValue);
            }
        }
    }

    public int getContentLength() {
        //This method makes it possible to read message body, returns length
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        return contentLength;
    }

    public String[] getStartLineArray() {
        return startLineArray;
    }

    public String getRequestTarget() {
        if(startLineArray.length > 1) {
            return startLineArray[1];
        }
        return "";
    }

    public void write(Socket socket) throws IOException {
        String responseText = startLine + "\r\n" +
                "Content-Length: " + messageBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                messageBody;

        socket.getOutputStream().write(responseText.getBytes());

    }
}
