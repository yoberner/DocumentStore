package edu.yu.cs.com1320.project.stage3;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;

import edu.yu.cs.com1320.project.impl.TrieImpl;

public class TrieImplTest {

    TrieImpl<Integer> testTrie = new TrieImpl<Integer>();
    Comparator<Integer> comparator = (Integer int1, Integer int2) -> int1.compareTo(int2);

    TrieImpl<Integer> testTrie2 = new TrieImpl<Integer>();

    @Test
    public void test1() {

        testTrie.put("FIVE", 5);
        testTrie.put("four", 4);
        testTrie.put("forest", 49);
        testTrie.put("FIVE", 5); //duplicate test

        testTrie.put("ThE", 50);
        testTrie.put("the%%^&*re", 51);
        testTrie.put("then@!?", 52);

        List<Integer> testList1 = new ArrayList<Integer>();
        //testList1.add(5);
        testList1.add(4);
        testList1.add(49);
        testList1.sort(comparator);
        assertEquals(testList1, testTrie.getAllWithPrefixSorted("fo", comparator));

        List<Integer> testList2 = new ArrayList<Integer>();
        testList2.add(50);
        testList2.add(51);
        testList2.add(52);
        assertEquals(testList2, testTrie.getAllWithPrefixSorted("TH", comparator));

        List<Integer> testList3 = new ArrayList<Integer>();
        testTrie.put("five", 6);
        
        testList3.add(6);
        testList3.add(5);
        testList3.sort(comparator);
        assertEquals(testList3, testTrie.getAllSorted("FiVe", comparator));
        assertEquals(testList3, testTrie.getAllWithPrefixSorted("fi", comparator));


        assertEquals(new HashSet<>(testList2), testTrie.deleteAllWithPrefix("tH"));
        assertEquals(new ArrayList<>(), testTrie.getAllWithPrefixSorted("th", comparator));

        assertEquals(new HashSet<>(testList3), testTrie.deleteAll("f&*(*&^%$#@ive!!!...,,,"));
        assertEquals(new ArrayList<>(), testTrie.getAllSorted("five", comparator));

        assertEquals(Integer.valueOf(49), testTrie.delete("FOREST", 49));
        assertEquals(new ArrayList<>(), testTrie.getAllSorted("foresT", comparator));
        assertEquals(null, testTrie.delete("FOREST", 49));

        
        List<Integer> falseList = testTrie.getAllWithPrefixSorted("ghfdsdcfvbhgtred", comparator);
        List<Integer> emptyList = new ArrayList<Integer>();

        assertEquals(emptyList, falseList);
        assertEquals(emptyList, testTrie.getAllSorted("gtrdfvbgtrdfvgtrdftred", comparator));
        assertEquals(null, testTrie.delete("gtrdfvbgtrdfvgtrdftred", 7654));
        assertEquals(new HashSet<Integer>(), testTrie.deleteAll("gtrdfvbgtrdfvgtrdfdsftred"));
        assertEquals(new HashSet<Integer>(), testTrie.deleteAllWithPrefix("gtrdfvbgtrasdfewsdfvgtrdftred"));
    }

    @Test
    public void searchTest() {
        testTrie2.put("random test5 random!!!???><", 5);
        testTrie2.put("more random test6!!!???><", 6);
        //testTrie2.put("random more random more random more random", 7);

        List<Integer> list = testTrie2.getAllSorted("random", comparator);
        List<Integer> expectedList = new ArrayList<Integer>();
        expectedList.add(5);
        expectedList.add(6);
        assertEquals(expectedList, list);

        List<Integer> expectedList2 = new ArrayList<Integer>();
        expectedList2.add(5);
        expectedList2.add(6);
        assertEquals(expectedList2, testTrie2.getAllWithPrefixSorted("ran", comparator));
    }

}