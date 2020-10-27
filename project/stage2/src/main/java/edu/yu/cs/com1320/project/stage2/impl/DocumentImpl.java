package edu.yu.cs.com1320.project.stage2.impl;

import java.io.*;
import java.net.URI;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;

import edu.yu.cs.com1320.project.stage2.*;

//@SuppressWarnings("unused")
public class DocumentImpl implements Document {
    private URI uri;
    private String txt;
    private int txtHash;
    private byte[] pdfBytes;

    public DocumentImpl(URI uri, String txt, int txtHash) {
        if (uri == null || txt == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        this.uri = uri;
        this.txt = txt;
        this.txtHash = txtHash;
    }

    public DocumentImpl(URI uri, String txt, int txtHash, byte[] pdfBytes) {
        if (uri == null || txt == null || pdfBytes == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        this.uri = uri;
        this.txt = txt;
        this.txtHash = txtHash;
        this.pdfBytes = pdfBytes;
    }

    @Override
    public byte[] getDocumentAsPdf() {
        try {
            if (this.pdfBytes == null) {
                PDDocument doc = txtToPdf(this.txt);
                
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                doc.save(byteArrayOutputStream);
                doc.close();
                InputStream input = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

                this.pdfBytes = getBarray(input);
                input.close();
                byteArrayOutputStream.close();

                // File file = new File(doc);
                // InputStream inputStream = new FileInputStream(file); 
                // byte[] bytes = new byte[(int) file.length()];
                // inputStream.read(bytes);
            }
            return this.pdfBytes;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("I/O Error!");
        }
    }

    private byte[] getBarray(InputStream input) {
        try {
            // byte[] bArray = new byte[input.available()];
            // input.read(bArray);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] bArray = buffer.toByteArray();
            buffer.close();
            return bArray;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("I/O Error!");
        }
    }

    private PDDocument txtToPdf(String txtString) {
        try {
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);
            PDFont font = PDType1Font.HELVETICA_BOLD;
            
            PDPageContentStream contents = new PDPageContentStream(doc, page);
            contents.beginText();
            contents.setFont(font, 12);
            //contents.newLineAtOffset(100, 700);
            contents.showText(txtString);
            contents.endText();
            contents.close();
            //doc.save("txtFile");
            
            return doc;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("I/O Error!");
        }
    }

    @Override
    public String getDocumentAsTxt() {
        String docTxt = this.txt.trim();
        return docTxt;
    }

    @Override
    public int getDocumentTextHashCode() {
        return this.txtHash;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((txt == null) ? 0 : txt.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DocumentImpl other = (DocumentImpl) obj;
        if (txt == null) {
            if (other.txt != null)
                return false;
        } else if (!txt.equals(other.txt))
            return false;
        if (txtHash != other.txtHash)
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }

    // @Override
    // public String toString() {
    //     return "DocumentImpl [txt=" + txt + ", uri=" + uri + "]";
    // }

}