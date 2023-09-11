package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import jakarta.xml.bind.DatatypeConverter;
import java.util.Map;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.Reader;
import com.google.gson.reflect.TypeToken;
import java.nio.file.FileSystem;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    private File directory;
    public DocumentPersistenceManager(File baseDir){
        if(baseDir != null){
            this.directory = baseDir;
        } else{
            this.directory = new File(System.getProperty("user.dir"));
        }
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        if(val == null || uri == null){
            throw new IllegalArgumentException();
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(Document.class, new docSerializer()).serializeNulls().create();
        String filePath = URItoFile(uri);
        File file = new File(this.directory, filePath);
        try{
            Files.createDirectories(Paths.get(file.getParent()));
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            //writer.write(json);
            gson.toJson(val, DocumentImpl.class, writer);
            writer.close();
        } catch (IOException e){
        }
    }

    // if deserialization fails throw RunTimeException
    @Override
    public Document deserialize(URI uri) throws IOException {
        if(uri == null){
            throw new IllegalArgumentException();
        }
        String path = URItoFile(uri);
        File file = new File(this.directory, path);
        this.directory.getParentFile().mkdirs();
        if (!file.exists()) {
            return null;
        }
        try{
            Gson gson = new GsonBuilder().registerTypeAdapter(Document.class, new docDeserializer()).setPrettyPrinting().create();
            FileReader reader = new FileReader(file);
            Document doc = gson.fromJson(reader, DocumentImpl.class);
            doc.setLastUseTime(System.nanoTime());
            reader.close();
            this.delete(uri);
            return doc;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * delete the file stored on disk that corresponds to the given key
     * @param uri
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    @Override
    public boolean delete(URI uri) throws IOException {
        String filePath = URItoFile(uri);
        File deletedFile = new File(this.directory, filePath);
        return deletedFile.delete();
    }
    private String URItoFile(URI uri){
        String toFile = uri.toString().replace("http://", "");
        String path = toFile + ".json";
        return path;
    }

    private class docSerializer implements JsonSerializer<Document>{
        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if(document.getDocumentTxt() != null) {
                jsonObject.addProperty("text", document.getDocumentTxt());
                jsonObject.add("WordCountMap", new Gson().toJsonTree(document.getWordMap()));
            } else{
                String base64Encoded = DatatypeConverter.printBase64Binary(document.getDocumentBinaryData());
                jsonObject.addProperty("binaryData", base64Encoded);
            }
            jsonObject.add("URI", new Gson().toJsonTree(document.getKey()));
            return jsonObject;
        }
    }

    private class docDeserializer implements JsonDeserializer<Document>{
        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            URI uri = new Gson().fromJson(jsonElement.getAsJsonObject().get("URI") , URI.class);
            Map<String, Integer> countMap = new Gson().fromJson(jsonElement.getAsJsonObject().get("WordCountMap"), new TypeToken<Map<String , Integer>>() {}.getType());
            Document doc;
            if(jsonObject.get("text") != null){
                doc = new DocumentImpl(uri, jsonObject.get("text").getAsString(), countMap);
            } else{
                // does String.valueOf really do the job
                byte[] base64Decoded = DatatypeConverter.parseBase64Binary(String.valueOf(jsonObject.get("binaryData")));
                doc = new DocumentImpl(uri, base64Decoded);
            }
            return doc;
        }
    }
}