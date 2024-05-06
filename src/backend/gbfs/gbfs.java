package backend.gbfs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class gbfs {
    static class WordNode {
        String word;
        int cost;
        WordNode parent;

        WordNode(String word, int cost, WordNode parent) {
            this.word = word;
            this.cost = cost;
            this.parent = parent;
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
        PriorityQueue<WordNode> queue = new PriorityQueue<>(Comparator.comparingInt(node -> heuristic(node.word, endWord)));
        Set<String> visited = new HashSet<>();

        queue.offer(new WordNode(startWord, 0, null));
        visited.add(startWord);

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

            for (int i = 0; i < currentWord.length(); i++) {
                char[] charArray = currentWord.toCharArray();
                for (char c = 'a'; c <= 'z'; c++) {
                    charArray[i] = c;
                    String nextWord = new String(charArray);
                    if (wordList.contains(nextWord) && !visited.contains(nextWord) && nextWord.length() == endWord.length()) {
                        queue.offer(new WordNode(nextWord, currentNode.cost + 1, currentNode));
                        visited.add(nextWord);
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
