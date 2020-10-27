package edu.yu.cs.com1320.project.stage5.impl;

//import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

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

    private File baseDir;

    private String updateAddition;

    @Before
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This is the text of doc1 in plain text No fancy file format just plain old String Computer Headphones";
        this.pdfTxt1 = "This is some PDF text for doc1 hat tip to Adobe";
        this.pdfData1 = Utils.textToPdfData(this.pdfTxt1);

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2 A plain old String";
        this.pdfTxt2 = "PDF content for doc2 PDF format was opened in 2008";
        this.pdfData2 = Utils.textToPdfData(this.pdfTxt2);

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "Text for doc3 A plain old String";
        this.pdfTxt3 = "This is some PDF text for doc3 hat tip to Adobe";
        this.pdfData3 = Utils.textToPdfData(this.pdfTxt3);

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "Text for doc4 A plain old String";
        this.pdfTxt4 = "This is some PDF text for doc4 which is open source";
        this.pdfData4 = Utils.textToPdfData(this.pdfTxt4);

        this.bytes1 = this.pdfTxt1.getBytes().length + this.pdfData1.length;
        this.bytes2 = this.pdfTxt2.getBytes().length + this.pdfData2.length;
        this.bytes3 = this.pdfTxt3.getBytes().length + this.pdfData3.length;
        this.bytes4 = this.pdfTxt4.getBytes().length + this.pdfData4.length;

        //create baseDir
        this.baseDir = Files.createTempDirectory("stage5").toFile();

        this.updateAddition = "UPDATED-UPDATED";
    }
    @After
    public void cleanUp(){
        TestUtils.deleteTree(this.baseDir);
        this.baseDir.delete();
    }
    @Test
    public void testPutPdfDocumentNoPreviousDocAtURI(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
    }

    @Test
    public void testPutTxtDocumentNoPreviousDocAtURI(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.txt1.hashCode());
    }

    @Test
    public void testPutDocumentWithNullArguments(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.txt1.hashCode());
        assertEquals("failed to return correct pdf text",this.txt1,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
    }

    @Test
    public void testGetTxtDocAsTxt(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.txt1.hashCode());
        assertEquals("failed to return correct text",this.txt1,store.getDocumentAsTxt(this.uri1));
    }

    @Test
    public void testGetPdfDocAsPdf(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
        assertEquals("failed to return correct pdf text",this.pdfTxt1,Utils.pdfDataToText(store.getDocumentAsPdf(this.uri1)));
    }

    @Test
    public void testGetPdfDocAsTxt(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        int returned = store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        //TODO allowing for student following old API comment. To be changed for stage 2 to insist on following new comment.
        assertTrue(returned == 0 || returned == this.pdfTxt1.hashCode());
        assertEquals("failed to return correct text",this.pdfTxt1,store.getDocumentAsTxt(this.uri1));
    }

    @Test
    public void testDeleteDoc(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        store.deleteDocument(this.uri1);
        assertEquals("calling get on URI from which doc was deleted should've returned null", null, store.getDocumentAsPdf(this.uri1));
    }

    @Test
    public void testDeleteDocReturnValue(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);

        List<String> results = store.search("plain");
        assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
        results = store.search("missing");
        assertEquals("expected 0 matches, received " + results.size(),0,results.size());
    }
    @Test
    public void stage3SearchPDFs(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
        store.putDocument(new ByteArrayInputStream(this.pdfData1),this.uri1, DocumentStore.DocumentFormat.PDF);
        store.putDocument(new ByteArrayInputStream(this.pdfData2),this.uri2, DocumentStore.DocumentFormat.PDF);

        List<byte[]> results = store.searchPDFs("pdf");
        assertEquals("expected 2 matches, only received " + results.size(),2,results.size());
        results = store.searchPDFs("missing");
        assertEquals("expected 0 matches, received " + results.size(),0,results.size());
    }

    @Test
    public void stage3DeleteAll(){
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStore store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
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
    
    @Test
    public void stage4TestNoUpdateDocLastUseTimeOnProtectedGet(){
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
        long before = System.nanoTime();
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the put?
        assertTrue("last use time should be after the time at which the document was put", before < doc.getLastUseTime());
    }
    @Test
    public void stage4TestUpdateDocLastUseTimeOnOverwrite(){
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
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
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
        store.putDocument(new ByteArrayInputStream(this.pdfData1), this.uri1, DocumentStore.DocumentFormat.PDF);
        long before = System.nanoTime();
        //this search should return the contents of the doc at uri1
        List<byte[]> results = store.searchPDFsByPrefix("pdf");
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the searchPDFs?
        assertTrue("last use time should be after the time at which the document was put", before < doc.getLastUseTime());
    }

    /**
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     * ***********************************************STAGE 5 TESTS***************************************************
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     */

    private void checkContents(String errorMsg, String contents,String expected){
        assertNotNull(errorMsg + ": contents were null",contents);
        assertTrue(errorMsg + ": expected content not found",contents.toLowerCase().indexOf(expected.toLowerCase()) >= 0);
    }

    //in each of the tests below, assert as a precondtion that whatever should be on disk is, and whatever should be in memory is

    //test1a:
    // 1) put docA which didn't exist, and thus causes docB to be written to disk due to reaching MAX DOC COUNT
    // 2) get docA which was on disk, thus going over DOCUMENT COUNT limit and causing docB to be written to disk
    @Test
    public void stage5PushToDiskViaMaxDocCount() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
        store.setMaxDocumentCount(2);
        pushAboveMaxViaPutNew(store);
    }

    private void pushAboveMaxViaPutNew(DocumentStoreImpl store) throws IOException{
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.getDocument(this.uri1);
        Document doc2 = store.getDocument(this.uri2);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);

        //at this point, 2 and 3 should be in memory, and 1 should be on disk, pushed out when doc3 was put
        String doc1Str = TestUtils.getContents(this.baseDir,this.uri1);
        checkContents("doc1 should've been on disk, but was not",doc1Str,this.txt1);
        assertNotNull("doc2 should be in memory",store.getDocument(this.uri2));
        assertNotNull("doc3 should be in memory",store.getDocument(this.uri3));
        assertNull("doc2 should NOT have been on disk",TestUtils.getContents(this.baseDir,this.uri2));
        assertNull("doc3 should NOT have been on disk",TestUtils.getContents(this.baseDir,this.uri3));
        //make sure that when doc1 is requested, it is NOT the same object as doc1 above, which was gotten BEFORE it was kicked out of memory
        //this search should bring doc1 back into memory and push doc2 out to disk
        store.search("doc1");
        Document doc1v2 = store.getDocument(this.uri1);
        assertTrue("the original doc1 object should NOT have been returned - should be a different object in memory now",TestUtils.equalButNotIdentical(doc1,doc1v2));

        //check that doc2 is now on disk, but 1 and 3 are in memory
        String doc2Str = TestUtils.getContents(this.baseDir,this.uri2);
        checkContents("doc2 should've been on disk, but was not",doc2Str,this.txt2);
        assertNull("doc1 should NOT have been on disk",TestUtils.getContents(this.baseDir,this.uri1));
        assertNull("doc3 should NOT have been on disk",TestUtils.getContents(this.baseDir,this.uri3));

        //make sure that when doc2 is requested, it is NOT the same object as docs above, which was gotten BEFORE it was kicked out of memory
        //this search should bring doc2 back into memory
        store.search("doc2");
        Document doc2v2 = store.getDocument(this.uri2);
        assertTrue("the original doc2 object should NOT have been returned - should be a different object in memory now",TestUtils.equalButNotIdentical(doc2,doc2v2));
    }

    //test4a: reach MAX MEMORY and have some docs on disk. Delete docs in memory. Assert that no docs were brought in from disk. Get docs that are on disk, assert they are back in memory and off disk.
    @Test
    public void stage5PushToDiskViaMaxDocCountBringBackInViaDelete() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
        store.setMaxDocumentCount(2);
        deleteDocInMemoryBringInDocFromDisk(store);
    }

    /**
     * This method assumes only 2 docs fit in memory for whatever reason. It does the following:
     * 1) put docs1, doc2, and then doc3
     * 2) assert that doc1 is NOT in memory and IS on disk, that doc2 and doc3 ARE in memory
     * 3) deletes doc3, making room in memory for doc1
     * 4) assert that doc1 is still NOT in memory even though doc3 was deleted
     * 5) do a search that brings doc1 back into memory
     * 6) assert that doc2 is still in memory and doc1 is back in memory
     * @param store
     * @throws IOException
     */
    private void deleteDocInMemoryBringInDocFromDisk(DocumentStoreImpl store) throws IOException{
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.getDocument(this.uri1);
        Document doc2 = store.getDocument(this.uri2);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);

        //at this point, 2 and 3 should be in memory, and 1 should be on disk, pushed out when doc3 was put
        //assertNull("doc1 should NOT be in memory",store.getDocument(this.uri1));
        String doc1Str = TestUtils.getContents(this.baseDir,this.uri1);
        checkContents("doc1 should've been on disk, but was not",doc1Str,this.txt1);
        assertNotNull("doc2 should be in memory",store.getDocument(this.uri2));
        assertNotNull("doc3 should be in memory",store.getDocument(this.uri3));
        assertNull("doc2 should NOT have been on disk",TestUtils.getContents(this.baseDir,this.uri2));
        assertNull("doc3 should NOT have been on disk",TestUtils.getContents(this.baseDir,this.uri3));

        //delete doc3, making room for doc1; assert that doc3 is gone but doc1 still not in memory
        store.deleteDocument(this.uri3);
        //assertNull("doc3 should be gone/deleted",store.getDocument(this.uri3));
       // assertNull("doc1 should STILL not be in memory",store.getDocument(this.uri1));

        //do a search that brings doc1 back into memory, assert that doc2 is still unaffected and doc1 is back in memory
        store.search("doc1");
        assertNotNull("doc1 should be back in memory",store.getDocument(this.uri1));
        assertNull("doc1 should have been removed from disk",TestUtils.getContents(this.baseDir,this.uri1));
        assertTrue("doc1 should NOT be the same exact object in memory as earlier - a new object should've been created  when deserializing",TestUtils.equalButNotIdentical(doc1,store.getDocument(this.uri1)));
        assertFalse("doc2 should still be the same exact object in memory",TestUtils.equalButNotIdentical(doc2,store.getDocument(this.uri2)));
    }


    //test5a: undo a delete which causes doc store to go over MAX MEMORY, causing docs to be written to disk. Assert docs being in memory and on disk as pre/post conditions.
    @Test
    public void stage5PushToDiskViaMaxDocCountViaUndoDelete() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
        store.setMaxDocumentCount(2);
        overLimitViaUndo(store);
    }

    private void overLimitViaUndo(DocumentStoreImpl store) throws IOException{
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.getDocument(this.uri1);
        Document doc2 = store.getDocument(this.uri2);
        //delete doc2, making room for doc3
        store.deleteDocument(this.uri2);
        //put doc 3
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        //at this point, 1 and 3 should be in memory, and 2 should be gone
        assertNotNull("doc3 should be in memory",store.getDocument(this.uri3));
        assertNotNull("doc1 should be in memory",store.getDocument(this.uri1));
        //assertNull("doc2 should be null because it was deleted",store.getDocument(this.uri2));
        //undo the deletion of doc2, which should push doc1 out to disk. doc2 and doc3 should be in memory
        store.undo(this.uri2);
        //assertNull("doc1 should NOT be in memory",store.getDocument(this.uri1));
        String doc1Str = TestUtils.getContents(this.baseDir,this.uri1);
        checkContents("doc1 should've been written out to disk, but was not",doc1Str,this.txt1);
        assertNull("doc2 should NOT be on disk",TestUtils.getContents(this.baseDir,this.uri2));
        assertNotNull("doc2 should be in memory",store.getDocument(this.uri2));
        assertNull("doc3 should NOT be on disk",TestUtils.getContents(this.baseDir,this.uri3));
        assertNotNull("doc3 should be in memory",store.getDocument(this.uri3));
    }
}