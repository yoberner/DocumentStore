package edu.yu.cs.com1320.project.stage4;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.yu.cs.com1320.project.impl.MinHeapImpl;

public class MinHeapImplTest {
    MinHeapImpl<Integer> heap = new MinHeapImpl<Integer>();

    @Test
    public void test1() {
        heap.insert(10);
        heap.insert(20);
        heap.insert(15);
        heap.insert(7);
        heap.insert(25);
        assertEquals(Integer.valueOf(7), heap.removeMin()); 
        heap.reHeapify(15);
        heap.insert(3);
        heap.reHeapify(10);
        assertEquals(Integer.valueOf(3), heap.removeMin());
        heap.insert(2);
        heap.reHeapify(2);
        assertEquals(Integer.valueOf(2), heap.removeMin());
    }
}