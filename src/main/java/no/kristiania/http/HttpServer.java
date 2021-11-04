package no.kristiania.http;

import no.kristiania.http.controllers.HttpController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);


    //properties
    private final ServerSocket serverSocket;
    private Path rootDirectory;

    //Instance variables for response
    //Set default contentType as text/html
    private String contentType;
    private String text = "";
    private int responseCode;
    private List<String> categories = new ArrayList<>();
    private HttpMessage requestMessage;

    private HashMap<String, HttpController> controllers = new HashMap<>();

    // Data access objects

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        //start a new thread for handling http requests
    }

    public void start(){
        Thread thread = new Thread(this::handleClients);
        thread.start();
    }


    private void handleClients(){
        try{
            while(true){
                handleClient();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleClient() {
        try{
            // Make socket open for accepting client request
            Socket clientSocket = serverSocket.accept();

            // method that handles the request target
            handleRequest(clientSocket);

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // handle the request target
    private void handleRequest(Socket clientSocket) throws IOException {
        requestMessage = new HttpMessage(clientSocket);
        String requestTarget = requestMessage.getRequestTarget();
        System.out.println(requestTarget);

        text = "";
        //default responseCode to 200 OK
        responseCode = 200;

        if(requestTarget.endsWith(".html"))
        {
            contentType = "text/html";
            searchDirectoryAndSetFileResponse();
        }
        else if(requestTarget.endsWith(".css")){
            contentType = "stylesheet";
            searchDirectoryAndSetFileResponse();
        }
        else if (requestTarget.startsWith("/api")) {
            handleApiRequestTarget(clientSocket);
        } else {
            responseCode = 404;
            text = "Invalid request: " + requestTarget;
        }

        writeResponse(clientSocket, responseCode, text, contentType);
    }

    //Search directory, return false if file is not found/ rootDirectory = null
    private void searchDirectoryAndSetFileResponse() throws IOException {
        String requestedFile = requestMessage.getRequestTarget().substring(1);

        if(rootDirectory != null && Files.exists(rootDirectory.resolve(requestedFile))){
            text = Files.readString((rootDirectory.resolve(requestedFile)));
            return;
        }

        responseCode = 404;
        text = String.format( "Html file not found %s", requestedFile);
    }


    private void handleApiRequestTarget(Socket socket) throws IOException {
        // example apiRequestTarget -> /api/products/Books
        String requestTarget = requestMessage.getRequestTarget();
        String apiTarget = requestMessage.getRequestTarget().split("/")[2];
        if(apiTarget.contains("?")){
            apiTarget = apiTarget.substring(0, apiTarget.indexOf("?"));
        }

        switch(apiTarget){
            case "questions":
                connectController(socket);
                break;
            case "products":
                break;
            case "newProduct":
                break;
            //If none, request is not found, responseCode set to 404
            default: responseCode = 404;
        }
    }

    private void connectController(Socket socket) throws IOException {
        String requestTarget = requestMessage.getRequestTarget();
        if(controllers.containsKey(requestTarget)){
            HttpMessage response = controllers.get(requestTarget).handle(requestMessage);
            response.write(socket);
        }else{
            text = "Error";
        }
    }



    // writes response
    private void writeResponse(Socket clientSocket, int responseCode, String text, String contentType) throws IOException {
        String responseText = String.format("HTTP/1.1 %d \r\n"+
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + text.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                text, responseCode);

        clientSocket.getOutputStream().write(responseText.getBytes());
    }

    public int getActualPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
        //System.out.println(rootDirectory);
    }

    public void addController(String path, HttpController controller) {
        if(!controllers.containsKey(path)){
            controllers.put(path, controller);
        }
    }
}
