package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
//import java.nio.*;
import java.net.URISyntaxException;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * created by the document store and given to the BTree via a call to
 * BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    private File baseDir;

    public DocumentPersistenceManager(File baseDir) {
        if (baseDir == null) {
            this.baseDir = new File(System.getProperty("user.dir"));
        }
        else {
            this.baseDir = baseDir;
        }
    }

    JsonSerializer<Document> jsr = new JsonSerializer<Document>() {

        @Override
        public JsonElement serialize(Document doc, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("docText", doc.getDocumentAsTxt());
            jObject.addProperty("URI", doc.getKey().toString());
            jObject.addProperty("textHashCode", doc.getDocumentTextHashCode());
            Gson gson = new Gson();
            String wordMapStr = gson.toJson(doc.getWordMap());
            jObject.addProperty("wordCountMap", wordMapStr);
            return jObject;
        }
    };

    @Override
    public void serialize(URI uri, Document doc) throws IOException {
        // String of the doc, URI, txtHashcode, wordCountMap

        if (uri == null || doc == null) {
            throw new IllegalArgumentException("Error: cannot serialize something null!");
        }

        JsonElement jElem = jsr.serialize(doc, Document.class, null);

        String filePath = createPath(uri);

        //File file = new File(filePath);
        File file = new File(baseDir, filePath);
        file.getParentFile().mkdirs();
        FileWriter fw = new FileWriter(file);
        fw.write(jElem.toString());
        fw.close();
    }

    JsonDeserializer<Document> jdsr = new JsonDeserializer<Document>() {

        @Override
        public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String docText = json.getAsJsonObject().get("docText").getAsString();
                URI uri = new URI(json.getAsJsonObject().get("URI").getAsString());
                int textHashCode = json.getAsJsonObject().get("textHashCode").getAsInt();
                String wordCountMap = json.getAsJsonObject().get("wordCountMap").getAsString();
                Gson gson = new Gson();
                Type StrIntMap = new TypeToken<HashMap<String, Integer>>(){}.getType();
                Map<String, Integer> wordMap = gson.fromJson(wordCountMap, StrIntMap);
                
                Document doc = new DocumentImpl(uri, docText, textHashCode, true);
                doc.setWordMap(wordMap);
                
                return doc;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    @Override
    public Document deserialize(URI uri) throws IOException {
        //String of the doc, URI, txtHashcode, wordCountMap

        if (uri == null) {
            throw new IllegalArgumentException("Error: uri cannot be null!");
        }
        
        String filePath = createPath(uri);

        //File file = new File(filePath);
        File file = new File(baseDir, filePath);
        
        if (! file.exists()) {
            return null;
        }

        FileReader fr = new FileReader(file);
        JsonElement json = JsonParser.parseReader(fr);

        Document doc = jdsr.deserialize(json, Document.class, null);

        file.delete();
        fr.close();

        this.deleteDirectories(file.getParentFile());

        return doc;
    }

    private void deleteDirectories(File file) {
        if (file.list().length == 0) {
            file.delete();
            if (file.getParentFile() != null || file.getParentFile().exists() || file.getParentFile().isDirectory()) {
                deleteDirectories(file.getParentFile());
            }
        }
    }

    private String createPath(URI uri) {
        // String uriStr = uri.toString();
        // uriStr = uriStr.substring(uriStr.indexOf("//")+1);
        // uriStr = uriStr.trim();

        //String fs = File.separator;
        //String fileStr = this.baseDir.toString();
        String uriHost = uri.getHost();
        String uriPath = uri.getPath();
        
        String filePath;
        if (uriHost == null) {
            filePath = uriPath + ".json";
        }
        else {
            filePath =  uriHost + uriPath + ".json";
        }
        
        return filePath;
    }

    // public static void main(String[] args) throws Exception {
    //     //System.out.println(System.getProperty("user.dir"));
    //     String filePath = "/Users/yonatanberner/Desktop";
    //     File file = new File(filePath);
    //     //System.out.println(file.getAbsolutePath());
    //     DocumentPersistenceManager dpm = new DocumentPersistenceManager(file);
    //     String str = "this is the context of test document for json";
    //     URI uri = new URI("https://www.yu.edu/documents/test/doc2");
    //     DocumentImpl doc = new DocumentImpl(uri, str, str.hashCode());
    //     dpm.serialize(uri, doc);
        
    //     dpm.deserialize(uri);
    // }
}
