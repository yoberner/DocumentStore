package edu.yu.cs.com1320.project.stage2.impl;

import java.io.*;
import java.net.URI;
import java.util.function.Function;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage2.*;

//@SuppressWarnings("unused")
public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, DocumentImpl> store = new HashTableImpl<URI, DocumentImpl>();
    private StackImpl<Command> comStack = new StackImpl<Command>(); //what is the diff of having the = here and not??

    @Override
    public void undo() throws IllegalStateException {
        if (this.comStack == null || this.comStack.peek() == null) {
            throw new IllegalStateException("Error: There are no commands to undo!");
        }
        Command com = this.comStack.pop();
        com.undo();
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        if (this.comStack == null) {
            throw new IllegalStateException("Error: There are no commands to undo!");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        Boolean uriDoesNotExist = true;
        StackImpl<Command> tempStack = new StackImpl<Command>();
        int comStackSize = this.comStack.size();
        for (int i = 1; i<= comStackSize; i++) {
            Command com = this.comStack.pop();
            if (com.getUri() == uri) {
                com.undo();
                uriDoesNotExist = false;
                break;
            }
            else {
                tempStack.push(com);
            }
        }
        if (tempStack != null) {
            int tempStackSize = tempStack.size();
            for (int i = 1; i <= tempStackSize; i++) {
                Command com = tempStack.pop();
                this.comStack.push(com);
            }
        }
        if (uriDoesNotExist == true) {
            throw new IllegalStateException("Error: This command does not exist!");
        }
    }

    /**
     * @return the Document object stored at that URI, or null if there is no such Document
     */
    protected Document getDocument(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        return store.get(uri);
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) {
        if (uri == null || format == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        if (format != DocumentFormat.PDF && format != DocumentFormat.TXT) {
            throw new IllegalArgumentException("Error: Invalid Input: Document Format must be PDF or TXT");
        }
        if (input == null) {
            DocumentImpl doc = store.get(uri);
            if (doc == null) { //if the doc dsnt exist
                Function<URI,Boolean> func = (uri1) -> { //lambda (no-op)
                    return true;
                };
                Command com = new Command(uri, func);
                this.comStack.push(com);

                return 0;
            }
            int txtHash = doc.getDocumentTextHashCode();
            deleteDocument(uri);
            return txtHash;
        }

        byte[] bArray = getBarray(input);

        if (format == DocumentFormat.TXT) {
            return inputTXT(input, uri, format, bArray);
        }
        if (format == DocumentFormat.PDF) {
            return inputPDF(input, uri, format, bArray);
        }
        return 0;
    }

    private int inputTXT(InputStream input, URI uri, DocumentFormat format, byte[] bArray) {
        String txt = new String(bArray);
        txt = txt.trim();
        int txtHash = txt.hashCode();
        if (store.get(uri) != null && store.get(uri).getDocumentTextHashCode() == txtHash) { //if the doc already exists and it is the exact same
            Function<URI,Boolean> func = (uri1) -> { //lambda (no-op)
                return true;
            };
            Command com = new Command(uri, func);
            this.comStack.push(com);

            return txtHash; //or: store.get(uri).getDocumentTextHashCode()
        }
        if (store.get(uri) != null) { //if the doc already exists but its diff, so then replace it (by adding it)
            DocumentImpl oldDoc = store.get(uri);
            int oldTxtHash = oldDoc.getDocumentTextHashCode();
            
            DocumentImpl doc = new DocumentImpl(uri, txt, txtHash);
            store.put(uri, doc);

            Function<URI,Boolean> func = (uri1) -> { //lambda
                store.put(uri, oldDoc);
                return true;
            };
            Command com = new Command(uri, func);
            this.comStack.push(com);

            return oldTxtHash;
        }
        DocumentImpl doc = new DocumentImpl(uri, txt, txtHash);
        store.put(uri, doc);

        Function<URI,Boolean> func = (uri1) -> { //lambda
            store.put(uri, null);
            return true;
        };
        Command com = new Command(uri, func);
        this.comStack.push(com);

        return 0;
    }

    private int inputPDF(InputStream input, URI uri, DocumentFormat format, byte[] bArray) {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            String txt = stripper.getText(PDDocument.load(bArray));
            txt = txt.trim();
            int txtHash = txt.hashCode();
            if (store.get(uri) != null && store.get(uri).getDocumentTextHashCode() == txtHash) { //if the doc already exists and it is the exact same
                Function<URI,Boolean> func = (uri1) -> { //lambda (no-op)
                    return true;
                };
                Command com = new Command(uri, func);
                this.comStack.push(com);

                return txtHash; //or: store.get(uri).getDocumentTextHashCode()
            }
            if (store.get(uri) != null) { //if the doc already exists but its diff, so then replace it (by adding it)
                DocumentImpl oldDoc = store.get(uri);
                int oldTxtHash = oldDoc.getDocumentTextHashCode();
                
                DocumentImpl doc = new DocumentImpl(uri, txt, txtHash, bArray);
                store.put(uri, doc);

                Function<URI,Boolean> func = (uri1) -> { //lambda
                    store.put(uri, oldDoc);
                    return true;
                };
                Command com = new Command(uri, func);
                this.comStack.push(com);

                return oldTxtHash;
            }
            DocumentImpl doc = new DocumentImpl(uri, txt, txtHash, bArray);
            store.put(uri, doc);

            Function<URI,Boolean> func = (uri1) -> { //lambda
                store.put(uri, null);
                return true;
            };
            Command com = new Command(uri, func);
            this.comStack.push(com);
            
            return 0;
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

    @Override
    public byte[] getDocumentAsPdf(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        if (store.get(uri) == null) {
            return null;
        }
        DocumentImpl doc = store.get(uri);
        byte[] pdfBytes = doc.getDocumentAsPdf();
        return pdfBytes;
    }

    @Override
    public String getDocumentAsTxt(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        if (store.get(uri) == null) {
            return null;
        }
        DocumentImpl doc = store.get(uri);
        String txt = doc.getDocumentAsTxt();
        return txt;
    }

    @Override
    public boolean deleteDocument(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        if (store.get(uri) == null) {
            Function<URI,Boolean> func = (uri1) -> { //lambda (no-op)
                return true;
            };
            Command com = new Command(uri, func);
            this.comStack.push(com);

            return false;
        }

        DocumentImpl doc = store.get(uri);
        Function<URI,Boolean> func = (uri1) -> { //lambda
            store.put(uri, doc);
            return true;
        };
        Command com = new Command(uri, func);
        this.comStack.push(com);

        store.put(uri, null); //delte it
        return true;
    }

    // public static void main(String[] args) {
    //     // DocumentStoreImpl bigStore = new DocumentStoreImpl();
    //     // HashTableImpl<URI, DocumentImpl> hashTable = new HashTableImpl<URI, DocumentImpl>();
    //     // bigStore.store = hashTable;
    //     // String testText = "hello this is a test code for a file";
    //     // String uris = "12345";
    //     // URI testUri = URI.create(uris);
    //     // InputStream targetStream = new ByteArrayInputStream(testText.getBytes());
    //     // bigStore.putDocument(targetStream, testUri, DocumentFormat.TXT);

    //     // System.out.println(bigStore.getDocumentAsTxt(testUri));
    // }
    
}