package edu.yu.cs.com1320.project.impl;

import java.util.*;

//import edu.yu.cs.com1320.project.Comparator; //what is this? why did it auto import this? dsnt make any sense
import edu.yu.cs.com1320.project.Trie;

public class TrieImpl<Value> implements Trie<Value> {
    
    private final int size = 91;
    private Node<Value> root; // root of trie
    private Value valToDelete; //for delete method

    private class Node<Value> {
        private LinkedHashSet<Value> valSet = new LinkedHashSet<Value>();
        private Node<Value>[] links = new Node[size];

        private Node() {
        }
        // private Node(Value value) {
        //     this.valList.add(value);
        // }
    }

    public TrieImpl() { //no arg constructor
    }

    private String stringFixer(String str) {
        str = str.replaceAll("[^a-zA-Z0-9\\s]", "");
        str = str.toUpperCase();
        //System.out.println(str);
        return str;
    }

    @Override
    public void put(String key, Value val) {
        if (val == null || key == null) {
            return;
        }
        else {
            key = stringFixer(key);

            this.root = put(this.root, key, val, 0);
        }
    }

    private Node<Value> put(Node<Value> node, String key, Value val, int d) {
        if (node == null) { //create a new node
            Node<Value> newNode = new Node<Value>();
            node = newNode;
        }
        StringTokenizer st = new StringTokenizer(key);
        while (st.hasMoreTokens()) {
            String key2 = st.nextToken();

            if (d == key2.length()) { //we've reached the last node in the key, set the value for the key and return the node
                if (node.valSet == null) {
                    node.valSet = new LinkedHashSet<Value>();
                }
                if (node.valSet.contains(val)) { //checks for duplicates
                    continue;
                }
                node.valSet.add(val);
                d = 0;
                continue;
            }
            //proceed to the next node in the chain of nodes that forms the desired key
            char c = key2.charAt(d);
            node.links[c] = this.put(node.links[c], key2, val, d + 1);
        }
        return node;
    }

    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        List<Value> emptyList = new ArrayList<Value>();
        if (key == null) {
            return emptyList;
        }
        key = stringFixer(key);
        
        Node<Value> node = this.get(this.root, key, 0);
        if (node == null) {
            return emptyList;
        }
        //return (List<Value>)Arrays.asList((Value)node.val);
        //Collections.sort(node.valList, comparator);
        if (node.valSet == null) {
            return emptyList;
        }
        List<Value> list = new ArrayList<Value>(node.valSet);
        list.sort(comparator);
        return list;
    }

    private Node<Value> get(Node<Value> node, String key, int d) {
        if (node == null) { //link was null - return null, indicating a miss
            return null;
        }
        if (d == key.length()) { //we've reached the last node in the key, return the node
            return node;
        }
        char c = key.charAt(d); //proceed to the next node in the chain of nodes that forms the desired key
        return this.get(node.links[c], key, d + 1);
    }

    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if (prefix == null) {
            List<Value> list = new ArrayList<Value>();
            return list;
        }
        prefix = stringFixer(prefix);

        Set<Value> results = new HashSet<Value>();

        Node<Value> node = this.get(this.root, prefix, 0); //find node which represents the prefix

        if (node != null) { //collect keys under it
            this.collect(node, new StringBuilder(prefix), results);
        }
        
        List<Value> noDuplicateResults = new ArrayList<Value>(results);
        noDuplicateResults.sort(comparator);
        return noDuplicateResults;
    }

    private void collect(Node<Value> node, StringBuilder prefix, Set<Value> results) {
        if (node.valSet != null && !node.valSet.isEmpty()) { //if this node has a value, add it’s key to the list
            results.addAll(node.valSet);
        }
        for (char c = 0; c < this.size; c++) { //visit each non-null child/link
            if (node.links[c] != null) {
                prefix.append(c); //add child's char to the string
                this.collect(node.links[c], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1); //remove the child's char to prepare for next iteration
            }
        }
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if (prefix == null) {
            Set<Value> set = new HashSet<Value>();
            return set;
        }
        prefix = stringFixer(prefix);

        Set<Value> results = new HashSet<Value>();

        Node<Value> node = this.get(this.root, prefix, 0); //find node which represents the prefix

        if (node != null) { //collect and delete keys under it
            this.deleteAndCollect(node, new StringBuilder(prefix), results);
            node = null;
        }

        return results;
    }

    private void deleteAndCollect(Node<Value> node, StringBuilder prefix, Set<Value> results) {
        if (node.valSet != null && !node.valSet.isEmpty()) { //if this node has a value, add it’s key to the list
            results.addAll(node.valSet);
            node.valSet = null; //delete
        }
        for (char c = 0; c < this.size; c++) { //visit each non-null child/link
            if (node.links[c] != null) {
                prefix.append(c); //add child's char to the string
                this.deleteAndCollect(node.links[c], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1); //remove the child's char to prepare for next iteration
            }
        }
    }

    @Override
    public Set<Value> deleteAll(String key) {
        if (key == null) {
            Set<Value> set = new HashSet<Value>();
            return set;
        }
        key = stringFixer(key);

        Set<Value> results = new HashSet<Value>();
        
        this.root = deleteAll(this.root, key, 0, results);
        
        return results;
    }

    private Node<Value> deleteAll(Node<Value> node, String key, int d, Set<Value> results) {
        if (node == null) {
            return null;
        }
        if (d == key.length()) { //we're at the node to del - set the val to null
            results.addAll(node.valSet);
            node.valSet = null;
        }
        else { //continue down the trie to the target node
            char c = key.charAt(d);
            node.links[c] = this.deleteAll(node.links[c], key, d + 1, results);
        }
        if (node.valSet != null && !node.valSet.isEmpty()) { //this node has a val – do nothing, return the node
            return node;
        }	
        for (int c = 0; c < this.size; c++) { //remove subtrie rooted at node if it is completely empty
            if (node.links[c] != null) {
                return node; //not empty
            }
        }
        return null; //empty - set this link to null in the parent
    }

    @Override
    public Value delete(String key, Value val) {
        if (key == null || val == null) {
            return null;
        }
        key = stringFixer(key);

        this.valToDelete = null;

        this.root = delete(this.root, key, 0, val);

        val = this.valToDelete;
        this.valToDelete = null; //resets for garbage collection
        return val;
    }

    private Node<Value> delete(Node<Value> node, String key, int d, Value val) {
        if (node == null) {
            return null;
        }
        if (d == key.length()) { //we're at the node to delete - set the val to null
            if (node.valSet != null && !node.valSet.isEmpty()) {
                if (node.valSet.contains(val)) {
                    this.valToDelete = val;
                    node.valSet.remove(val);
                }
            }   
        }
        else { //continue down the trie to the target node
            char c = key.charAt(d);
            node.links[c] = this.delete(node.links[c], key, d+1, val);
        }
        if (node.valSet != null && !node.valSet.isEmpty()) { //this node has a val – do nothing, return the node
            return node;
        }
        for (int c = 0; c < this.size; c++) { //otherwise, check if subtrie rooted at x is completely empty
            if (node.links[c] != null) {
                return node; //not empty
            }
        }
        return null; //empty - set this link to null in the parent
    }
    
}