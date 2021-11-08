package no.kristiania.http.messages;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestMessage extends HttpMessage {
    public final Map<String, String> queries = new HashMap<>();

    public HttpRequestMessage(Socket socket) throws IOException {
        super(socket);
        String requestTarget = getRequestTarget();

        //hvis vi har content length kan vi parse messagebody, hvis ikke kan den være tom
        if (headers.containsKey("Content-Length")) {
            messageBody = readCharacters(socket, getContentLength());
            setQueries(messageBody);
        } else if(requestTarget.contains("?")){
            setQueries(requestTarget);
        }
    }

    private void setQueries(String query) {
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('='); //finne hvor "=" er
            String parameterName = queryParameter.substring(0, equalsPos); //fra start til =
            String parameterValue = queryParameter.substring(equalsPos + 1);
            queries.put(parameterName, parameterValue);
        }
    }

    public String getRequestTarget() {
        if(startLine.length > 1){
            return startLine[1];
        }
        return "";
    }
}
