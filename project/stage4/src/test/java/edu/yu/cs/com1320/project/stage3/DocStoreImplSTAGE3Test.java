package edu.yu.cs.com1320.project.stage3;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URI;
import java.util.*;

import org.junit.*;

import edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;

public class DocStoreImplSTAGE3Test {

    DocumentStoreImpl store = new DocumentStoreImpl();
    DocumentStoreImpl store3 = new DocumentStoreImpl();

    String str1 = "12345";
    URI uri1 = URI.create(str1);
    InputStream stream1 = new ByteArrayInputStream(str1.getBytes());

    String str2 = "123456";
    URI uri2 = URI.create(str2);
    InputStream stream2 = new ByteArrayInputStream(str2.getBytes());

    String str3 = "1234567";
    URI uri3 = URI.create(str3);
    InputStream stream3 = new ByteArrayInputStream(str3.getBytes());

    String str4 = "12345678";
    URI uri4 = URI.create(str4);
    InputStream stream4 = new ByteArrayInputStream(str4.getBytes());

    URI uri5 = URI.create("5");
    URI uri6 = URI.create("6");
    URI uri7 = URI.create("7");
    URI uri8 = URI.create("8");

    String string1 = "this is a test f&^%or doc **&^% number 1)()(*&^%$";
    InputStream input1 = new ByteArrayInputStream(string1.getBytes());
    String string2 = "this is a TEST f&^%or doc **&^% number 2)()(*&^%$";
    InputStream input2 = new ByteArrayInputStream(string2.getBytes());
    String string3 = "this iS a test f&^%or doc **&^% number 3)()(*&^%$";
    InputStream input3 = new ByteArrayInputStream(string3.getBytes());
    String string4 = "this is a test f&^%or dOC **&^% number 4)()(*&^%$";
    InputStream input4 = new ByteArrayInputStream(string4.getBytes());
    String string5 = "random test5 random!!!???><";
    InputStream input5 = new ByteArrayInputStream(string5.getBytes());
    String string6 = "more random test6!!!???><";
    InputStream input6 = new ByteArrayInputStream(string6.getBytes());
    String string7 = "this test, this is 7";
    InputStream input7 = new ByteArrayInputStream(string7.getBytes());
    String string8 = "more random more random more random more random more random 8";
    InputStream input8 = new ByteArrayInputStream(string8.getBytes());


    @Test
    public void stage3Tests() {
        store3.putDocument(input1, uri1, DocumentFormat.TXT);
        store3.putDocument(input2, uri2, DocumentFormat.TXT);
        store3.putDocument(input3, uri3, DocumentFormat.TXT);
        store3.putDocument(input4, uri4, DocumentFormat.TXT);
        store3.putDocument(input5, uri5, DocumentFormat.TXT);
        store3.putDocument(input6, uri6, DocumentFormat.TXT);
        store3.putDocument(input7, uri7, DocumentFormat.TXT);
        store3.putDocument(input8, uri8, DocumentFormat.TXT);

        List<String> list = store3.search("random");
        List<String> expectedList = new ArrayList<String>();
        expectedList.add(string8);
        expectedList.add(string5);
        expectedList.add(string6);
        assertEquals(expectedList, list);
        
        List<String> expectedList2 = new ArrayList<String>();
        expectedList2.add(string7);
        expectedList2.add(string3);
        expectedList2.add(string1);
        expectedList2.add(string4);
        expectedList2.add(string2);
        assertEquals(expectedList2, store3.searchByPrefix("th"));

        List<String> listWithA = new ArrayList<String>();
        listWithA.add(string1);
        listWithA.add(string2);
        listWithA.add(string3);
        listWithA.add(string4);
        assertEquals(listWithA, store3.search("a"));
        store3.deleteAll("a");
        assertEquals(new ArrayList<>(), store3.search("a"));

        assertEquals(list, store3.searchByPrefix("rand"));
        store3.deleteAllWithPrefix("rand");
        assertEquals(new ArrayList<>(), store3.searchByPrefix("rand"));

        store3.undo();
        assertEquals(list, store3.searchByPrefix("rand"));

        List<String> listWithString4 = new ArrayList<String>();
        listWithString4.add(string4);
        store3.undo(uri4);
        assertEquals(listWithString4, store3.search("a"));

        List<String> listWithString7 = new ArrayList<String>();
        listWithString7.add(string7);
        assertEquals(listWithString7, store3.search("7"));
        store3.undo(uri7);
        assertEquals(new ArrayList<>(), store3.search("7"));

        store3.putDocument(input7, uri7, DocumentFormat.TXT);
        store3.undo();

        store3.deleteDocument(uri4);
        assertEquals(null, store3.getDocumentAsTxt(uri4));
        store3.deleteDocument(uri8);
        list.remove(string8);
        assertEquals(list, store3.searchByPrefix("r"));

        store3.deleteAll("jhgfdfcvgbnhgtrfdcvbhgtfrd");
        store3.undo();
        store3.deleteAllWithPrefix("juy654resdxcvbhjuy76   ccch*&^%5");
        store3.undo();

        assertEquals(new HashSet<>(), store.deleteAll("123456789"));
        assertEquals(new HashSet<>(), store.deleteAllWithPrefix("123456789"));
        
        assertEquals(list, store3.searchByPrefix("r"));

        List<byte[]> pdfList = new ArrayList<byte[]>();
        pdfList.add(store3.getDocumentAsPdf(uri5));
        pdfList.add(store3.getDocumentAsPdf(uri6));
        assertEquals(pdfList, store3.searchPDFs("random"));
        assertEquals(pdfList, store3.searchPDFsByPrefix("test"));
        
        store3.undo();
    }

    @Test
    public void testUndoPut() {
        store.putDocument(stream1, uri1, DocumentFormat.TXT);
        store.putDocument(stream2, uri2, DocumentFormat.TXT);
        store.putDocument(stream3, uri3, DocumentFormat.TXT);
        store.putDocument(stream4, uri4, DocumentFormat.TXT);

        assertEquals(str4, store.getDocumentAsTxt(uri4));
        store.undo();
        assertEquals(null, store.getDocumentAsTxt(uri4));

        assertEquals(str2, store.getDocumentAsTxt(uri2));
        store.undo(uri2);
        assertEquals(null, store.getDocumentAsTxt(uri2));

        assertEquals(str1, store.getDocumentAsTxt(uri1));
        store.undo(uri1);
        assertEquals(null, store.getDocumentAsTxt(uri1));

        assertEquals(str3, store.getDocumentAsTxt(uri3));
        store.undo(uri3);
        assertEquals(null, store.getDocumentAsTxt(uri3));
        assertEquals(null, store.getDocumentAsPdf(uri3));
    }

    @Test
    public void testUndoRemove() {
        store.putDocument(stream1, uri1, DocumentFormat.TXT);
        store.putDocument(stream2, uri2, DocumentFormat.TXT);
        store.putDocument(stream3, uri3, DocumentFormat.TXT);
        store.putDocument(stream4, uri4, DocumentFormat.TXT);

        assertEquals(str3, store.getDocumentAsTxt(uri3));
        store.deleteDocument(uri3);
        assertEquals(null, store.getDocumentAsTxt(uri3));
        assertEquals(null, store.getDocumentAsPdf(uri3));
        store.undo();
        assertEquals(str3, store.getDocumentAsTxt(uri3));

        assertEquals(str2, store.getDocumentAsTxt(uri2));
        store.putDocument(null, uri2, DocumentFormat.TXT);
        assertEquals(null, store.getDocumentAsTxt(uri2));
        assertEquals(null, store.getDocumentAsPdf(uri2));
        store.undo(uri2);
        assertEquals(str2, store.getDocumentAsTxt(uri2));
    }

    @Test
    public void testUndoReplace() {
        store.putDocument(stream1, uri1, DocumentFormat.TXT);
        store.putDocument(stream2, uri1, DocumentFormat.TXT);

        assertEquals(str2, store.getDocumentAsTxt(uri1));
        store.undo(uri1);
        assertEquals(str1, store.getDocumentAsTxt(uri1));
    }

    @Test
    public void testUndoReplace2() {
        store.putDocument(stream1, uri1, DocumentFormat.TXT);
        store.putDocument(stream2, uri1, DocumentFormat.TXT);

        assertEquals(str2, store.getDocumentAsTxt(uri1));
        store.undo();
        assertEquals(str1, store.getDocumentAsTxt(uri1));
    }

    @Test(expected = IllegalStateException.class)
    public void illegalStateUndoTest() {
        store.undo();
    }

    @Test(expected = IllegalStateException.class)
    public void illegalStateUndoTest2() {
        store.putDocument(stream1, uri1, DocumentFormat.TXT);
        store.undo(uri2);
    }

    @Test
    public void undoDoesNothingTest() {
        store.deleteDocument(uri1);
        store.undo();
        
        store.putDocument(stream1, uri1, DocumentFormat.TXT);
        store.putDocument(stream1, uri1, DocumentFormat.TXT);
        store.undo();
        store.undo();
    }

    @Test
    public void undoDoesNotMessUpRestStackTest() {

    }

}