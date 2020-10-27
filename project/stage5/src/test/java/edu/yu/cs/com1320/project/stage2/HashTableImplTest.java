// package edu.yu.cs.com1320.project.stage2;

// import static org.junit.Assert.*;
// import org.junit.*;

// import edu.yu.cs.com1320.project.impl.HashTableImpl;

// public class HashTableImplTest {

//     @Test
//     public void test1() {
//         // HashTableImpl<String,Integer> hashTable = new HashTableImpl<String,Integer>();

//         // hashTable.put("Yonatan", 21);
//         // hashTable.put("Ariel", 17);
//         // hashTable.put("Elan", 14);
//         // hashTable.put("Talia", 9);
//         // hashTable.put("Ima", 48);
//         // hashTable.put("Aba", 54);

//         // hashTable.put("1",4);
//         // hashTable.put("2", 5);
//         // hashTable.put("3", 6);
//         // hashTable.put("4", 7);
//         // hashTable.put("5", 8);
//         // hashTable.put("6", 9);
//         // hashTable.put("7", 10);

//         // assertEquals(Integer.valueOf(21), hashTable.get("Yonatan"));
//         // assertEquals(Integer.valueOf(17), hashTable.get("Ariel"));
//         // assertEquals(Integer.valueOf(10), hashTable.get("7"));

//         HashTableImpl<String, Integer> hashTable = new HashTableImpl<>();
//         hashTable.put("Yonatan", 21);
//         hashTable.put("Ariel", 17);
//         hashTable.put("Elan", 14);
//         hashTable.put("Talia", 9);
//         hashTable.put("Ima", 48);
//         hashTable.put("Aba", 54);
//         hashTable.put("Guy A",4);
//         hashTable.put("Guy B", 5);
//         hashTable.put("Guy C", 6);
//         hashTable.put("Guy D", 7);
//         hashTable.put("Guy E", 8);
//         hashTable.put("Guy F", 9);
//         hashTable.put("Guy G", 10);
//         hashTable.put("Guy H", 11);
//         hashTable.put("Guy I", 12);
//         hashTable.put("Guy J", 13);
//         hashTable.put("Guy K", 14);
//         hashTable.put("Guy L", 15);
//         hashTable.put("Guy M", 16);
//         hashTable.put("Guy N", 17);
//         // System.out.println("\n"+hashTable.get("Yonatan"));
//         // System.out.println(hashTable.get("Ariel"));
//         // System.out.println(hashTable.get("Elan"));
//         // System.out.println(hashTable.get("Talia"));
//         // System.out.println(hashTable.get("Ima"));
//         // System.out.println(hashTable.get("Aba"));
//         // System.out.println();
//         // System.out.println(hashTable.get("Guy A"));
//         // System.out.println(hashTable.get("Guy B"));
//         // System.out.println(hashTable.get("Guy C"));
//         // System.out.println(hashTable.get("Guy D"));
//         // System.out.println(hashTable.get("Guy E"));
//         // System.out.println(hashTable.get("Guy F"));
//         // System.out.println(hashTable.get("Guy G"));
//         // System.out.println(hashTable.get("Guy H"));
//         // System.out.println(hashTable.get("Guy I"));
//         // System.out.println(hashTable.get("Guy J"));
//         // System.out.println(hashTable.get("Guy K"));
//         // System.out.println(hashTable.get("Guy L"));
//         // System.out.println(hashTable.get("Guy M"));
//         // System.out.println(hashTable.get("Guy N"));
//         // System.out.println(hashTable.get("Guy O"));
//         // System.out.println(hashTable.get("Guy P"));
//         // System.out.println(hashTable.get("Guy Q"));

//         assertEquals(Integer.valueOf(4), hashTable.get("Guy A"));
//         assertEquals(Integer.valueOf(5), hashTable.get("Guy B"));
//         assertEquals(Integer.valueOf(6), hashTable.get("Guy C"));
//         assertEquals(Integer.valueOf(7), hashTable.get("Guy D"));
//         assertEquals(Integer.valueOf(8), hashTable.get("Guy E"));
//         assertEquals(Integer.valueOf(9), hashTable.get("Guy F"));
//         assertEquals(Integer.valueOf(10), hashTable.get("Guy G"));
//         assertEquals(Integer.valueOf(11), hashTable.get("Guy H"));
//         assertEquals(Integer.valueOf(12), hashTable.get("Guy I"));
//         assertEquals(Integer.valueOf(13), hashTable.get("Guy J"));
//         assertEquals(Integer.valueOf(14), hashTable.get("Guy K"));
//         assertEquals(Integer.valueOf(15), hashTable.get("Guy L"));
//         assertEquals(Integer.valueOf(16), hashTable.get("Guy M"));
//         assertEquals(Integer.valueOf(17), hashTable.get("Guy N"));
//     }

//     @Test
//     public void test2() {
//         HashTableImpl<Integer,Integer> hashTable = new HashTableImpl<Integer,Integer>();

//         for (int i = 0; i<500; i++) {
//             hashTable.put(i,i);
//         }

//         // System.out.println("Size: " + hashTable.array.length + " ("+hashTable.size+")");
//         // System.out.println("Counter: " + hashTable.counter);

//         for (int i = 0; i<500; i++) {
//             //System.out.println("Value at "+i+": "+hashTable.get(i));
//             assertEquals(Integer.valueOf(i), hashTable.get(i));
//         }
//     }
// }