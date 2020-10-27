package edu.yu.cs.com1320.project.stage2.judastests;

import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.net.URI;

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

    private DocumentStoreImpl createStoreAndPutOne(){
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        ByteArrayInputStream bas1 = new ByteArrayInputStream(this.txt1.getBytes());
        dsi.putDocument(bas1,this.uri1, DocumentStore.DocumentFormat.TXT);
        return dsi;
    }

    private DocumentStoreImpl createStoreAndPutAll(){
        DocumentStoreImpl dsi = new DocumentStoreImpl();
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
        this.txt1 = "This is the text of doc1, in plain text. No fancy file format - just plain old String";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2. A plain old String.";

        //init possible values for doc1
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "This is the text of doc3 - doc doc goose";

        //init possible values for doc2
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "doc4: how much wood would a woodchuck chuck...";
    }

    // @Test
    // public void undoAfterOnePut() throws Exception {
    //     DocumentStoreImpl dsi = createStoreAndPutOne();
    //     //undo after putting only one doc
    //     Document doc1 = new DocumentImpl(this.uri1, this.txt1, this.txt1.hashCode());
    //     Document returned1 = dsi.getDocument(this.uri1);
    //     assertNotNull("Did not get a document back after putting it in",returned1);
    //     assertEquals("Did not get doc1 back",doc1.getKey(),returned1.getKey());
    //     dsi.undo();
    //     returned1 = dsi.getDocument(this.uri1);
    //     assertNull("Put was undone - should have been null",returned1);
    //     try {
    //         dsi.undo();
    //         fail("no documents - should've thrown IllegalStateException");
    //     }catch(IllegalStateException e){}
    // }

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

    // @Test
    // public void undoAfterMultiplePuts() throws Exception {
    //     DocumentStoreImpl dsi = createStoreAndPutAll();
    //     //undo put 4 - test before and after
    //     Document returned = dsi.getDocument(this.uri4);
    //     assertEquals("should've returned doc with uri4",this.uri4,returned.getKey());
    //     dsi.undo();
    //     assertNull("should've been null - put doc4 was undone",dsi.getDocument(this.uri4));

    //     //undo put 3 - test before and after
    //     returned = dsi.getDocument(this.uri3);
    //     assertEquals("should've returned doc with uri3",this.uri3,returned.getKey());
    //     dsi.undo();
    //     assertNull("should've been null - put doc3 was undone",dsi.getDocument(this.uri3));

    //     //undo put 2 - test before and after
    //     returned = dsi.getDocument(this.uri2);
    //     assertEquals("should've returned doc with uri3",this.uri2,returned.getKey());
    //     dsi.undo();
    //     assertNull("should've been null - put doc2 was undone",dsi.getDocument(this.uri2));

    //     //undo put 1 - test before and after
    //     returned = dsi.getDocument(this.uri1);
    //     assertEquals("should've returned doc with uri1",this.uri1,returned.getKey());
    //     dsi.undo();
    //     assertNull("should've been null - put doc1 was undone",dsi.getDocument(this.uri1));
    //     try {
    //         dsi.undo();
    //         fail("no documents - should've thrown IllegalStateException");
    //     }catch(IllegalStateException e){}
    // }

    // @Test
    // public void undoNthPutByURI() throws Exception {
    //     DocumentStoreImpl dsi = createStoreAndPutAll();
    //     //undo put 2 - test before and after
    //     Document returned = dsi.getDocument(this.uri2);
    //     assertEquals("should've returned doc with uri2",this.uri2,returned.getKey());
    //     dsi.undo(this.uri2);
    //     assertNull("should've returned null - put was undone",dsi.getDocument(this.uri2));
    // }

    // @Test
    // public void undoDelete() throws Exception {
    //     DocumentStoreImpl dsi = createStoreAndPutAll();
    //     assertTrue("text was not as expected",dsi.getDocumentAsTxt(this.uri3).equals(this.txt3));
    //     dsi.deleteDocument(this.uri3);
    //     assertNull("doc should've been deleted",dsi.getDocument(this.uri3));
    //     dsi.undo(this.uri3);
    //     assertTrue("should return doc3",dsi.getDocument(this.uri3).getKey().equals(this.uri3));
    // }

    // @Test
    // public void undoNthDeleteByURI() throws Exception {
    //     DocumentStoreImpl dsi = createStoreAndPutAll();
    //     assertTrue("text was not as expected",dsi.getDocumentAsTxt(this.uri3).equals(this.txt3));
    //     dsi.deleteDocument(this.uri3);
    //     dsi.deleteDocument(this.uri2);
    //     assertNull("should've been null",dsi.getDocument(this.uri2));
    //     dsi.undo(this.uri2);
    //     assertTrue("should return doc2",dsi.getDocument(this.uri2).getKey().equals(this.uri2));
    // }

    // @Test
    // public void undoOverwriteByURI() throws Exception {
    //     DocumentStoreImpl dsi = createStoreAndPutAll();
    //     String replacement = "this is a replacement for txt2";
    //     dsi.putDocument(new ByteArrayInputStream(replacement.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     assertTrue("should've returned replacement text",dsi.getDocument(this.uri2).getDocumentAsTxt().equals(replacement));
    //     dsi.undo(this.uri2);
    //     assertTrue("should've returned original text",dsi.getDocument(this.uri2).getDocumentAsTxt().equals(this.txt2));
    // }
}