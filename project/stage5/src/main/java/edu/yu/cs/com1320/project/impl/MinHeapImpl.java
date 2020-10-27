package edu.yu.cs.com1320.project.impl;

import java.util.*;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl<E extends Comparable> extends MinHeap<E> {

    public MinHeapImpl() { //no arg constructor
        Map<E,Integer> map = new HashMap<E, Integer>();
        this.elementsToArrayIndex = map;

        this.elements = (E[]) new Comparable[10]; //(E[]) new Object[5]
    }

    @Override
    public void reHeapify(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Error: Invalid Input: Null!");
        }
        int i = getArrayIndex(element);
        this.upHeap(i);
        this.downHeap(i);
    }

    @Override
    protected int getArrayIndex(E element) {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("Error: heap is empty");
        }
        Integer i = this.elementsToArrayIndex.get(element);
        if (i == null) {
            throw new IllegalArgumentException("Error: this element is null");
        }
        return i;
    }

    @Override
    protected void doubleArraySize() { //do I have to update map here??????
        if (this.elements == null) {
            return;
        }
        this.elements = Arrays.copyOf(this.elements, this.elements.length * 2);
    }


    @Override
    protected boolean isEmpty() {
        return this.count == 0;
    }

    /**
     * is elements[i] > elements[j]?
     */
    @Override
    protected boolean isGreater(int i, int j) {
        return this.elements[i].compareTo(this.elements[j]) > 0;
    }

    /**
     * swap the values stored at elements[i] and elements[j]
     */
    @Override
    protected void swap(int i, int j) {
        E temp = this.elements[i];
        this.elements[i] = this.elements[j];
        this.elements[j] = temp;

        this.elementsToArrayIndex.put(elements[i], i);
        this.elementsToArrayIndex.put(elements[j], j);
    }

    /**
     *while the key at index k is less than its
     *parent's key, swap its contents with its parent’s
     */
    @Override
    protected void upHeap(int k) {
        while (k > 1 && this.isGreater(k / 2, k)) {
            this.swap(k, k / 2);
            k = k / 2;
        }

        this.elementsToArrayIndex.put(elements[k], k);
    }

    /**
     * move an element down the heap until it is less than
     * both its children or is at the bottom of the heap
     */
    @Override
    protected void downHeap(int k) {
        while (2 * k <= this.count) {
            //identify which of the 2 children are smaller
            int j = 2 * k;
            if (j < this.count && this.isGreater(j, j + 1)) {
                j++;
            }
            //if the current value is < the smaller child, we're done
            if (!this.isGreater(k, j)) {
                break;
            }
            //if not, swap and continue testing
            this.swap(k, j);
            k = j;
        }

        this.elementsToArrayIndex.put(elements[k], k);
    }

    @Override
    public void insert(E x) {
        // double size of array if necessary
        if (this.count >= this.elements.length - 1) {
            this.doubleArraySize();
        }
        //add x to the bottom of the heap
        this.elements[++this.count] = x;
        
        this.elementsToArrayIndex.put(x, this.count);

        //percolate it up to maintain heap order property
        this.upHeap(this.count);
    }

    @Override
    public E removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        E min = this.elements[1];
        //swap root with last, decrement count
        this.swap(1, this.count--);
        //move new root down as needed
        this.downHeap(1);
        this.elements[this.count + 1] = null; //null it to prepare for GC

        this.elementsToArrayIndex.remove(min);

        return min;
    }

}