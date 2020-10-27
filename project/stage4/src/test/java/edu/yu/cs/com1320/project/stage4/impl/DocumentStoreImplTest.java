package edu.yu.cs.com1320.project.stage4.impl;

//import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

public class DocumentStoreImplTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;
    private byte[] pdfData1;
    private String pdfTxt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;
    private byte[] pdfData2;
    private String pdfTxt2;

    //variables to hold possible values for doc3
    private URI uri3;
    private String txt3;
    private byte[] pdfData3;
    private String pdfTxt3;

    //variables to hold possible values for doc4
    private URI uri4;
    private String txt4;
    private byte[] pdfData4;
    private String pdfTxt4;

    private int bytes1;
    private int bytes2;
    private int bytes3;
    private int bytes4;

    @Before
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This is the text of doc1, in plain text. No fancy file format - just plain old String. Computer. Headphones.";
        this.pdfTxt1 = "This is some PDF text for doc1, hat tip to Adobe.";
        this.pdfData1 = Utils.textToPdfData(this.pdfTxt1);

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2. A plain old String.";
        this.pdfTxt2 = "PDF content for doc2: PDF format was opened in 2008.";
        this.pdfData2 = Utils.textToPdfData(this.pdfTxt2);

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "This is the text of doc3";
        this.pdfTxt3 = "This is some PDF text for doc3, hat tip to Adobe.";
        this.pdfData3 = Utils.textToPdfData(this.pdfTxt3);

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "This is the text of doc4";
        this.pdfTxt4 = "This is some PDF text for doc4, which is open source.";
        this.pdfData4 = Utils.textToPdfData(this.pdfTxt4);

        this.bytes1 = this.pdfTxt1.getBytes().length + this.pdfData1.length;
        this.bytes2 = this.pdfTxt2.getBytes().length + this.pdfData2.length;
        this.bytes3 = this.pdfTxt3.getBytes().length + this.pdfData3.length;
        this.bytes4 = this.pdfTxt4.getBytes().length + this.pdfData4.length;
    }

    @Test
    public void testPutPdfDocumentNoPreviousDocAtURI(){
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
    }

    @Test
    public void testPutTxtDocumentNoPreviousDocAtURI(){
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.txt1.hashCode());
    }

    @Test
    public void testPutDocumentWithNullArguments(){
        DocumentStore store = new DocumentStoreImpl();
        try {
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), null, DocumentStore.DocumentFormat.TXT);
            fail("null URI should've thrown IllegalArgumentException");
        }catch(IllegalArgumentException e){}
        try {
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, null);
            fail("null format should've thrown IllegalArgumentException");
        }catch(IllegalArgumentException e){}
    }

    @Test
    public void testPutNewVersionOfDocumentPdf(){
        //put the first version
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
        assertEquals("failed to return correct pdf text",this.pdfTxt1,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));

        //put the second version, testing both return value of put and see if it gets the correct text
        returned = store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri1, DocumentStore.DocumentFormat.PDF);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue("should return hashcode of old text",this.pdfTxt1.hashCode() == returned || this.pdfTxt2.hashCode() == returned);
        assertEquals("failed to return correct pdf text", this.pdfTxt2,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
    }

    @Test
    public void testPutNewVersionOfDocumentTxt(){
        //put the first version
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.txt1.hashCode());
        assertEquals("failed to return correct text",this.txt1,store.getDocumentAsTxt(this.uri1));

        //put the second version, testing both return value of put and see if it gets the correct text
        returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue("should return hashcode of old text",this.txt1.hashCode() == returned || this.txt2.hashCode() == returned);
        assertEquals("failed to return correct text",this.txt2,store.getDocumentAsTxt(this.uri1));
    }

    @Test
    public void testGetTxtDocAsPdf(){
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.txt1.hashCode());
        assertEquals("failed to return correct pdf text",this.txt1,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
    }

    @Test
    public void testGetTxtDocAsTxt(){
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.txt1.hashCode());
        assertEquals("failed to return correct text",this.txt1,store.getDocumentAsTxt(this.uri1));
    }

    @Test
    public void testGetPdfDocAsPdf(){
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
        assertEquals("failed to return correct pdf text",this.pdfTxt1,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
    }

    @Test
    public void testGetPdfDocAsTxt(){
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
        assertEquals("failed to return correct text",this.pdfTxt1,store.getDocumentAsTxt(this.uri1));
    }

    @Test
    public void testDeleteDoc(){
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        store.deleteDocument(this.uri1);
        assertEquals("calling get on URI from which doc was deleted should've returned null", null, store.getDocumentAsPdf(this.uri1));
    }

    @Test
    public void testDeleteDocReturnValue(){
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        //should return true when deleting a document
        assertEquals("failed to return true when deleting a document",true,store.deleteDocument(this.uri1));
        //should return false if I try to delete the same doc again
        assertEquals("failed to return false when trying to delete that which was already deleted",false,store.deleteDocument(this.uri1));
        //should return false if I try to delete something that was never there to begin with
        assertEquals("failed to return false when trying to delete that which was never there to begin with",false,store.deleteDocument(this.uri2));
    }

    @Test
    public void stage3Search(){
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);

        List<String> results = store.search("plain");
        assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
        results = store.search("missing");
        assertEquals("expected 0 matches, received " + results.size(),0,results.size());
    }
    @Test
    public void stage3SearchPDFs(){
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri2, DocumentStore.DocumentFormat.PDF);

        List<byte[]> results = store.searchPDFs("pdf");
        assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
        results = store.searchPDFs("missing");
        assertEquals("expected 0 matches, received " + results.size(),0,results.size());
    }

    @Test
    public void stage3DeleteAll(){
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        //search, get results
        List<String> results = store.search("plain");
        assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
        //delete all, get no matches
        store.deleteAll("plain");
        results = store.search("plain");
        assertEquals("expected 0 matches, received " + results.size(),0,results.size());
    }

    @Test
    public void stage3SearchByPrefix(){
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        //search, get results
        List<String> results = store.searchByPrefix("str");
        assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
        results = store.searchByPrefix("comp");
        assertEquals("expected 1 match, only received " + results.size(),1,results.size());
        results = store.searchByPrefix("doc2");
        assertEquals("expected 1 match, only received " + results.size(),1,results.size());
        results = store.searchByPrefix("blah");
        assertEquals("expected 0 match, received " + results.size(),0,results.size());
    }
    @Test
    public void stage3SearchPDFsByPrefix(){
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri2, DocumentStore.DocumentFormat.PDF);
        //search, get results
        List<byte[]> results = store.searchPDFsByPrefix("pd");
        assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
        results = store.searchPDFsByPrefix("ado");
        assertEquals("expected 1 match, only received " + results.size(),1,results.size());
        results = store.searchPDFsByPrefix("blah");
        assertEquals("expected 0 match, received " + results.size(),0,results.size());
    }

    @Test
    public void stage3DeleteAllWithPrefix(){
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri2, DocumentStore.DocumentFormat.PDF);
        //search, get results
        List<byte[]> results = store.searchPDFsByPrefix("pd");
        assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
        //delete all starting with pd
        store.deleteAllWithPrefix("pd");
        //search again, should be empty
        results = store.searchPDFsByPrefix("pd");
        assertEquals("expected 0 matches, received " + results.size(),0,results.size());
    }

    @Test
    public void stage3TestSearchByKeyword() {
        //put the four docs into the doc store
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        //search by keyword
        List<String> results = store.search("adobe");
        assertEquals("search should've returned 2 results",2,results.size());
        //make sure we have the correct two documents
        boolean found1, found3;
        found1 = found3 = false;
        String lower1 = this.pdfTxt1.toLowerCase();
        String lower3 = this.pdfTxt3.toLowerCase();
        for(String txt:results){
            if(txt.toLowerCase().equals(lower1)) {
                found1 = true;
            }else if(txt.toLowerCase().equals(lower3)){
                found3 = true;
            }
        }
        assertTrue("should've found doc1 and doc3",found1 && found3);
    }

    @Test
    public void stage3TestSearchByPrefix() {
        //put the four docs into the doc store
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        //search by prefix
        List<String> results = store.searchByPrefix("ha");
        assertEquals("search should've returned 2 results",2,results.size());
        //make sure we have the correct two documents
        boolean found1, found3;
        found1 = found3 = false;
        String lower1 = this.pdfTxt1.toLowerCase();
        String lower3 = this.pdfTxt3.toLowerCase();
        for(String txt:results){
            if(txt.toLowerCase().equals(lower1)) {
                found1 = true;
            }else if(txt.toLowerCase().equals(lower3)){
                found3 = true;
            }
        }
        assertTrue("should've found doc1 and doc3",found1 && found3);
    }

    @Test
    public void stage3TestDeleteAllByKeyword() {
        //put the four docs into the doc store
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        //delete by keyword
        store.deleteAll("adobe");
        //search by keyword
        List<String> results = store.search("adobe");
        assertEquals("search should've returned 0 results",0,results.size());
        //make sure the correct two documents were deleted
        assertNull("doc1 should've been deleted",store.getDocument(this.uri1));
        assertNull("doc3 should've been deleted",store.getDocument(this.uri3));
        //make sure the other two documents were NOT deleted
        assertNotNull("doc2 should NOT been deleted",store.getDocument(this.uri2));
        assertNotNull("doc4 should NOT been deleted",store.getDocument(this.uri4));
    }

    @Test
    public void stage3TestDeleteAllByPrefix() {
        //put the four docs into the doc store
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        String prefix = "ha";
        //delete by prefix
        store.deleteAllWithPrefix(prefix);
        //search by keyword
        List<String> results = store.searchByPrefix(prefix);
        assertEquals("search should've returned 0 results",0,results.size());
        //make sure the correct two documents were deleted
        assertNull("doc1 should've been deleted",store.getDocument(this.uri1));
        assertNull("doc3 should've been deleted",store.getDocument(this.uri3));
        //make sure the other two documents were NOT deleted
        assertNotNull("doc2 should NOT been deleted",store.getDocument(this.uri2));
        assertNotNull("doc4 should NOT been deleted",store.getDocument(this.uri4));
    }

    /*
Every time a document is used, its last use time should be updated to the relative JVM time, as measured in nanoseconds (see java.lang.System.nanoTime().)
A Document is considered to be “used” whenever it is accessed as a result of a call to any part of DocumentStore’s public API. In other words, if it is “put”,
or returned in any form as the result of any “get” or “search” request, or an action on it is undone via any call to either of the DocumentStore.undo methods.
     */

    @Test
    public void stage4TestNoUpdateDocLastUseTimeOnProtectedGet(){
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        Document doc = store.getDocument(this.uri1);
        long first = doc.getLastUseTime();
        doc = store.getDocument(this.uri1);
        long second = doc.getLastUseTime();
        //was last use time updated on the put?
        assertTrue("last use time should NOT be changed when the protected DocStore.getDoc method is called", first == second);
    }

    @Test
    public void stage4TestUpdateDocLastUseTimeOnPut(){
        DocumentStoreImpl store = new DocumentStoreImpl();
        long before = System.nanoTime();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the put?
        assertTrue("last use time should be after the time at which the document was put", before < doc.getLastUseTime());
    }
    @Test
    public void stage4TestUpdateDocLastUseTimeOnOverwrite(){
        DocumentStoreImpl store = new DocumentStoreImpl();
        //was last use time updated on the put?
        long before = System.nanoTime();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        Document doc = store.getDocument(this.uri1);
        assertTrue("last use time should be after the time at which the document was put", before < doc.getLastUseTime());
        before = System.nanoTime();
        //was last use time updated on overwrite?
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri1, DocumentStore.DocumentFormat.PDF);
        Document doc2 = store.getDocument(this.uri1);
        assertTrue("last use time should be after the time at which the document was overwritten", before < doc2.getLastUseTime());
    }

    @Test
    public void stage4TestUpdateDocLastUseTimeOnSearch(){
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        long before = System.nanoTime();
        //this search should return the contents of the doc at uri1
        List<String> results = store.search("pdf");
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the search?
        assertTrue("last use time should be after the time at which the document was put", before < doc.getLastUseTime());
    }
    @Test
    public void stage4TestUpdateDocLastUseTimeOnSearchByPrefix(){
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        long before = System.nanoTime();
        //this search should return the contents of the doc at uri1
        List<String> results = store.searchByPrefix("pdf");
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the searchByPrefix?
        assertTrue("last use time should be after the time at which the document was put", before < doc.getLastUseTime());
    }
    @Test
    public void stage4TestUpdateDocLastUseTimeOnSearchPDFs(){
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        long before = System.nanoTime();
        //this search should return the contents of the doc at uri1
        List<byte[]> results = store.searchPDFs("pdf");
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the searchPDFs?
        assertTrue("last use time should be after the time at which the document was put", before < doc.getLastUseTime());
    }
    @Test
    public void stage4TestUpdateDocLastUseTimeOnSearchPDFsByPrefix(){
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        long before = System.nanoTime();
        //this search should return the contents of the doc at uri1
        List<byte[]> results = store.searchPDFsByPrefix("pdf");
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the searchPDFs?
        assertTrue("last use time should be after the time at which the document was put", before < doc.getLastUseTime());
    }

    /**
     * test max doc count via put
     */
    @Test
    public void stage4TestMaxDocCountViaPut() {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        //uri1 and uri2 should both be gone, having been pushed out by 3 and 4
        assertNull("uri1 should've been pushed out of memory when uri3 was inserted",store.getDocument(this.uri1));
        assertNull("uri2 should've been pushed out of memory when uri4 was inserted",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        assertNotNull("uri4 should still be in memory",store.getDocument(this.uri4));
    }

    /**
     * test max doc count via search
     */
    @Test
    public void stage4TestMaxDocCountViaSearch() {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        //all 3 should still be in memory
        assertNotNull("uri1 should still be in memory",store.getDocument(this.uri1));
        assertNotNull("uri2 should still be in memory",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        //"touch" uri1 via a search
        store.search("doc1");
        //add doc4, doc2 should be pushed out, not doc1
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        assertNotNull("uri1 should still be in memory",store.getDocument(this.uri1));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        assertNotNull("uri4 should still be in memory",store.getDocument(this.uri4));
        //uri2 should've been pushed out of memory
        assertNull("uri2 should still be in memory",store.getDocument(this.uri2));
    }

    /**
     * test undo after going over max doc count
     */
    @Test
    public void stage4TestUndoAfterMaxDocCount() {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        //all 3 should still be in memory
        assertNotNull("uri1 should still be in memory",store.getDocument(this.uri1));
        assertNotNull("uri2 should still be in memory",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        //add doc4, doc1 should be pushed out
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        assertNotNull("uri2 should still be in memory",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        assertNotNull("uri4 should still be in memory",store.getDocument(this.uri4));
        //uri1 should've been pushed out of memory
        assertNull("uri1 should still be in memory",store.getDocument(this.uri1));
        //undo the put - should eliminate doc4, and only uri2 and uri3 should be left
        store.undo();
        assertNull("uri4 should be gone due to the undo",store.getDocument(this.uri4));
        assertNotNull("uri2 should still be in memory",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
    }


    /**
     * test max doc bytes via put
     */
    @Test
    public void stage4TestMaxDocBytesViaPut() {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(this.bytes1 + this.bytes2 + 20);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        //uri1 and uri2 should both be gone, having been pushed out by 3 and 4
        assertNull("uri1 should've been pushed out of memory when uri3 was inserted",store.getDocument(this.uri1));
        assertNull("uri2 should've been pushed out of memory when uri4 was inserted",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        assertNotNull("uri4 should still be in memory",store.getDocument(this.uri4));
    }

    /**
     * test max doc bytes via search
     */
    @Test
    public void stage4TestMaxDocBytesViaSearch() {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(this.bytes1 + this.bytes2 + this.bytes3 + 20);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        //all 3 should still be in memory
        assertNotNull("uri1 should still be in memory",store.getDocument(this.uri1));
        assertNotNull("uri2 should still be in memory",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        //"touch" uri1 via a search
        store.search("doc1");
        //add doc4, doc2 should be pushed out, not doc1
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        assertNotNull("uri1 should still be in memory",store.getDocument(this.uri1));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        assertNotNull("uri4 should still be in memory",store.getDocument(this.uri4));
        //uri2 should've been pushed out of memory
        assertNull("uri2 should still be in memory",store.getDocument(this.uri2));
    }

    /**
     * test undo after going over max doc count
     */
    @Test
    public void stage4TestUndoAfterMaxBytes() {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(this.bytes1 + this.bytes2 + this.bytes3 + 20);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        //all 3 should still be in memory
        assertNotNull("uri1 should still be in memory",store.getDocument(this.uri1));
        assertNotNull("uri2 should still be in memory",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        //add doc4, doc1 should be pushed out
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        assertNotNull("uri2 should still be in memory",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        assertNotNull("uri4 should still be in memory",store.getDocument(this.uri4));
        //uri1 should've been pushed out of memory
        assertNull("uri1 should still be in memory",store.getDocument(this.uri1));
        //undo the put - should eliminate doc4, and only uri2 and uri3 should be left
        store.undo();
        assertNull("uri4 should be gone due to the undo",store.getDocument(this.uri4));
        assertNotNull("uri2 should still be in memory",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
    }

    /**
     * test going over max docs only when both max docs and max bytes are set
     */
    @Test
    public void stage4TestMaxDocsWhenDoubleMaxViaPut() {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(this.bytes1*10);
        store.setMaxDocumentCount(2);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        //uri1 and uri2 should both be gone, having been pushed out by 3 and 4
        assertNull("uri1 should've been pushed out of memory when uri3 was inserted",store.getDocument(this.uri1));
        assertNull("uri2 should've been pushed out of memory when uri4 was inserted",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        assertNotNull("uri4 should still be in memory",store.getDocument(this.uri4));
    }

    /**
     * test going over max bytes only when both max docs and max bytes are set
     */
    @Test
    public void stage4TestMaxBytesWhenDoubleMaxViaPut() {
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(this.bytes1 + this.bytes2 + 20);
        store.setMaxDocumentCount(20);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2), this.uri2, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData3), this.uri3, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData4), this.uri4, DocumentStore.DocumentFormat.PDF);
        //uri1 and uri2 should both be gone, having been pushed out by 3 and 4
        assertNull("uri1 should've been pushed out of memory when uri3 was inserted",store.getDocument(this.uri1));
        assertNull("uri2 should've been pushed out of memory when uri4 was inserted",store.getDocument(this.uri2));
        assertNotNull("uri3 should still be in memory",store.getDocument(this.uri3));
        assertNotNull("uri4 should still be in memory",store.getDocument(this.uri4));
    }
}