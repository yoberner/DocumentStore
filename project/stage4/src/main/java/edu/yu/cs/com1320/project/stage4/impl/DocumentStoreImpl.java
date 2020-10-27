package edu.yu.cs.com1320.project.stage4.impl;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage4.*;

//@SuppressWarnings("unused")
public class DocumentStoreImpl implements DocumentStore {
    private TrieImpl<Document> docTrie = new TrieImpl<Document>();
    private HashTableImpl<URI, DocumentImpl> store = new HashTableImpl<URI, DocumentImpl>();
    private StackImpl<Undoable> comStack = new StackImpl<Undoable>();
    private String compString; //string passed in to comparators
    private MinHeapImpl<Document> heap = new MinHeapImpl<Document>();
    private Integer maxDocCount;
    private Integer maxDocBytes;
    private int memory;
    private int docCount;
    private long minRunTimeValue;

    public DocumentStoreImpl() {
        this.minRunTimeValue = System.nanoTime();
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Error: limit cannot be negative!");
        }
        this.maxDocCount = limit;
        while (this.maxDocCount < this.docCount) {
            obliterateLeastUsedDoc();
        }
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Error: limit cannot be negative!");
        }
        this.maxDocBytes = limit;
        while (this.maxDocBytes < this.memory) {
            obliterateLeastUsedDoc();
        }
    }

    private void addDocToMemory(DocumentImpl doc) {
        this.heap.insert(doc);
        this.memory = this.memory + doc.getMemory();
        this.docCount++;

        while ((this.maxDocBytes != null && this.memory > this.maxDocBytes) || (this.maxDocCount != null && this.docCount > this.maxDocCount)) {
            obliterateLeastUsedDoc();
        }
    }

    private void subtractDocFromMemory(DocumentImpl doc) {
        doc.setLastUseTime(minRunTimeValue);
        this.heap.reHeapify(doc);
        
        this.heap.removeMin();
        this.memory = this.memory - doc.getMemory();
        this.docCount--;
    }

    private void obliterateLeastUsedDoc() {
        DocumentImpl doc = (DocumentImpl) this.heap.removeMin();
        URI uri = doc.getKey();
        this.store.put(uri, null);
        deleteDocFromTrie(doc);

        this.docCount--;
        this.memory = this.memory - doc.getMemory();

        deleteFromStack(uri);
    }

    private void deleteFromStack(URI uri) {
        //delete the doc from all places in the command stack, without undoing the command
        StackImpl<Undoable> tempStack = new StackImpl<Undoable>();
        int comStackSize = this.comStack.size();
        for (int i = 1; i <= comStackSize; i++) {
            Undoable comType = this.comStack.peek();
            if (comType instanceof GenericCommand) {
                GenericCommand<URI> com = (GenericCommand<URI>) this.comStack.pop();
                if (com.getTarget() == uri) {
                    //do nothing, leave it popped off to delete it
                }
                else {
                    tempStack.push(com);
                }
            }
            if (comType instanceof CommandSet) {
                CommandSet<URI> com = (CommandSet<URI>) this.comStack.pop();
                if (com.containsTarget(uri)) {
                    Iterator<GenericCommand<URI>> it = com.iterator();
                    while (it.hasNext()) {
                        if (it.next().getTarget() == uri) { //should this be a while instead?
                            it.remove();
                        }
                    }
                    if (com.size() != 0) {
                        tempStack.push(com);
                    }
                }
                else {
                    tempStack.push(com);
                }
            }
        }
        if (tempStack != null) {
            int tempStackSize = tempStack.size();
            for (int i = 1; i <= tempStackSize; i++) {
                Undoable com = tempStack.pop();
                this.comStack.push(com);
            }
        }
    }
    
    Comparator<Document> comparator = (Document doc1, Document doc2) -> {
        String word = this.compString;
        int doc1Count = doc1.wordCount(word);
        int doc2Count = doc2.wordCount(word);

        // return doc2Count - doc1Count;
        
        if (doc1Count == doc2Count) {
            return 0;
        }
        else if (doc1Count > doc2Count) {
            return -1;
        }
        else {
            return 1;
        }
    };

    Comparator<Document> prefixComparator = (Document doc1, Document doc2) -> {
        String word = this.compString;
        int doc1Count = ((DocumentImpl) doc1).getPrefixWordCount(word, (DocumentImpl) doc1);
        int doc2Count = ((DocumentImpl) doc2).getPrefixWordCount(word, (DocumentImpl) doc2);

        // return doc2Count - doc1Count;
        
        if (doc1Count == doc2Count) {
            return 0;
        }
        else if (doc1Count > doc2Count) {
            return -1;
        }
        else {
            return 1;
        }
    };

    @Override
    public List<String> search(String keyword) {
        List<String> list = new ArrayList<String>();
        if (keyword == null) {
            return list;
        }
        keyword = stringFixerForTrie(keyword);
        this.compString = keyword;

        long lastUsedTime = System.nanoTime();
        List<Document> docList = this.docTrie.getAllSorted(keyword, comparator);
        for (Document doc : docList) {
            list.add(doc.getDocumentAsTxt());

            doc.setLastUseTime(lastUsedTime);
            this.heap.reHeapify(doc);
        }
        return list;
    }

    @Override
    public List<byte[]> searchPDFs(String keyword) {
        List<byte[]> list = new ArrayList<byte[]>();
        if (keyword == null) {
            return list;
        }
        keyword = stringFixerForTrie(keyword);
        this.compString = keyword;

        long lastUsedTime = System.nanoTime();
        List<Document> docList = this.docTrie.getAllSorted(keyword, comparator);
        for (Document doc : docList) {
            list.add(doc.getDocumentAsPdf());

            doc.setLastUseTime(lastUsedTime);
            this.heap.reHeapify(doc);
        }
        return list;
    }

    @Override
    public List<String> searchByPrefix(String keywordPrefix) {
        List<String> list = new ArrayList<String>();
        if (keywordPrefix == null) {
            return list;
        }
        keywordPrefix = stringFixerForTrie(keywordPrefix);
        this.compString = keywordPrefix;

        long lastUsedTime = System.nanoTime();
        List<Document> docList = this.docTrie.getAllWithPrefixSorted(keywordPrefix, prefixComparator);
        for (Document doc : docList) {
            list.add(doc.getDocumentAsTxt());

            doc.setLastUseTime(lastUsedTime);
            this.heap.reHeapify(doc);
        }
        return list;
    }

    @Override
    public List<byte[]> searchPDFsByPrefix(String keywordPrefix) {
        List<byte[]> list = new ArrayList<byte[]>();
        if (keywordPrefix == null) {
            return list;
        }
        keywordPrefix = stringFixerForTrie(keywordPrefix);
        this.compString = keywordPrefix;

        long lastUsedTime = System.nanoTime();
        List<Document> docList = this.docTrie.getAllWithPrefixSorted(keywordPrefix, prefixComparator);
        for (Document doc : docList) {
            list.add(doc.getDocumentAsPdf());

            doc.setLastUseTime(lastUsedTime);
            this.heap.reHeapify(doc);
        }
        return list;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<URI> uriSet = new HashSet<URI>();
        
        if (keyword == null) {
            URI uri = URI.create("noDoc");
            addBlankUndo(uri);
            return uriSet;
        }
        keyword = stringFixerForTrie(keyword);

        Set<Document> docSet = new HashSet<Document>();
        docSet = this.docTrie.deleteAll(keyword); //deltes all the docs for that word in the trie

        if (docSet == null || docSet.isEmpty()) {
            URI uri = URI.create("noDoc");
            addBlankUndo(uri);
            return uriSet;
        }

        CommandSet<URI> commandSet = new CommandSet<URI>();

        long lastUsedTime = System.nanoTime();
        for (Document doc : docSet) { //for all the docs in the set (i.e. the docs with that word that were deleted):
            URI uri = doc.getKey();
            String docText = doc.getDocumentAsTxt();

            uriSet.add(uri);

            StringTokenizer st = new StringTokenizer(docText);
            while (st.hasMoreTokens()) { //for all words in the doc, delete that doc in the trie
                String word = st.nextToken();
                this.docTrie.delete(word, doc);
            }

            Function<URI, Boolean> func = (uri1) -> { // lambda - addsDeleteUndo
                this.store.put(uri, (DocumentImpl) doc);
                addDocToTrie(docText, doc);
                doc.setLastUseTime(lastUsedTime);
                addDocToMemory((DocumentImpl) doc);
                return true;
            };
            GenericCommand<URI> com = new GenericCommand<URI>(uri, func);
            commandSet.addCommand(com);

            subtractDocFromMemory((DocumentImpl) doc);

            this.store.put(uri, null); //delete the doc from the store
        }

        this.comStack.push(commandSet);

        return uriSet;
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<URI> uriSet = new HashSet<URI>();
        
        if (keywordPrefix == null) {
            URI uri = URI.create("noDoc");
            addBlankUndo(uri);
            return uriSet;
        }
        keywordPrefix = stringFixerForTrie(keywordPrefix);

        Set<Document> docSet = new HashSet<Document>();
        docSet = this.docTrie.deleteAllWithPrefix(keywordPrefix); //deltes all the docs for that prefix in the trie

        if (docSet == null || docSet.isEmpty()) {
            URI uri = URI.create("noDoc");
            addBlankUndo(uri);
            return uriSet;
        }

        CommandSet<URI> commandSet = new CommandSet<URI>();

        long lastUsedTime = System.nanoTime();
        for (Document doc : docSet) { //for all the docs in the set (i.e. the docs with that prefix that were deleted):
            URI uri = doc.getKey();
            String docText = doc.getDocumentAsTxt();

            uriSet.add(uri);

            StringTokenizer st = new StringTokenizer(docText);
            while (st.hasMoreTokens()) { //for all words with those prefixs in the doc, delete that doc in the trie
                String word = st.nextToken();
                this.docTrie.delete(word, doc);
            }

            Function<URI, Boolean> func = (uri1) -> { // lambda - addsDeleteUndo
                this.store.put(uri, (DocumentImpl) doc);
                addDocToTrie(docText, doc);
                doc.setLastUseTime(lastUsedTime);
                addDocToMemory((DocumentImpl) doc);
                return true;
            };
            GenericCommand<URI> com = new GenericCommand<URI>(uri, func);
            commandSet.addCommand(com);

            subtractDocFromMemory((DocumentImpl) doc);

            this.store.put(uri, null); //delete the doc from the store
        }

        this.comStack.push(commandSet);

        return uriSet;
    }

    private String stringFixerForTrie(String str) {
        str = str.replaceAll("[^a-zA-Z0-9\\s]", "");
        str = str.toUpperCase();
        //System.out.println(str);
        return str;
    }

    private boolean deleteDocFromTrie(Document doc) { //deletes only from Trie and without adding undo lambda
        String txt = doc.getDocumentAsTxt();
        txt = stringFixerForTrie(txt);
        StringTokenizer st = new StringTokenizer(txt);
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            this.docTrie.delete(word, doc);
        }
        return true;
    }

    private boolean addDocToTrie(String txt, Document doc) { //adds only to Trie and without adding undo lambda
        txt = stringFixerForTrie(txt);
        this.docTrie.put(txt, doc);
        return true;
    }

    @Override
    public void undo() throws IllegalStateException {
        if (this.comStack == null || this.comStack.peek() == null) {
            throw new IllegalStateException("Error: There are no commands to undo!");
        }
        Undoable com = this.comStack.pop();
        com.undo();
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        if (this.comStack == null || this.comStack.peek() == null) {
            throw new IllegalStateException("Error: There are no commands to undo!");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        Boolean uriDoesNotExist = true;
        StackImpl<Undoable> tempStack = new StackImpl<Undoable>();
        int comStackSize = this.comStack.size();
        for (int i = 1; i <= comStackSize; i++) {
            Undoable comType = this.comStack.peek();
            //URI comUri = null;
            if (comType instanceof GenericCommand) {
                GenericCommand<URI> com = (GenericCommand<URI>) this.comStack.pop();
                if (com.getTarget() == uri) {
                    com.undo();
                    uriDoesNotExist = false;
                    break;
                }
                else {
                    tempStack.push(com);
                }
            }
            if (comType instanceof CommandSet) {
                CommandSet<URI> com = (CommandSet<URI>) this.comStack.pop();
                if (com.containsTarget(uri)) {
                    com.undo(uri);
                    uriDoesNotExist = false;
                    if (com.size() != 0) {
                        tempStack.push(com);
                    }
                    break;
                }
                else {
                    tempStack.push(com);
                }
            }
            // if (comUri == uri) {
            //     com.undo();
            //     uriDoesNotExist = false;
            //     break;
            // }
            // else {
            //     tempStack.push(com);
            // }
        }
        if (tempStack != null) {
            int tempStackSize = tempStack.size();
            for (int i = 1; i <= tempStackSize; i++) {
                Undoable com = tempStack.pop();
                this.comStack.push(com);
            }
        }
        if (uriDoesNotExist == true) {
            throw new IllegalStateException("Error: This command does not exist!");
        }
    }

    /**
     * @return the Document object stored at that URI, or null if there is no such
     *         Document
     */
    protected Document getDocument(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        return this.store.get(uri);
    }

    private void addAddUndo(URI uri, DocumentImpl doc) {
        Function<URI, Boolean> func = (uri1) -> { // lambda
            subtractDocFromMemory(doc);
            this.store.put(uri, null);
            deleteDocFromTrie(doc);
            return true;
        };
        GenericCommand<URI> com = new GenericCommand<URI>(uri, func);
        this.comStack.push(com);
    }

    private void addBlankUndo(URI uri) {
        Function<URI, Boolean> func = (uri1) -> { // lambda (no-op)
            DocumentImpl doc = this.store.get(uri);
            if (doc == null) {
                return true;
            }
            doc.setLastUseTime(System.nanoTime());
            this.heap.reHeapify(doc);
            return true;
        };
        GenericCommand<URI> com = new GenericCommand<URI>(uri, func);
        this.comStack.push(com);
    }

    private void addReplaceUndo(URI uri, String oldDocText, DocumentImpl doc, DocumentImpl oldDoc) {
        Function<URI, Boolean> func = (uri1) -> { // lambda
            subtractDocFromMemory(doc);
            oldDoc.setLastUseTime(System.nanoTime());
            addDocToMemory(oldDoc);
            this.store.put(uri, oldDoc);
            deleteDocFromTrie(doc);
            addDocToTrie(oldDocText, oldDoc); //passes a doc with its old text to the trie
            return true;
        };
        GenericCommand<URI> com = new GenericCommand<URI>(uri, func);
        this.comStack.push(com);
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
            DocumentImpl doc = this.store.get(uri);
            if (doc == null) { // if the doc dsnt exist
                addBlankUndo(uri);
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
        if (this.store.get(uri) != null && this.store.get(uri).getDocumentTextHashCode() == txtHash) { // if the doc already exists and it is the exact same
            addBlankUndo(uri);

            this.store.get(uri).setLastUseTime(System.nanoTime());
            this.heap.reHeapify(this.store.get(uri));
            return txtHash; // or: this.store.get(uri).getDocumentTextHashCode()
        }
        if (this.store.get(uri) != null) { // if the doc already exists but its diff, so then replace it (by adding it)
            DocumentImpl oldDoc = this.store.get(uri);
            int oldTxtHash = oldDoc.getDocumentTextHashCode();

            DocumentImpl doc = new DocumentImpl(uri, txt, txtHash);
            this.store.put(uri, doc);

            deleteDocFromTrie(oldDoc);
            addDocToTrie(txt, doc);

            addReplaceUndo(uri, oldDoc.getDocumentAsTxt(), doc, oldDoc);

            subtractDocFromMemory(oldDoc);
            doc.setLastUseTime(System.nanoTime());
            addDocToMemory(doc);

            return oldTxtHash;
        }
        DocumentImpl doc = new DocumentImpl(uri, txt, txtHash);
        this.store.put(uri, doc);

        addDocToTrie(txt, doc);

        addAddUndo(uri, doc);

        doc.setLastUseTime(System.nanoTime());
        addDocToMemory(doc);

        return 0;
    }

    private int inputPDF(InputStream input, URI uri, DocumentFormat format, byte[] bArray) {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            String txt = stripper.getText(PDDocument.load(bArray));
            txt = txt.trim();
            int txtHash = txt.hashCode();
            if (this.store.get(uri) != null && this.store.get(uri).getDocumentTextHashCode() == txtHash) { // if the doc already exists and it is the exact same
                addBlankUndo(uri);

                this.store.get(uri).setLastUseTime(System.nanoTime());
                this.heap.reHeapify(this.store.get(uri));
                return txtHash; // or: this.store.get(uri).getDocumentTextHashCode()
            }
            if (this.store.get(uri) != null) { // if the doc already exists but its diff, so then replace it (by adding it)
                DocumentImpl oldDoc = this.store.get(uri);
                int oldTxtHash = oldDoc.getDocumentTextHashCode();

                DocumentImpl doc = new DocumentImpl(uri, txt, txtHash, bArray);
                this.store.put(uri, doc);

                deleteDocFromTrie(oldDoc);
                addDocToTrie(txt, doc);

                addReplaceUndo(uri, oldDoc.getDocumentAsTxt(), doc, oldDoc);

                subtractDocFromMemory(oldDoc);
                doc.setLastUseTime(System.nanoTime());
                addDocToMemory(doc);

                return oldTxtHash;
            }
            DocumentImpl doc = new DocumentImpl(uri, txt, txtHash, bArray);
            this.store.put(uri, doc);

            addDocToTrie(txt, doc);

            addAddUndo(uri, doc);

            doc.setLastUseTime(System.nanoTime());
            addDocToMemory(doc);

            return 0;
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("I/O Error!");
        }
    }

    @Override
    public byte[] getDocumentAsPdf(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        if (this.store.get(uri) == null) {
            return null;
        }
        DocumentImpl doc = this.store.get(uri);
        byte[] pdfBytes = doc.getDocumentAsPdf();

        doc.setLastUseTime(System.nanoTime());
        this.heap.reHeapify(doc);
        return pdfBytes;
    }

    @Override
    public String getDocumentAsTxt(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        if (this.store.get(uri) == null) {
            return null;
        }
        DocumentImpl doc = this.store.get(uri);
        String txt = doc.getDocumentAsTxt();

        doc.setLastUseTime(System.nanoTime());
        this.heap.reHeapify(doc);
        return txt;
    }

    @Override
    public boolean deleteDocument(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Error: Invalid Input: NULL!");
        }
        if (this.store.get(uri) == null) {
            addBlankUndo(uri);

            return false;
        }

        DocumentImpl doc = this.store.get(uri);
        
        final String docText = doc.getDocumentAsTxt();
        Function<URI, Boolean> func = (uri1) -> { // lambda - addsDeleteUndo
            this.store.put(uri, doc);
            addDocToTrie(docText, doc);
            doc.setLastUseTime(System.nanoTime());
            addDocToMemory(doc);
            return true;
        };
        GenericCommand<URI> com = new GenericCommand<URI>(uri, func);
        this.comStack.push(com);

        deleteDocFromTrie(doc);

        subtractDocFromMemory(doc);

        this.store.put(uri, null); // delte it
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