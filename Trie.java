import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Trie implementation using data structures to mimic tree structure
 */
public class Trie {
    private Node root = new Node(null, "!");
    private HashMap<String, Node> answer = new HashMap<>();

    public void addNode(String name) {
        Node current = root;
        StringBuilder soFar = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            soFar.append(c);
            if (!current.children.containsKey(c)) {
                current.children.put(c, new Node(current, soFar.toString()));
                answer.put(soFar.toString(), current.children.get(c));
            }
            current = current.children.get(c);
        }
        while (current.parent != null) {
            if (name.contains(current.value))
                current.words.add(name);
            current = current.parent;
        }
    }

    public LinkedList<String> prefix(String str) {
        return answer.get(str).words;
    }

    private class Node {
        Node parent;
        Map<Character, Node> children = new HashMap<>();
        String value;
        LinkedList<String> words;  // words with prefix

        Node(Node parent, String value) {
            this.parent = parent;
            children = new HashMap<>();
            this.value = value;
            this.words = new LinkedList<>();
        }
    }
}
