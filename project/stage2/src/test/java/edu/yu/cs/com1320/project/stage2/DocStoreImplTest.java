package edu.yu.cs.com1320.project.stage2;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URI;

import org.junit.*;

import edu.yu.cs.com1320.project.stage2.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage2.impl.DocumentStoreImpl;

public class DocStoreImplTest {

    DocumentStoreImpl store = new DocumentStoreImpl();

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