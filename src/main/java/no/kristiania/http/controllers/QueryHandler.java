package no.kristiania.http.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QueryHandler {
    //Static method for treating queries
    public static Map<String, String> handleQueries(Map<String, String> queries){
       queries.forEach((k, v) -> queries.put(k, v.replaceAll("\\+", " ")));
       queries.forEach((k, v) -> {
           try {
               queries.put(k, decode(v));
           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           }
       });

       return queries;
    }


    public static String decode(String s) throws UnsupportedEncodingException {
        String decoded = URLDecoder.decode(s, StandardCharsets.UTF_8.toString());
        return decoded;
    }

    public static boolean checkAllQueries(Map<String, String> queries) {
        for(String s : queries.values()){
            if(s.length() == 0){
                return false;
            }
        }
        return true;
    }

    public static Integer tryParse(String string) {
        try {
            return Integer.parseInt(string);
        }catch (NumberFormatException ex){
            return null;
        }
    }
}
