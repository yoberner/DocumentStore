package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.*;

public class StackImpl<T> implements Stack<T> {
    private Node<T> top;
    private int counter;

    public StackImpl() { //no arg constructor
    }

    @Override
    public void push(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        Node<T> node = new Node<T>(element);
        node.next = this.top;
        this.top = node;
        counter++;
    }

    @Override
    public T pop() {
        if (this.isEmpty()) {
            return null;
        }
        T data = this.top.data;
        this.top = this.top.next;
        counter--;
        return data;
    }

    @Override
    public T peek() {
        if (this.isEmpty()) {
            return null;
        }
        return this.top.data;
    }

    @Override
    public int size() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.counter;
    }

    private boolean isEmpty() {
        if (this.top == null) {
            return true;
        }
        return false;
    }

    private class Node<T> {
        private Node<T> next;
        private T data;

        private Node(T d) {
            if (d == null) {
                throw new IllegalArgumentException("Error: Invalid Input: NULL!");
            }
            this.data = d;
        }
    }
    
}