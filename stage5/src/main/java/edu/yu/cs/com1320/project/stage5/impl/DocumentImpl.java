
package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;

import java.net.URI;
import java.util.*;
import java.util.HashMap;

public class DocumentImpl implements Document {
    private URI uri;
    private String text;
    private byte[] binaryData;
    private Map<String, Integer> wordCountMap = new HashMap<String, Integer>();
    private Boolean isTxt;
    private long lastTimeUsed;

    public DocumentImpl(URI uri, String txt, Map<String, Integer> wordCountMap){
        if(uri == null || uri.toString().isEmpty()){
            throw new IllegalArgumentException();
        }
        if(txt == null ){//|| txt.isEmpty()){
            throw new IllegalArgumentException();
        }
        if(wordCountMap != null) {
            this.wordCountMap = wordCountMap; // document is being deserialized
        } else{
            toHashMap(txt); //puts all words into HashMap
        }
        this.uri = uri;
        this.text = txt;
        this.isTxt = true;
    }

    public DocumentImpl(URI uri, byte[] binaryData){
        if(uri == null || uri.toString().isEmpty()){
            throw new IllegalArgumentException();
        }
        if(binaryData == null || binaryData.toString().isEmpty()){ // is this allowed / makes sense?
            throw new IllegalArgumentException();
        }
        toHashMap(binaryData.toString()); // will this work?
        this.uri = uri;
        this.binaryData = binaryData;
        this.isTxt = false;
    }
    @Override
    public String getDocumentTxt() {
        return this.text;
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public int wordCount(String word) {
        if(this.isTxt == true) {
            if(wordCountMap.get(word) == null){
                return 0;
            } else{
                return wordCountMap.get(word);
            }
        }
        else{
            return 0;
        }
    }

    @Override
    public Set<String> getWords() {
        Set<String> words = new HashSet<>();
        if(this.isTxt == false){
            return words;
        }
        words = this.wordCountMap.keySet();
        return words;
    }

    @Override
    public long getLastUseTime() {
        return this.lastTimeUsed;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.lastTimeUsed = timeInNanoseconds;
    }

    @Override
    public Map<String, Integer> getWordMap() {
        return new HashMap(this.wordCountMap);
    }

    @Override // pretty sure this is unnecasary because of the constructor - from piazza
    public void setWordMap(Map<String, Integer> wordMap) {
        this.wordCountMap = wordMap;
    }

    @Override
    public int compareTo(Document o) {
        if(o == null){
            throw new NullPointerException();
        }
        if(this.getLastUseTime() > o.getLastUseTime()){
            return 1;
        }
        else if(this.getLastUseTime() == o.getLastUseTime()){
            return 0;
        }
        else{
            return -1;
        }
    }
    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return Math.abs(result);
    }

    //Two documents are considered equal if they have the same hashCode
    @Override
    public boolean equals(Object obj){
        DocumentImpl otherDoc = (DocumentImpl) obj;
        if(this.hashCode() == otherDoc.hashCode()){
            return true;
        }
        return false;
    }

    private void toHashMap(String txt){
        String noPunctuation = txt.replaceAll("\\p{Punct}", ""); // make sure this runs properly
        String[] split = noPunctuation.split(" ");
        for(int i = 0; i < split.length; i++){
            if(wordCountMap.get(split[i]) != null){ // should i do is not null or greater than zero
                int val = wordCountMap.get(split[i]);
                wordCountMap.replace(split[i], val + 1);
            }
            else{
                wordCountMap.put(split[i], 1);
            }
        }
    }
}