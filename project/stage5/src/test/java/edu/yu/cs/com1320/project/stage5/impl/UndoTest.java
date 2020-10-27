package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

public class UndoTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;

    //variables to hold possible values for doc2
    private URI uri3;
    private String txt3;

    //variables to hold possible values for doc2
    private URI uri4;
    private String txt4;

    private File baseDir;
    @Before
    public void createBaseDir()throws Exception{
        this.baseDir = Files.createTempDirectory("stage5").toFile();
    }

    private DocumentStoreImpl createStoreAndPutOne(){
        DocumentStoreImpl dsi = new DocumentStoreImpl(this.baseDir);
        ByteArrayInputStream bas1 = new ByteArrayInputStream(this.txt1.getBytes());
        dsi.putDocument(bas1,this.uri1, DocumentStore.DocumentFormat.TXT);
        return dsi;
    }

    private DocumentStoreImpl createStoreAndPutAll(){
        DocumentStoreImpl dsi = new DocumentStoreImpl(this.baseDir);
        //doc1
        ByteArrayInputStream bas = new ByteArrayInputStream(this.txt1.getBytes());
        dsi.putDocument(bas,this.uri1, DocumentStore.DocumentFormat.TXT);
        //doc2
        bas = new ByteArrayInputStream(this.txt2.getBytes());
        dsi.putDocument(bas,this.uri2, DocumentStore.DocumentFormat.TXT);
        //doc3
        bas = new ByteArrayInputStream(this.txt3.getBytes());
        dsi.putDocument(bas,this.uri3, DocumentStore.DocumentFormat.TXT);
        //doc4
        bas = new ByteArrayInputStream(this.txt4.getBytes());
        dsi.putDocument(bas,this.uri4, DocumentStore.DocumentFormat.TXT);
        return dsi;
    }

    @Before
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "keyword1 This is the text of doc1, in plain text. No fancy file format - just plain old String";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "keyword1 Text for doc2. A plain old String.";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "keyword123 This is the text of doc3 - doc doc goose";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "keyword12 doc4: how much wood would a woodchuck chuck...";
    }

    @Test
    public void undoAfterOnePut() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutOne();
        //undo after putting only one doc
        Document doc1 = new DocumentImpl(this.uri1, this.txt1, this.txt1.hashCode());
        Document returned1 = dsi.getDocument(this.uri1);
        assertNotNull("Did not get a document back after putting it in",returned1);
        assertEquals("Did not get doc1 back",doc1.getKey(),returned1.getKey());
        dsi.undo();
        returned1 = dsi.getDocument(this.uri1);
        assertNull("Put was undone - should have been null",returned1);
        try {
            dsi.undo();
            fail("no documents - should've thrown IllegalStateException");
        }catch(IllegalStateException e){}
    }

    @Test(expected=IllegalStateException.class)
    public void undoWhenEmptyShouldThrow() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutOne();
        //undo after putting only one doc
        dsi.undo();
        dsi.undo();
    }

    @Test(expected=IllegalStateException.class)
    public void undoByURIWhenEmptyShouldThrow() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutOne();
        //undo after putting only one doc
        dsi.undo();
        dsi.undo(this.uri1);
    }

    @Test
    public void undoAfterMultiplePuts() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        //undo put 4 - test before and after
        Document returned = dsi.getDocument(this.uri4);
        assertEquals("should've returned doc with uri4",this.uri4,returned.getKey());
        dsi.undo();
        assertNull("should've been null - put doc4 was undone",dsi.getDocument(this.uri4));

        //undo put 3 - test before and after
        returned = dsi.getDocument(this.uri3);
        assertEquals("should've returned doc with uri3",this.uri3,returned.getKey());
        dsi.undo();
        assertNull("should've been null - put doc3 was undone",dsi.getDocument(this.uri3));

        //undo put 2 - test before and after
        returned = dsi.getDocument(this.uri2);
        assertEquals("should've returned doc with uri3",this.uri2,returned.getKey());
        dsi.undo();
        assertNull("should've been null - put doc2 was undone",dsi.getDocument(this.uri2));

        //undo put 1 - test before and after
        returned = dsi.getDocument(this.uri1);
        assertEquals("should've returned doc with uri1",this.uri1,returned.getKey());
        dsi.undo();
        assertNull("should've been null - put doc1 was undone",dsi.getDocument(this.uri1));
        try {
            dsi.undo();
            fail("no documents - should've thrown IllegalStateException");
        }catch(IllegalStateException e){}
    }

    @Test
    public void undoNthPutByURI() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        //undo put 2 - test before and after
        Document returned = dsi.getDocument(this.uri2);
        assertEquals("should've returned doc with uri2",this.uri2,returned.getKey());
        dsi.undo(this.uri2);
        assertNull("should've returned null - put was undone",dsi.getDocument(this.uri2));
    }

    @Test
    public void undoDelete() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        assertTrue("text was not as expected",dsi.getDocumentAsTxt(this.uri3).equals(this.txt3));
        dsi.deleteDocument(this.uri3);
        assertNull("doc should've been deleted",dsi.getDocument(this.uri3));
        dsi.undo(this.uri3);
        assertTrue("should return doc3",dsi.getDocument(this.uri3).getKey().equals(this.uri3));
    }

    @Test
    public void undoNthDeleteByURI() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        assertTrue("text was not as expected",dsi.getDocumentAsTxt(this.uri3).equals(this.txt3));
        dsi.deleteDocument(this.uri3);
        dsi.deleteDocument(this.uri2);
        assertNull("should've been null",dsi.getDocument(this.uri2));
        dsi.undo(this.uri2);
        assertTrue("should return doc2",dsi.getDocument(this.uri2).getKey().equals(this.uri2));
    }

    @Test
    public void undoOverwriteByURI() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        String replacement = "this is a replacement for txt2";
        dsi.putDocument(new ByteArrayInputStream(replacement.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue("should've returned replacement text",dsi.getDocument(this.uri2).getDocumentAsTxt().equals(replacement));
        dsi.undo(this.uri2);
        assertTrue("should've returned original text",dsi.getDocument(this.uri2).getDocumentAsTxt().equals(this.txt2));
    }

    //undo most recent when most recent deleted multiple documents
    @Test
    public void stage3PlainUndoThatImpactsMultiple(){
        String keyword1 = "keyword1";
        //step 1: put all documents in
        DocumentStoreImpl dsi = createStoreAndPutAll();

        //step 2: delete multiple docs that have the same keyword
        dsi.deleteAll(keyword1);
        //make sure they are gone - search by keyword
        List<String> results = dsi.search(keyword1);
        assertEquals("docs with keyword1 should be gone - List size should be 0",0,results.size());
        //make sure they are gone by URI - use protected method
        assertNull("document with URI " + this.uri1 + "should've been deleted",dsi.getDocument(this.uri1));
        assertNull("document with URI " + this.uri2 + "should've been deleted",dsi.getDocument(this.uri2));
        //make sure other docs are still there - use protected method
        assertNotNull("document with URI " + this.uri3 + "should NOT have been deleted",dsi.getDocument(this.uri3));
        assertNotNull("document with URI " + this.uri4 + "should NOT have been deleted",dsi.getDocument(this.uri4));

        //step 3: undo the last command, i.e. the delete
        dsi.undo();

        //check that they are back by keyword
        results = dsi.search(keyword1);
        assertEquals("docs with keyword1 should be back - List size should be 2",2,results.size());
        //check that they are back by URI - use protected method
        assertNotNull("document with URI " + this.uri1 + "should be back",dsi.getDocument(this.uri1));
        assertNotNull("document with URI " + this.uri2 + "should be back",dsi.getDocument(this.uri2));
        //make sure the other docs are still unaffected - by protected method
        assertNotNull("document with URI " + this.uri3 + "should NOT have been deleted",dsi.getDocument(this.uri3));
        assertNotNull("document with URI " + this.uri4 + "should NOT have been deleted",dsi.getDocument(this.uri4));
    }
    //undo by URI which is part of most recent which deleted multiple documents
    @Test
    public void stage3UndoByURIThatImpactsOne() {
        String keyword1 = "keyword1";
        //step 1: put all documents in
        DocumentStoreImpl dsi = createStoreAndPutAll();

        //step 2: delete multiple docs that have the same keyword
        dsi.deleteAll(keyword1);
        //make sure they are gone - search by keyword
        List<String> results = dsi.search(keyword1);
        assertEquals("docs with keyword1 should be gone - List size should be 0",0,results.size());
        //make sure they are gone by URI - use protected method
        assertNull("document with URI " + this.uri1 + "should've been deleted",dsi.getDocument(this.uri1));
        assertNull("document with URI " + this.uri2 + "should've been deleted",dsi.getDocument(this.uri2));
        //make sure other docs are still there - use protected method
        assertNotNull("document with URI " + this.uri3 + "should NOT have been deleted",dsi.getDocument(this.uri3));
        assertNotNull("document with URI " + this.uri4 + "should NOT have been deleted",dsi.getDocument(this.uri4));

        //step 3: undo the deletion of doc 2
        dsi.undo(this.uri2);

        //check that doc2 is back by keyword
        results = dsi.search(keyword1);
        assertEquals("doc2 should be back - List size should be 1",1,results.size());
        assertEquals("doc2 should be back",results.get(0),this.txt2);
        //check that doc2 is back by URI but doc 1 is still null- use protected method
        assertNotNull("document with URI " + this.uri2 + "should be back",dsi.getDocument(this.uri2));
        assertNull("document with URI " + this.uri1 + "should still be null",dsi.getDocument(this.uri1));
        //make sure the other docs are still unaffected - by protected method
        assertNotNull("document with URI " + this.uri3 + "should NOT have been deleted",dsi.getDocument(this.uri3));
        assertNotNull("document with URI " + this.uri4 + "should NOT have been deleted",dsi.getDocument(this.uri4));
    }
    //undo by URI which is EARLIER than most recent
    @Test
    public void stage3UndoByURIThatImpactsEarlierThanLast() {
        String prefix = "keyword12";
        String keyword = "keyword1";
        //step 1: put all documents in
        DocumentStoreImpl dsi = createStoreAndPutAll();

        //step 2: delete multiple docs that have the same prefix, and then delete others by keyword
        dsi.deleteAllWithPrefix(prefix);
        dsi.deleteAll(keyword);
        //make sure they are gone - search by keyword
        List<String> results = dsi.search(keyword);
        assertEquals("docs with keyword1 should be gone - List size should be 0",0,results.size());
        results = dsi.searchByPrefix(prefix);
        assertEquals("docs with prefix " + prefix + " should be gone - List size should be 0",0,results.size());
        //make sure they are gone by URI - use protected method
        assertNull("document with URI " + this.uri1 + "should've been deleted",dsi.getDocument(this.uri1));
        assertNull("document with URI " + this.uri2 + "should've been deleted",dsi.getDocument(this.uri2));
        assertNull("document with URI " + this.uri3 + "should've been deleted",dsi.getDocument(this.uri3));
        assertNull("document with URI " + this.uri4 + "should've been deleted",dsi.getDocument(this.uri4));

        //step 3: undo the deletion of doc 3
        dsi.undo(this.uri3);

        //check that doc3 is back by keyword
        results = dsi.search("keyword123");
        assertEquals("doc3 should be back - List size should be 1",1,results.size());
        //check that doc3 is back but none of the others are back
        assertNotNull("document with URI " + this.uri3 + "should be back",dsi.getDocument(this.uri3));
        assertNull("document with URI " + this.uri1 + "should still be null",dsi.getDocument(this.uri1));
        assertNull("document with URI " + this.uri2 + "should NOT have been deleted",dsi.getDocument(this.uri2));
        assertNull("document with URI " + this.uri4 + "should NOT have been deleted",dsi.getDocument(this.uri4));
    }
}