package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.DocumentStore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class DocumentStoreAPITest {

    @Test
    public void interfaceCount() {//tests that the class only implements one interface and its the correct one
        @SuppressWarnings("rawtypes")
        Class[] classes = DocumentStoreImpl.class.getInterfaces();
        assertTrue(classes.length == 1);
        assertTrue(classes[0].getName().equals("edu.yu.cs.com1320.project.stage4.DocumentStore"));
    }

    @Test
    public void methodCount() {
        Method[] methods = DocumentStoreImpl.class.getDeclaredMethods();
        int publicMethodCount = 0;
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                publicMethodCount++;
            }
        }
        assertTrue(publicMethodCount == 14);
    }

    @Test
    public void fieldCount() {
        Field[] fields = DocumentStoreImpl.class.getFields();
        int publicFieldCount = 0;
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers())) {
                publicFieldCount++;
            }
        }
        assertTrue(publicFieldCount == 0);
    }

    @Test
    public void subClassCount() {
        @SuppressWarnings("rawtypes")
        Class[] classes = DocumentStoreImpl.class.getClasses();
        assertTrue(classes.length == 0);
    }

    @Test
    public void constructorExists() {
            new DocumentStoreImpl();
    }

    @Test
    public void putDocumentExists(){
        try {
            new DocumentStoreImpl().putDocument(null, new URI("hi"), DocumentStore.DocumentFormat.PDF);
        } catch (URISyntaxException e) {}
    }

    @Test
    public void getDocumentAsPdfExists(){
        try {
            new DocumentStoreImpl().getDocumentAsPdf(new URI("hi"));
        } catch (URISyntaxException e) {}
    }

    @Test
    public void getDocumentAsTxtExists(){
        try {
            new DocumentStoreImpl().getDocumentAsTxt(new URI("hi"));
        } catch (URISyntaxException e) {}
    }

    @Test
    public void deleteDocumentExists(){
        try {
            new DocumentStoreImpl().deleteDocument(new URI("hi"));
        } catch (URISyntaxException e) {}
    }

    @Test
    public void stage2UndoExists(){
        try {
            new DocumentStoreImpl().undo();
        }catch (IllegalStateException e){}
    }

    @Test
    public void stage2UndoByURIExists(){
        try {
            new DocumentStoreImpl().undo(new URI("hi"));
        } catch (URISyntaxException e) {}
        catch (IllegalStateException e){}

}

    @Test
    public void stage2GetDocumentExists(){
        try {
            new DocumentStoreImpl().getDocument(new URI("hi"));
        } catch (URISyntaxException e) {}
    }
    @Test
    public void stage3SearchExists(){
            new DocumentStoreImpl().search("test search");
    }
    @Test
    public void stage3SearchPDFsExists(){
        new DocumentStoreImpl().searchPDFs("test search");
    }
    @Test
    public void stage3DeleteAllExists(){
        new DocumentStoreImpl().deleteAll("test search");
    }
    @Test
    public void stage3SearchByPrefixExists(){
        new DocumentStoreImpl().searchByPrefix("test search");
    }
    @Test
    public void stage3SearchPDFsByPrefixExists(){
        new DocumentStoreImpl().searchPDFsByPrefix("test search");
    }
    @Test
    public void stage3DeleteAllWithPrefixExists(){
        new DocumentStoreImpl().deleteAllWithPrefix("test search");
    }

    //STAGE 4 TESTS
    @Test
    public void stage4SetMaxDocumentCountExists(){
        new DocumentStoreImpl().setMaxDocumentCount(1);
    }
    @Test
    public void stage4SetMaxDocumentBytesExists(){
        new DocumentStoreImpl().setMaxDocumentBytes(1);
    }
}