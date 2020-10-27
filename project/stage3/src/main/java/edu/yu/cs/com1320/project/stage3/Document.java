package edu.yu.cs.com1320.project.stage3;

import java.net.URI;

public interface Document
{
    /**
     * @return the document as a PDF
     */
    byte[] getDocumentAsPdf();

    /**
     * @return the document as a Plain String
     */
    String getDocumentAsTxt();

    /**
     * @return hash code of the plain text version of the document
     */
    int getDocumentTextHashCode();

    /**
     * @return URI which uniquely identifies this document
     */
    URI getKey();

    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document
     */
    int wordCount(String word);
}