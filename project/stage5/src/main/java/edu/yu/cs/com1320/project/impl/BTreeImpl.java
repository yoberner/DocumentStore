package edu.yu.cs.com1320.project.impl;

import java.io.IOException;
import java.util.*;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {

    private static final int MAX = 6; // max children per B-tree node = MAX-1 (must be an even number and greater than 2)
    private Node root; // root of the B-tree
    private Node leftMostExternalNode; // for linkedList
    private int height; // height of the B-tree
    private int size; // number of key-value pairs in the B-tree
    private PersistenceManager<Key, Value> pm;

    public BTreeImpl() { // Initializes an empty B-tree.
        this.root = new Node(0);
        this.leftMostExternalNode = this.root; // for linkedList
    }

    @Override
    public void moveToDisk(Key k) throws Exception {
        //when being kicked out: remove from minHeap and only keep reference to location on disk, not memory
        if (k == null) {
            throw new IllegalArgumentException("Error: Key is null!");
        }
        Value val = this.get(k);
        if (val == null) {
            return;
        }
        this.pm.serialize(k, val);
        this.put(k, null);
    }

    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {
        if (pm == null) {
            throw new IllegalArgumentException("Error: Persistence Manager is null");
        }
        this.pm = pm;
    }

    @Override
    public Value get(Key k) {
        // if doc is in disk, then go get it. and if too much memory now, then delete leastUsedDocs (also update useTime and delete disk location) check when null - if on disk or not, will determine if exists or no - check piazza

        if (k == null) {
            throw new IllegalArgumentException("argument to get() is null");
        }
        Entry entry = this.get(this.root, k, this.height);
        
        if (entry != null) {
            
            if (entry.val == null) {
                
                try {
                    Value val = pm.deserialize(k);

                    if (val == null) { // case1: it dsnt exist - was deleted
                        return null;
                    }
                    else { // case2: on disk - then deserialize and put it back
                        this.put(k, val);
                        return val;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
            }
            
            else {
                return (Value) entry.val;
            }
        }
        return null;
    }

    private Entry get(Node currentNode, Key key, int height) {
        Entry[] entries = currentNode.entries;

        //current node is external (i.e. height == 0)
        if (height == 0) {
            for (int j = 0; j < currentNode.entryCount; j++) {
                if (isEqual(key, entries[j].key)) {
                    //found desired key. Return its value
                    return entries[j];
                }
            }
            return null; //didn't find the key
        }

        //current node is internal (height > 0)
        else {
            for (int j = 0; j < currentNode.entryCount; j++) {
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be in the subtree below the current entry),
                //then recurse into the current entry’s child
                if (j + 1 == currentNode.entryCount || less(key, entries[j+1].key)) {
                    return this.get(entries[j].child, key, height - 1);
                }
            }
            return null; //didn't find the key
        }
    }

    @Override
    public Value put(Key k, Value v) {
        //if doc is in disk, then go get it. and if too much memory now, then delete leastUsedDocs (also update useTime and delete disk location)
        
        if (k == null) {
            throw new IllegalArgumentException("argument key to put() is null");
        }
        //if the key already exists in the b-tree, simply replace the value
        Entry alreadyThere = this.get(this.root, k, this.height);
        
        if (alreadyThere != null) {

            if (alreadyThere.val == null) {
                
                try {
                    Value val = pm.deserialize(k);

                    if (val == null) { // case1: it dsnt exist, was deleted - so set new value
                        alreadyThere.val = v;
                        return null;
                    }
                    else { // case2: on disk - replace it
                        alreadyThere.val = v;
                        return val;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
            }

            else {
                Value oldVal = (Value) alreadyThere.val;
                alreadyThere.val = v;
                return oldVal;
            }
        }

        //CHECK THAT ITS NOT ON DISK IF NULL - if it is, then bring back to memory set it to value and delete from disk-!!!!!!!!!!!!!

        Node newNode = this.put(this.root, k, v, this.height);
        this.size++;
        if (newNode == null) { //if no split - nothing to do
            return null;
        }

        //split the root:
        //Create a new node to be the root.
        //Set the old root to be new root's first entry.
        //Set the node returned from the call to put to be new root's second entry
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        this.height++; //a split at the root always increases the tree height by 1

        return null;
    }

    private Node put(Node currentNode, Key key, Value val, int height) {
        int j;
        Entry newEntry = new Entry(key, val, null);

        //external node
        if (height == 0) {
            //find index in currentNode’s entry[] to insert new entry
            //we look for key < entry.key since we want to leave j
            //pointing to the slot to insert the new entry, hence we want to find
            //the first entry in the current node that key is LESS THAN
            for (j = 0; j < currentNode.entryCount; j++) {
                if (less(key, currentNode.entries[j].key)) {
                    break;
                }
            }
        }

        // internal node
        else {
            //find index in node entry array to insert the new entry
            for (j = 0; j < currentNode.entryCount; j++) {
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be added to the subtree below the current entry),
                //then do a recursive call to put on the current entry’s child
                if ((j+1 == currentNode.entryCount) || less(key, currentNode.entries[j+1].key)) {
                    //increment j (j++) after the call so that a new entry created by a split
                    //will be inserted in the next slot
                    Node newNode = this.put(currentNode.entries[j++].child, key, val, height-1);
                    if (newNode == null) {
                        return null;
                    }
                    //if the call to put returned a node, it means I need to add a new entry to the current node
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }

        //shift entries over one place to make room for new entry
        for (int i = currentNode.entryCount; i > j; i--) {
            currentNode.entries[i] = currentNode.entries[i-1]; //shifts element over one (to make room for new entry)
        }
        //add new entry
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;

        //check to see if we need to split (if filled in last spot in node)
        if (currentNode.entryCount < BTreeImpl.MAX) {
            //no structural changes needed in the tree
            //so just return null
            return null;
        }
        else {
            //will have to create new entry in the parent due
            //to the split, so return the new node, which is
            //the node for which the new entry will be created
            return this.split(currentNode, height);
        }
    }

    private Node split(Node currentNode, int height) {
        Node newNode = new Node(BTreeImpl.MAX / 2);
        //by changing currentNode.entryCount, we will treat any value
        //at index higher than the new currentNode.entryCount as if
        //it doesn't exist
        currentNode.entryCount = BTreeImpl.MAX / 2;
        //copy top half of h into t
        for (int j = 0; j < BTreeImpl.MAX / 2; j++) {
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
        }
        //external node
        if (height == 0) { //changing the list
            newNode.setNext(currentNode.getNext());
            newNode.setPrevious(currentNode);
            currentNode.setNext(newNode);
        }
        return newNode;
    }

    // comparison functions - make Comparable instead of Key to avoid casts
    private static boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    private static boolean isEqual(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }

    // B-tree node data type
    private static final class Node {
        private int entryCount; // number of entries
        private Entry[] entries = new Entry[BTreeImpl.MAX]; // the array of children
        private Node next;
        private Node previous;

        // create a node with k entries
        private Node(int k) {
            this.entryCount = k;
        }

        //the following methods are only for the linkedList:

        private void setNext(Node next) {
            this.next = next;
        }

        private Node getNext() {
            return this.next;
        }

        private void setPrevious(Node previous) {
            this.previous = previous;
        }

        private Node getPrevious() {
            return this.previous;
        }

        private Entry[] getEntries() {
            return Arrays.copyOf(this.entries, this.entryCount);
        }

    }

    // internal nodes: only use key and child
    // external nodes: only use key and value
    private static class Entry {
        private Comparable key; //either points to value (could be null) or another node (when height not 0)
        private Object val;
        private Node child;

        private Entry(Comparable key, Object val, Node child) {
            this.key = key;
            this.val = val;
            this.child = child;
        }

        private Object getValue() {
            return this.val;
        }

        private Comparable getKey() {
            return this.key;
        }
    }


    //methods used for linkedList:

    private void delete(Key key) {
        put(key, null);
    }

    /**
     * Returns true if this symbol table is empty.
     *
     * @return {@code true} if this symbol table is empty; {@code false} otherwise
     */
    private boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * @return the number of key-value pairs in this symbol table
     */
    private int size() {
        return this.size;
    }

    /**
     * @return the height of this B-tree
     */
    private int height() {
        return this.height;
    }

    /**
     * returns a list of all the entries in the Btree, ordered by key
     * 
     * @return
     */
    private ArrayList<Entry> getOrderedEntries() { //this uses the optional linked list (to make things easier)
        Node current = this.leftMostExternalNode;
        ArrayList<Entry> entries = new ArrayList<>();
        while (current != null) {
            for (Entry e : current.getEntries()) {
                if (e.val != null) {
                    entries.add(e);
                }
            }
            current = current.getNext();
        }
        return entries;
    }

    private Entry getMinEntry() { //for linked list
        Node current = this.leftMostExternalNode;
        while (current != null) {
            for (Entry e : current.getEntries()) {
                if (e.val != null) {
                    return e;
                }
            }
        }
        return null;
    }

    private Entry getMaxEntry() { //for linked list
        ArrayList<Entry> entries = this.getOrderedEntries();
        return entries.get(entries.size() - 1);
    }

}