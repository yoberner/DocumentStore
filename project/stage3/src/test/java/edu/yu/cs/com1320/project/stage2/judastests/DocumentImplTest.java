package edu.yu.cs.com1320.project.stage2.judastests;

//import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DocumentImplTest {
    private URI textUri;
    private String textString;
    private int textHashCode;

    private URI pdfUri;
    private String pdfString;
    private int pdfHashCode;
    private byte[] pdfData;

    @Before
    public void setUp() throws Exception {
        this.textUri = new URI("http://edu.yu.cs/com1320/txt");
        this.textString = "This is text content. Lots of it.";
        this.textHashCode = this.textString.hashCode();

        this.pdfUri = new URI("http://edu.yu.cs/com1320/pdf");
        this.pdfString = "This is a PDF, brought to you by Adobe.";
        this.pdfHashCode = this.pdfString.hashCode();
        this.pdfData = Utils.textToPdfData(this.pdfString);
    }

    @Test
    public void testGetTextDocumentAsTxt() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        assertEquals(this.textString, textDocument.getDocumentAsTxt());
    }

    @Test
    public void testGetPdfDocumentAsTxt() {
        DocumentImpl pdfDocument = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        assertEquals(this.pdfString, pdfDocument.getDocumentAsTxt());
    }

    @Test
    public void testGetTextDocumentAsPdf() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        byte[] pdfBytes = textDocument.getDocumentAsPdf();
        String textAsPdfString = Utils.pdfDataToText(pdfBytes);
        assertEquals(this.textString, textAsPdfString);
    }

    @Test
    public void testGetPdfDocumentAsPdf() {
        DocumentImpl pdfDocument = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        byte[] pdfBytes = pdfDocument.getDocumentAsPdf();
        String pdfAsPdfString = Utils.pdfDataToText(pdfBytes);
        assertEquals(this.pdfString, pdfAsPdfString);
    }

    @Test
    public void testGetTextDocumentTextHashCode() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        assertEquals(this.textHashCode, textDocument.getDocumentTextHashCode());
    }

    @Test
    public void testGetPdfDocumentTextHashCode() {
        DocumentImpl pdfDocument = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        assertEquals(this.pdfHashCode, pdfDocument.getDocumentTextHashCode());
        assertNotEquals(this.pdfHashCode, 25);
    }

    @Test
    public void testGetTextDocumentKey() {
        DocumentImpl textDocument = new DocumentImpl(this.textUri, this.textString, this.textHashCode);
        assertEquals(this.textUri, textDocument.getKey());
        URI fakeUri = null;
        try {
            fakeUri = new URI("http://wrong.com");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assertNotEquals(this.textUri, fakeUri);
    }

    @Test
    public void testGetPdfDocumentKey() {
        DocumentImpl pdfDocument = new DocumentImpl(this.pdfUri, this.pdfString, this.pdfHashCode, this.pdfData);
        assertEquals(this.pdfUri, pdfDocument.getKey());
        URI fakeUri = null;
        try {
            fakeUri = new URI("http://wrong.com");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assertNotEquals(this.pdfUri, fakeUri);
    }

}