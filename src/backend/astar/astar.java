package backend.astar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class astar {
    static class WordNode {
        String word;
        int gCost;
        int hCost; 
        WordNode parent;

        WordNode(String word, int gCost, int hCost, WordNode parent) {
            this.word = word;
            this.gCost = gCost;
            this.hCost = hCost;
            this.parent = parent;
        }

        int getTotalCost() {
            return gCost + hCost;
        }
    }

    public static class Result {
        public List<String> ladder;
        public int totalNodesVisited;

        public Result(List<String> ladder, int totalNodesVisited) {
            this.ladder = ladder;
            this.totalNodesVisited = totalNodesVisited;
        }
    }

    public static String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
    
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();
    
            return content.toString();
        } else {
            return "GET request not worked";
        }
    }

    public static Result findLadder(String startWord, String endWord, Set<String> wordList) {
        int totalNodesVisited = 0;
        List<String> ladder = new ArrayList<>();
        PriorityQueue<WordNode> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.getTotalCost()));
        Map<String, Integer> gCostMap = new HashMap<>(); 
        Set<String> visited = new HashSet<>();
    
        WordNode startNode = new WordNode(startWord, 0, heuristic(startWord, endWord), null);
        queue.offer(startNode);
        gCostMap.put(startWord, 0);
    
        while (!queue.isEmpty()) {
            WordNode currentNode = queue.poll();
            String currentWord = currentNode.word;
            totalNodesVisited++;
    
            if (currentWord.equals(endWord)) {
                while (currentNode != null) {
                    ladder.add(0, currentNode.word);
                    currentNode = currentNode.parent;
                }
                return new Result(ladder, totalNodesVisited);
            }
    
            visited.add(currentWord);
    
            for (int i = 0; i < currentWord.length(); i++) {
                char[] charArray = currentWord.toCharArray();
                for (char c = 'a'; c <= 'z'; c++) {
                    charArray[i] = c;
                    String nextWord = new String(charArray);
    
                    if (wordList.contains(nextWord) && !visited.contains(nextWord) && nextWord.length() == endWord.length()) {
                        int newGCost = currentNode.gCost + 1;
                        if (!gCostMap.containsKey(nextWord) || newGCost < gCostMap.get(nextWord)) {
                            gCostMap.put(nextWord, newGCost);
                            WordNode nextNode = new WordNode(nextWord, newGCost, heuristic(nextWord, endWord), currentNode);
                            queue.offer(nextNode);
                        }
                    }
                }
            }
        }
    
        return null;
    }

    static int heuristic(String word1, String word2) {
        int count = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                count++;
            }
        }
        return count;
    }
}
