package edu.yu.cs.com1320.project.stage5;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;

public interface DocumentStore
{
    /**
     * the two document formats supported by this document store.
     * Note that TXT means plain text, i.e. a String.
     */
    static enum DocumentFormat{
        TXT,PDF
    };
    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the String version of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     */
    int putDocument(InputStream input, URI uri, DocumentFormat format);

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document as a PDF, or null if no document exists with that URI
     */
    byte[] getDocumentAsPdf(URI uri);

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document as TXT, i.e. a String, or null if no document exists with that URI
     */
    String getDocumentAsTxt(URI uri);

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    boolean deleteDocument(URI uri);

    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    void undo() throws IllegalStateException;

    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    void undo(URI uri) throws IllegalStateException;
    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    List<String> search(String keyword);

    /**
     * same logic as search, but returns the docs as PDFs instead of as Strings
     */
    List<byte[]> searchPDFs(String keyword);

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    List<String> searchByPrefix(String keywordPrefix);

    /**
     * same logic as searchByPrefix, but returns the docs as PDFs instead of as Strings
     */
    List<byte[]> searchPDFsByPrefix(String keywordPrefix);

    /**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    Set<URI> deleteAll(String keyword);

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    Set<URI> deleteAllWithPrefix(String keywordPrefix);
    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    void setMaxDocumentCount(int limit);

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    void setMaxDocumentBytes(int limit);
}