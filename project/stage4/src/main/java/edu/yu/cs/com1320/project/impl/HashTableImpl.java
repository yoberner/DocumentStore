package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.*;

//@SuppressWarnings("unchecked")
public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {
    private int size = 5;
    private LinkedList<Key, Value>[] array = new LinkedList[size]; //I understand this warning here. to correct it and fix it properly: the array should be made ype object and then casted into LinkedList in get and put methods.
    // private LinkedList<Key, Value>[] array1 = (LinkedList<Key, Value>[]) new LinkedList[size];
    // private Object[] array2 = new Object[size];
    private int counter;

    public HashTableImpl() { //no arg constructor
    }

    @Override
    public Value get(Key k) {
        if ( k == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        int index = hashFunction(k);
        
        if (this.array[index] == null || this.array[index].isEmpty()) {
            return null;
        }

        if (this.array[index].contains(k)) {
            //search through code to find the value for that key
            return this.array[index].getValue(k);
        }
        return null;
    }

    @Override
    public Value put(Key k, Value v) {
        if (k == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }

        // if (this.array.length <= this.counter/4) { //array doubling
        //     arrayDouble();
        // }
        
        int index = hashFunction(k);

        if (v == null) {
            if (this.array[index] == null || this.array[index].isEmpty()) { //should i have the isEmpty here also or no?? might be giving a NullPointer
                return null;
            }
            if (this.array[index].contains(k)) {
                Value vl = this.array[index].getValue(k);
                this.array[index].remove(k);
                counter--;
                return vl;
            }
            return null;
        }

        if (this.array.length <= this.counter/4) { //array doubling
            arrayDouble();
            index = hashFunction(k);
        }

        if (this.array[index] == null || this.array[index].isEmpty()) { //should i have the isEmpty here also or no?? might be giving a NullPointer
            this.array[index] = new LinkedList<Key, Value>();
            this.array[index].add(k, v);
            counter++;
            return null;
        }
        if (this.array[index].contains(k)) {
            Value vl = this.array[index].getValue(k);
            this.array[index].remove(k); //IS THIS CORRECT!? IN ORDER TO REPLACE JUST DELETE THEN ADD!?!?!
            this.array[index].add(k, v);
            return vl;
        }
        this.array[index].add(k, v);
        counter++;
        return null;
    }

    private void arrayDouble() {
        this.size = this.size * 2;
        LinkedList<Key, Value>[] oldArray = this.array;
        LinkedList<Key, Value>[] newArray = new LinkedList[size];
        this.array = newArray;
        this.counter = 0;
        // for (int i = 0; i < newArray.length; i++) {
        //     newArray[i] = new LinkedList<Key, Value>();
        // }
        for (int i = 0; i < oldArray.length; i++) {
            LinkedList<Key, Value> list = oldArray[i];
            if (list == null || list.isEmpty()) { //makes sure will not throw null pointer if nothing was hashed to that list
                continue;
            }
            LinkedList<Key, Value>.Node<Key, Value> node = list.head;
            while (node != null) {
                put(node.key, node.value);
                node = node.next;
            }
        }
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
                Node<Key, Value> current = this.head;
                while (current.next != null) {
                    current = current.next;
                }
                current.next = nod;
            }
        }
    
        private boolean isEmpty() {
            if (this.head == null) {
               return true;
            }
            return false;
        }
    
        private boolean contains(Key k) {
           if (this.head == null) {
               return false;
            }
           //search through code comparing the current key to all the others
           Node<Key, Value> current = this.head;
           while (current != null) {
               if (current.key.equals(k)) {
                   return true;
                }
                current = current.next;
            }
            return false;
        }

        private Value getValue(Key k) {
            if (this.head == null) {
                return null;
            }

            Node<Key, Value> current = this.head;
            while (current != null) {
                if (current.key.equals(k)) {
                    return current.value;
                }
                current = current.next;
            }
            return null;
        }
    
        private void remove(Key k) {
            if (this.head == null) {
                return;
            }
            if (this.head.key.equals(k)) {
                this.head = this.head.next;
                return;
            }
    
            Node<Key, Value> current = this.head;
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

    private void printMatziv() { //for debugging - prints out array with all variables and indexes
        System.out.println();
        System.out.println("Counter: " + this.counter);
        System.out.println("Size: " + this.size + " ("+this.array.length+")");
        System.out.println();
        System.out.println("Array:");
        
        for (int i = 0; i < this.array.length; i++) {
            if (this.array[i] == null || this.array[i].isEmpty()) {
                continue;
            }

            System.out.println("Index:["+i+"]: ");
            LinkedList<Key, Value> list = this.array[i];
            LinkedList<Key, Value>.Node<Key, Value> node = list.head;
            while (node != null) {
                //System.out.print(get(node.key)+", ");
                System.out.print(node.value+", ");
                node = node.next;
            }
            System.out.println();
        }
    }

}