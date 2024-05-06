package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.HttpServer;

import backend.astar.astar;
import backend.gbfs.gbfs;
import backend.ucs.ucs;

public class Main {

    public static List<String> parseDictionary(String filePath) throws IOException {
        List<String> parsedList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            parsedList.add(line.trim());
        }
        reader.close();
        return parsedList;
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8000), 0);

        server.createContext("/findLadder", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                URI requestedUri = exchange.getRequestURI();
                String query = requestedUri.getRawQuery();
                Map<String, String> queryParams = parseQueryParams(query);

                String startWord = queryParams.get("startWord");
                String endWord = queryParams.get("endWord");
                String algorithm = queryParams.get("algorithm");

                if (startWord == null || endWord == null || algorithm == null) {
                    exchange.sendResponseHeaders(400, -1); 
                    return;
                }

                Set<String> wordList;
                try {
                    wordList = new HashSet<>(parseDictionary("backend/dictionary/words_alpha.txt"));
                } catch (IOException e) {
                    exchange.sendResponseHeaders(500, -1);
                    return;
                }

                Object result = null;
                List<String> ladder = null;
                int totalNodesVisited = 0;

                switch (algorithm.toLowerCase()) {
                    case "ucs":
                        result = ucs.findLadder(startWord, endWord, wordList);
                        ladder = ((ucs.Result)result).ladder;
                        totalNodesVisited = ((ucs.Result)result).totalNodesVisited;
                        break;
                    case "astar":
                        result = astar.findLadder(startWord, endWord, wordList);
                        ladder = ((astar.Result)result).ladder;
                        totalNodesVisited = ((astar.Result)result).totalNodesVisited;
                        break;
                    case "gbfs":
                        result = gbfs.findLadder(startWord, endWord, wordList);
                        ladder = ((gbfs.Result)result).ladder;
                        totalNodesVisited = ((gbfs.Result)result).totalNodesVisited;
                        break;
                    default:
                        exchange.sendResponseHeaders(400, -1); 
                        return;
                }

                StringBuilder jsonBuilder = new StringBuilder("{\"path\":[");
                for (int i = 0; i < ladder.size(); i++) {
                    jsonBuilder.append("\"").append(ladder.get(i)).append("\"");
                    if (i < ladder.size() - 1) {
                        jsonBuilder.append(",");
                    }
                }
                jsonBuilder.append("]");
                jsonBuilder.append(",\"totalNodesVisited\":").append(totalNodesVisited);
                jsonBuilder.append("}");
                String response = jsonBuilder.toString();
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); 
            }
        });

        server.start();
    }

    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
