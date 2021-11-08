package no.kristiania.http.controllers;

import java.util.Map;

public class QueryHandler {
    //Static method for treating queries
    public static Map<String, String> handleQueries(Map<String, String> queries){
       queries.forEach((k, v) -> queries.put(k, v.replaceAll("\\+", " ")));

       return queries;
    }
}
