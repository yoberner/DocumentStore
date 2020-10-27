package edu.yu.cs.com1320.project.stage1.judastests;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unused")
public class Utils {
    /**
     * @param input
     * @return
     */
    public static byte[] toByteArray(InputStream input) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for(int data = input.read(); data != -1; data = input.read()){
                outputStream.write(data);
            }
            byte[] byteArray = outputStream.toByteArray();
            outputStream.close();
            return byteArray;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * converts byte[] of pdf data to text
     */
    public static String pdfDataToText(byte[] pdfBytes) {
        try {
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(PDDocument.load(pdfBytes)).trim();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * converts a string to a PDF doc written out to a byte[]
     */
    public static byte[] textToPdfData(String text) throws IOException {
        //setup document and page
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.beginText();
        PDFont font = PDType1Font.HELVETICA_BOLD;
        content.setFont(font, 10);
        content.newLineAtOffset(20, 20);
        //add text
        content.showText(text);
        content.endText();
        content.close();
        //save to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream.toByteArray();
    }
}
