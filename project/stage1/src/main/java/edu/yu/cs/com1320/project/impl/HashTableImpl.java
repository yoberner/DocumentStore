package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.*;

//@SuppressWarnings("unchecked")
public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {

    private final static int size = 5;
    private LinkedList<Key, Value>[] array = new LinkedList[size]; //I understand this warning here. to correct it and fix it properly: the array should be made ype object and then casted into LinkedList in get and put methods.
    // private LinkedList<Key, Value>[] array1 = (LinkedList<Key, Value>[]) new LinkedList[size];
    // private Object[] array2 = new Object[size];

    @Override
    public Value get(Key k) {
        if ( k == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        int index = hashFunction(k);
        
        if (array[index] == null || array[index].isEmpty()) {
            return null;
        }

        if (array[index].contains(k)) {
            //search through code to find the value for that key
            return array[index].getValue(k);
        }
        return null;
    }

    @Override
    public Value put(Key k, Value v) {
        if (k == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        
        int index = hashFunction(k);

        if (v == null) {
            if (array[index] == null || array[index].isEmpty()) { //should i have the isEmpyty here also or no?? might be giving a NullPointer
                return null;
            }
            if (array[index].contains(k)) {
                Value vl = array[index].getValue(k);
                array[index].remove(k);
                return vl;
            }
            return null;
        }
        if (array[index] == null || array[index].isEmpty()) { //should i have the isEmpyty here also or no?? might be giving a NullPointer
            array[index] = new LinkedList<Key, Value>();
            array[index].add(k, v);
            return null;
        }
        if (array[index].contains(k)) {
            Value vl = array[index].getValue(k);
            array[index].remove(k); //IS THIS CORRECT!? IN ORDER TO REPLACE JUST DELETE THEN ADD!?!?!
            array[index].add(k, v);
            return vl;
        }
        array[index].add(k, v);
        return null;
    }

    private int hashFunction(Key k) {
        //int hash = Math.abs(k.hashCode()) % size;
        int hash = (k.hashCode() & 0x7fffffff) % size;
        return hash;
    }

    private class LinkedList<Key, Value> {
        private Node<Key, Value> head;
    
        private void add(Key k, Value v) {
            Node<Key, Value> nod = new Node<Key, Value>(k, v);
    
            if (this.head == null) {
                this.head = nod;
            }
            else {
                Node<Key, Value> current = head;
                while (current.next != null) {
                    current = current.next;
                }
                current.next = nod;
            }
        }
    
        private boolean isEmpty() {
            if (head == null) {
               return true;
            }
            else {
               return false;
            }
        }
    
        private boolean contains(Key k) {
           if (head == null) {
               return false;
            }
           //search through code comparing the current key to all the others
           Node<Key, Value> current = head;
           while (current != null) {
               if (current.key.equals(k)) {
                   return true;
                }
                current = current.next;
            }
            return false;
        }

        private Value getValue(Key k) {
            if (head == null) {
                return null;
            }

            Node<Key, Value> current = head;
            while (current != null) {
                if (current.key.equals(k)) {
                    return current.value;
                }
                current = current.next;
            }
            return null;
        }
    
        private void remove(Key k) {
            if (head==null) {
                return;
            }
            if (head.key.equals(k)) {
                head = head.next;
                return;
            }
    
            Node<Key, Value> current = head;
            while (current.next != null) {
                if (current.next.key.equals(k)) {
                    current.next = current.next.next;
                    return;
                }
                current = current.next;
            }
        }
    
        private class Node<Key, Value> {
            private Node<Key,Value> next;
            private Key key;
            private Value value;
            
            private Node(Key k, Value v) {
                if (k == null && v == null) {
                    throw new IllegalArgumentException("Error: Invalid Input: NULL!");
                }
                this.key = k;
                this.value = v;
            }
        }
    }
}