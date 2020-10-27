package edu.yu.cs.com1320.project.stage1;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URI;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.*;

import edu.yu.cs.com1320.project.stage3.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage3.impl.*;
import edu.yu.cs.com1320.project.stage3.*;

public class DocStoreImplSTAGE1Test {

    @Test
    public void overloadTest() throws IOException {
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText0 = "this is the test text for the 0 file";
        String uris0 = "12345";
        URI testUri0 = URI.create(uris0);
        InputStream targetStream0 = new ByteArrayInputStream(testText0.getBytes());
        bigStore.putDocument(targetStream0, testUri0, DocumentStore.DocumentFormat.TXT);

        String testText1 = "this is the test text for the 2nd file";
        String uris1 = "123456789";
        URI testUri1 = URI.create(uris1);
        InputStream targetStream1 = new ByteArrayInputStream(testText1.getBytes());
        bigStore.putDocument(targetStream1, testUri1, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is the test text for the 3rd file";
        String uris2 = "1234567894321";
        URI testUri2 = URI.create(uris2);
        InputStream targetStream2 = new ByteArrayInputStream(testText2.getBytes());
        bigStore.putDocument(targetStream2, testUri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is the test text for the 4th file";
        String uris3 = "1234567891";
        URI testUri3 = URI.create(uris3);
        InputStream targetStream3 = new ByteArrayInputStream(testText3.getBytes());
        bigStore.putDocument(targetStream3, testUri3, DocumentStore.DocumentFormat.TXT);

        System.out.println(bigStore.getDocumentAsTxt(testUri0));
        System.out.println(bigStore.getDocumentAsPdf(testUri0));
        assertEquals(bigStore.getDocumentAsTxt(testUri0), "this is the test text for the 0 file");

        System.out.println(bigStore.getDocumentAsTxt(testUri1));
        System.out.println(bigStore.getDocumentAsPdf(testUri1));
        assertEquals(bigStore.getDocumentAsTxt(testUri1), "this is the test text for the 2nd file");

        System.out.println(bigStore.getDocumentAsTxt(testUri2));
        System.out.println(bigStore.getDocumentAsPdf(testUri2));
        assertEquals(bigStore.getDocumentAsTxt(testUri2), "this is the test text for the 3rd file");

        System.out.println(bigStore.getDocumentAsTxt(testUri3));
        System.out.println(bigStore.getDocumentAsPdf(testUri3));
        assertEquals(bigStore.getDocumentAsTxt(testUri3), "this is the test text for the 4th file");

        bigStore.deleteDocument(testUri2);
        assertEquals(null, bigStore.getDocumentAsTxt(testUri2));

        //bigStore.deleteDocument(testUri3);
        bigStore.putDocument(null, testUri3, DocumentFormat.TXT);
        assertEquals(null, bigStore.getDocumentAsTxt(testUri3));

        //Replace:
        String testText4 = "this is the test text for the 5th file";
        //String uris4 = "123456789";
        //URI testUri4 = URI.create(uris4);
        InputStream targetStream4 = new ByteArrayInputStream(testText4.getBytes());
        bigStore.putDocument(targetStream4, testUri1, DocumentStore.DocumentFormat.TXT);
        assertEquals(bigStore.getDocumentAsTxt(testUri1), "this is the test text for the 5th file");

        //"FB" and "Ea" have the same hashcode
        String testText5 = "FB-test2.1";
        URI testUri5 = URI.create("FB");
        InputStream targetStream5 = new ByteArrayInputStream(testText5.getBytes());
        bigStore.putDocument(targetStream5, testUri5, DocumentStore.DocumentFormat.TXT);

        String testText6 = "Ea-test2.2";
        URI testUri6 = URI.create("Ea");
        InputStream targetStream6 = new ByteArrayInputStream(testText6.getBytes());
        bigStore.putDocument(targetStream6, testUri6, DocumentStore.DocumentFormat.TXT);

        assertEquals("FB-test2.1", bigStore.getDocumentAsTxt(testUri5));
        assertEquals("Ea-test2.2", bigStore.getDocumentAsTxt(testUri6));

        //Adding a PDF:
        String testText7 = "hello this is a test for a PDF";
        URI testUri7 = URI.create("BlaBla");
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream contents = new PDPageContentStream(doc, page);
        contents.beginText();
        contents.setFont(font, 12);
        //contents.newLineAtOffset(100, 700);
        contents.showText(testText7);
        contents.endText();
        contents.close();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        doc.save(byteArrayOutputStream);
        doc.close();
        InputStream input = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        
        bigStore.putDocument(input, testUri7, DocumentFormat.PDF);
        assertEquals("hello this is a test for a PDF", bigStore.getDocumentAsTxt(testUri7)); //trim?

        String testText8 = "hello this is a test for a PDF NUMBER 2";
        URI testUri8 = URI.create("BlaBlaBluBlu123");
        PDDocument doc2 = new PDDocument();
        PDPage page2 = new PDPage();
        doc2.addPage(page2);
        PDFont font2 = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream contents2 = new PDPageContentStream(doc2, page2);
        contents2.beginText();
        contents2.setFont(font2, 12);
        //contents2.newLineAtOffset(100, 700);
        contents2.showText(testText8);
        contents2.endText();
        contents2.close();
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        doc2.save(byteArrayOutputStream2);
        doc2.close();
        InputStream input2 = new ByteArrayInputStream(byteArrayOutputStream2.toByteArray());
        
        bigStore.putDocument(input2, testUri8, DocumentFormat.PDF);
        assertEquals("hello this is a test for a PDF NUMBER 2", bigStore.getDocumentAsTxt(testUri8)); //trim?

        //Get as PDF test:
        String testText9 = "this is the test text for the 10th file";
        URI testUri9 = URI.create("1029384576612343");
        InputStream targetStream9 = new ByteArrayInputStream(testText9.getBytes());
        bigStore.putDocument(targetStream9, testUri9, DocumentStore.DocumentFormat.TXT);
        byte[] bArray = bigStore.getDocumentAsPdf(testUri9);
        PDFTextStripper stripper = new PDFTextStripper();
        String txt = stripper.getText(PDDocument.load(bArray));
        assertEquals("this is the test text for the 10th file", txt.trim());

        //Remove Nothing:
        URI uri = URI.create("1029384576612987654345678");
        InputStream inputRN = null;
        bigStore.putDocument(inputRN, uri, DocumentFormat.TXT);
    }

    @Test
    public void getAsPdf() throws IOException {
        //Get as PDF test:
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText9 = "this is the test text for the 9th file";
        URI testUri9 = URI.create("1029384576612343");
        InputStream targetStream9 = new ByteArrayInputStream(testText9.getBytes());
        bigStore.putDocument(targetStream9, testUri9, DocumentStore.DocumentFormat.TXT);
        byte[] bArray = bigStore.getDocumentAsPdf(testUri9);
        PDFTextStripper stripper = new PDFTextStripper();
        String txt = stripper.getText(PDDocument.load(bArray));
        assertEquals("this is the test text for the 9th file", txt.trim());
    }

    @Test
    public void removeNothing() {
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        URI uri = URI.create("1029384576612");
        InputStream input = null;
        bigStore.putDocument(input, uri, DocumentFormat.TXT);
    }

    @Test
    public void addPdf() {
        //Adding a PDF:
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText7 = "hello this is a test for a PDF";
        URI testUri7 = URI.create("BlaBla");
        try {
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDPageContentStream contents = new PDPageContentStream(doc, page);
            contents.beginText();
            contents.setFont(font, 12);
            //contents.newLineAtOffset(100, 700);
            contents.showText(testText7);
            contents.endText();
            contents.close();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            doc.save(byteArrayOutputStream);
            doc.close();
            InputStream input = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            
            bigStore.putDocument(input, testUri7, DocumentFormat.PDF);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("I/O Error!");
        }
    }

    @Test
    public void collisionTest() {
        //"FB" and "Ea" have the same hashcode
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText5 = "FB-test2.1";
        URI testUri5 = URI.create("FB");
        InputStream targetStream5 = new ByteArrayInputStream(testText5.getBytes());
        bigStore.putDocument(targetStream5, testUri5, DocumentStore.DocumentFormat.TXT);

        String testText6 = "Ea-test2.2";
        URI testUri6 = URI.create("Ea");
        InputStream targetStream6 = new ByteArrayInputStream(testText6.getBytes());
        bigStore.putDocument(targetStream6, testUri6, DocumentStore.DocumentFormat.TXT);

        assertEquals("FB-test2.1", bigStore.getDocumentAsTxt(testUri5));
        assertEquals("Ea-test2.2", bigStore.getDocumentAsTxt(testUri6));
    }


    @Test(expected = IllegalArgumentException.class)
    public void nullInsert() {
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText = "hello this is a test code for a file";
        //String uris = "12345";
        //URI testUri = URI.create(uris);
        InputStream targetStream = new ByteArrayInputStream(testText.getBytes());
        bigStore.putDocument(targetStream, null, DocumentStore.DocumentFormat.TXT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInsert1() {
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText = "hello this is a test code for a file";
        String uris = "12345";
        URI testUri = URI.create(uris);
        InputStream targetStream = new ByteArrayInputStream(testText.getBytes());
        bigStore.putDocument(targetStream, testUri, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInsert2() {
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText = "hello this is a test code for a file";
        String uris = "12345";
        URI testUri = URI.create(uris);
        InputStream targetStream = new ByteArrayInputStream(testText.getBytes());
        bigStore.putDocument(targetStream, testUri, DocumentStore.DocumentFormat.TXT);
        bigStore.getDocumentAsPdf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInsert3() {
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText = "hello this is a test code for a file";
        String uris = "12345";
        URI testUri = URI.create(uris);
        InputStream targetStream = new ByteArrayInputStream(testText.getBytes());
        bigStore.putDocument(targetStream, testUri, DocumentStore.DocumentFormat.TXT);
        bigStore.getDocumentAsTxt(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInsert4() {
        DocumentStoreImpl bigStore = new DocumentStoreImpl();

        String testText = "hello this is a test code for a file";
        String uris = "12345";
        URI testUri = URI.create(uris);
        InputStream targetStream = new ByteArrayInputStream(testText.getBytes());
        bigStore.putDocument(targetStream, testUri, DocumentStore.DocumentFormat.TXT);
        bigStore.deleteDocument(null);
    }

}