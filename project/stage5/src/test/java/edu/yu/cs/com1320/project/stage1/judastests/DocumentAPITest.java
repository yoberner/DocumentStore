package edu.yu.cs.com1320.project.stage1.judastests;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;

import edu.yu.cs.com1320.project.stage5.impl.*;
import org.junit.Test;

public class DocumentAPITest {


    @Test
    public void interfaceCount() {//tests that the class only implements one interface and its the correct one
        @SuppressWarnings("rawtypes")
        Class[] classes = DocumentImpl.class.getInterfaces();
        assertTrue(classes.length == 1);
        assertTrue(classes[0].getName().equals("edu.yu.cs.com1320.project.stage5.Document"));
    }

    @Test
    public void methodCount() {//need only test for non constructors
        Method[] methods = DocumentImpl.class.getDeclaredMethods();
        int publicMethodCount = 0;
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                if(!method.getName().equals("equals") && !method.getName().equals("hashCode")) {
                    publicMethodCount++;
                }
            }
        }
        assertTrue(publicMethodCount == 11);
    }

    @Test
    public void fieldCount() {
        Field[] fields = DocumentImpl.class.getFields();
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
        Class[] classes = DocumentImpl.class.getClasses();
        assertTrue(classes.length == 0);
    }

    @Test
    public void constructor1Exists() throws URISyntaxException {
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi", 1);
        } catch (RuntimeException e) {}
    }

    @Test
    public void constructor2Exists() throws URISyntaxException {
        URI uri = new URI("https://this.com");
        byte[] ary = {0,0,0};
        try {
            new DocumentImpl(uri, "hi", 1, ary );
        } catch (RuntimeException e) {}
    }

    @Test
    public void getDocumentTextHashCodeExists() throws URISyntaxException{
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi", 1).getDocumentTextHashCode();
        } catch (RuntimeException e) {}
    }

    @Test
    public void getDocumentAsPdfExists() throws URISyntaxException{
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi", 1).getDocumentAsPdf();
        } catch (RuntimeException e) {}
    }

    @Test
    public void getDocumentAsTxtExists() throws URISyntaxException{
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi", 1).getDocumentAsTxt();
        } catch (RuntimeException e) {}
    }

    @Test
    public void getKeyExists() throws URISyntaxException {
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi", 1).getKey();
        } catch (RuntimeException e) {}
    }
}