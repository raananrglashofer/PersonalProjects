package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.JsonSerializer;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;

import java.util.List;
import java.util.Set;
import java.util.*;
import java.util.function.Function;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import static java.lang.System.nanoTime;
import java.io.File;

public class DocumentStoreImpl implements DocumentStore {
    private BTreeImpl<URI, Document> BTree = new BTreeImpl<>();
    private StackImpl<Undoable> commandStack = new StackImpl<Undoable>();
    private TrieImpl<URI> trie = new TrieImpl<>();
    private MinHeapImpl<DocTemp> heap = new MinHeapImpl<>();
    private int storedHash = 0;
    private int maxDocsStored;
    private int maxBytesStored;
    private int docsCount;
    private int bytesCount;
    private boolean wasMaxDocsSet = false;
    private boolean wasMaxBytesSet = false;
    private Set<URI> docTempSet = new HashSet<>();
    private Map<URI, DocTemp> docTempMap = new HashMap<>();

    private class DocTemp implements Comparable<DocTemp>{
        private URI uri;
        private long lastUse;
        private DocTemp(URI uri){
            this.uri = uri;
            setLastUse(BTree.get(uri).getLastUseTime());
        }
        private long getLastUse(){
            return this.lastUse;
        }
        private void setLastUse(long lastUse){
            this.lastUse = lastUse;
        }
        private URI getUri(){
            return this.uri;
        }
        @Override
        public int compareTo(DocTemp o) {
            if(this.getLastUse() > o.getLastUse()){
                return 1;
            }
            if(this.getLastUse() < o.getLastUse()){
                return -1;
            }
            return 0;
        }
    }

    public DocumentStoreImpl(){
        this.BTree.setPersistenceManager(new DocumentPersistenceManager(null));
    }
    public DocumentStoreImpl(File baseDir){
        this.BTree.setPersistenceManager(new DocumentPersistenceManager(baseDir));
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if(uri == null || format == null){
            throw new IllegalArgumentException();
        }
        if(input == null){
            delete(uri);
            return storedHash;
        }
        DocumentImpl doc = null;
        Map<String, Integer> wordCountMap = null; // empty wordCountMap to please the constructor
        if(format == DocumentFormat.TXT){
            doc = new DocumentImpl(uri, byteToString(tryReadingInput(input)), wordCountMap);
            if(wasMaxBytesSet == true) {
                if (doc.getDocumentTxt().getBytes().length > this.maxBytesStored) {
                    throw new IllegalArgumentException();
                }
                if ((this.bytesCount + doc.getDocumentTxt().getBytes().length) > maxBytesStored) {
                    removeFromEverything();
                }
            }
            this.bytesCount += doc.getDocumentTxt().getBytes().length;
        } else if (format == DocumentFormat.BINARY) {
            doc = new DocumentImpl(uri, tryReadingInput(input));
            // if doc is bigger than size constraint throw IAE
            if(wasMaxBytesSet == true) {
                if (doc.getDocumentBinaryData().length > this.maxBytesStored) {
                    throw new IllegalArgumentException();
                }
                if ((this.bytesCount + doc.getDocumentBinaryData().length) > maxBytesStored) {
                    removeFromEverything();
                }
            }
            this.bytesCount += doc.getDocumentBinaryData().length;
        }
        DocumentImpl data = (DocumentImpl) this.BTree.put(uri, doc);
        outOfBtree(data); //checks if fully out of BTree to see if needs to be removed from Trie
        if(doc != null){
            if(wasMaxDocsSet == true) {
                if (this.docsCount == this.maxDocsStored) {
                    removeFromEverything();
                }
            }
        }
        DocTemp docTemp = new DocTemp(doc.getKey());
        addToHeap(docTemp);
        addToTrie(doc.getKey());
        Document document = get(uri);
        Function<URI, Boolean> undo = (URI uri1) -> {
            removeFromTrie(uri);
            removeFromHeap(docTemp);
            this.BTree.put(uri1, data);
            return true;
        };
        GenericCommand command = new GenericCommand(uri, undo);
        commandStack.push(command);
        if(data != null){
            return data.hashCode();
        } else{
            return 0;
        }
    }

    @Override
    public Document get(URI uri) {
        if(getFromBTree(uri) != null) {
            getFromBTree(uri).setLastUseTime(System.nanoTime());
            return (Document) this.BTree.get(uri);
        }
        return null;
    }

    @Override
    public boolean delete(URI uri) {
        if(getFromBTree(uri) == null){
            return false;
        }
        removeFromTrie(uri); // removes all values of Document in the trie
        DocTemp docTemp = this.docTempMap.get(uri);
//        for(DocTemp d : docTempMap.values()){
//            if(d.getUri().equals(uri)){
//                docTemp = d;
//            }
//        }
//        DocTemp temp = docTemp;// doing this because temp needs to be "final" and it is not earlier
        Document document = getFromBTree(uri);
        removeFromHeap(docTemp);
        if(document.getDocumentBinaryData() != null) {
            bytesCount -= document.getDocumentBinaryData().length; // double check that deletedDoc works here
        } else{
            bytesCount -= document.getDocumentTxt().getBytes().length; // double check that deletedDoc works here
        }
        storedHash = document.hashCode();
        DocumentImpl deletedDoc = (DocumentImpl) this.BTree.put(uri, null);
        Function<URI, Boolean> undo = (URI uri1) -> {
            this.BTree.put(uri, deletedDoc);
            addToTrie(uri);
            addToHeap(docTemp);
            deletedDoc.setLastUseTime(System.nanoTime());
//            if(wasMaxDocsSet && docTempSet.size() > maxDocsStored){
//                removeFromEverything();
//            }
            return true;
        };
        GenericCommand command = new GenericCommand(uri, undo);
        commandStack.push(command);
        return true;
    }

    @Override
    public void undo() throws IllegalStateException {
        if(this.commandStack.size() == 0){
            throw new IllegalStateException();
        }
        this.commandStack.pop().undo();
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        boolean goneInWhile = false;
        boolean uriFound = false;
        StackImpl<Undoable> tempStack = new StackImpl<Undoable>();
        while(this.commandStack.size() > 0){
            goneInWhile = true;
            if(this.commandStack.peek() instanceof GenericCommand) {
                GenericCommand temp = (GenericCommand) this.commandStack.pop();
                if(temp.getTarget().equals(uri)){
                    //genericCommandRemove(temp); // I don't think it matters whether it is a pop or a peek
                    uriFound = true;
                    try {
                        temp.undo();
                    } catch(IllegalStateException e){}
                    putBackStack(tempStack, this.commandStack);
                    return;
                }
                tempStack.push(temp);
            } else{
                CommandSet tempSet = (CommandSet) this.commandStack.pop();
                if(tempSet.containsTarget(uri)) {
                    CommandSet updatedSet = new CommandSet();
                    uriFound = true;
                    Set<GenericCommand> values = new HashSet<>(getCommandSetValues(tempSet));
                    for(GenericCommand g : values){
                        if(g.getTarget().equals(uri)){
                            //     genericCommandRemove(g); // I don't think it matters whether it is a pop or a peek
                            try{
                                g.undo();
                            } catch(IllegalStateException e) {
                            }
                        } else{
                            updatedSet.addCommand(g);
                        }
                    }
                    if(tempSet.size() > 0){
                        tempStack.push(updatedSet);
                    }
                    putBackStack(tempStack, this.commandStack);
                    return;
                }
                tempStack.push(tempSet);
            }
        }
        if(goneInWhile == false || uriFound == false){
            throw new IllegalStateException();}
        putBackStack(tempStack, commandStack);
    }
// The goal with the new comparator is basically this:
    /*
    1. Create URI comparator that does actually sort in anyway
    2. Pass that into trie.getAllSorted and get returned an unsorted list of all the URIs from that node
    3. Use a for each loop to add all the docs with the uris to a list<document>
    4. Now, manually sort the list of documents and return it
     */
    @Override
    public List<Document> search(String keyword) {
        Comparator<URI> comparator = (URI uri1, URI uri2) -> (getFromBTree(uri1).wordCount(keyword) - getFromBTree(uri2).wordCount(keyword));
        //Comparator<URI> doNothingComparator = (URI uri1, URI uri2) -> 0;
        List<URI> urisSorted = this.trie.getAllSorted(keyword, comparator);
        List<Document> sorted = new ArrayList<>();
        for(URI u : urisSorted){
            sorted.add(getFromBTree(u));
        }
        //Collections.sort(sorted, comparator);
//        for(Document d : sorted){
//            d.setLastUseTime(System.nanoTime());
//            this.heap.reHeapify(d.getKey()); // not sure if this is necessary
//        }
        return sorted;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<URI> comparator = (URI uri1, URI uri2) -> (getFromBTree(uri1).wordCount(keywordPrefix) - getFromBTree(uri2).wordCount(keywordPrefix));
        //Comparator<URI> doNothingComparator = (URI uri1, URI uri2) -> 0;
        List<URI> urisSorted = this.trie.getAllWithPrefixSorted(keywordPrefix, comparator);
        List<Document> sorted = new ArrayList<>();
        for(URI u : urisSorted){
            // calling getFromBTree should be updating time and reheapifying
            sorted.add(getFromBTree(u));
        }
        //Collections.sort(sorted, comparator);
        return sorted;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        if (keyword == null) {
            return null;
        }
        // delete docs at the node
        Set<URI> deletedUris = this.trie.deleteAll(keyword);
        CommandSet<GenericCommand> commands = new CommandSet();
        // delete doc from BTree, and delete doc from trie
        for (URI u : deletedUris) {
            Document document = this.BTree.get(u);
            if (document.getDocumentBinaryData() != null) {
                bytesCount -= getFromBTree(u).getDocumentBinaryData().length;
            } else {
                bytesCount -= getFromBTree(u).getDocumentTxt().getBytes().length;
            }
            DocTemp docTemp = new DocTemp(u);
            removeFromHeap(docTemp);
            removeFromTrie(u);
            Document deletedDoc = this.BTree.put(u, null);
            Function<URI, Boolean> undo = (URI uri1) -> {BTree.put(u, deletedDoc); addToTrie(u); addToHeap(docTemp); return true;};
            GenericCommand command = new GenericCommand(u, undo);
            commands.addCommand(command);
        }
        commandStack.push(commands);
        return deletedUris;
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        if(keywordPrefix == null){
            return null;
        }
        Set<URI> deletedUris = this.trie.deleteAllWithPrefix(keywordPrefix);
        CommandSet<GenericCommand> commands = new CommandSet();
        for(URI u : deletedUris){
            Document deletedDoc = this.BTree.put(u, null);
            if(deletedDoc.getDocumentBinaryData() != null) {
                bytesCount -= getFromBTree(u).getDocumentBinaryData().length;
            } else{
                bytesCount -= getFromBTree(u).getDocumentTxt().getBytes().length;
            }
            DocTemp docTemp = new DocTemp(u);
            removeFromHeap(docTemp);
            Function<URI, Boolean> undo = (URI uri1) -> {BTree.put(u, deletedDoc); addToTrie(u); addToHeap(docTemp); return true;};
            GenericCommand command = new GenericCommand(u, undo);
            commands.addCommand(command);
            removeFromTrie(u);
        }
        commandStack.push(commands);
        return deletedUris;
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        if(limit < 0){
            throw new IllegalArgumentException();
        }
        this.wasMaxDocsSet = true;
        this.maxDocsStored = limit;
        while(docsCount > limit) {
            removeFromEverything();
        }
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        if(limit < 0){
            throw new IllegalArgumentException();
        }
        wasMaxBytesSet = true;
        this.maxBytesStored = limit;
        while(bytesCount > limit) {
            removeFromEverything();
        }
    }

    private String byteToString(byte [] bytes){
        String str = new String(bytes);
        return str;
    }

    private byte[] tryReadingInput(InputStream input) throws IOException{
        byte[] byteArray;
        try {
            byteArray = input.readAllBytes();
        } catch (IOException e){
            throw new IOException();
        }
        return byteArray;
    }

    // should i be removing from trie and commandStack or not
    // can i do a try/catch here with this exception --> I think I can because moveToDisk throws
    // exception so I have to deal with it somewhere
    private void removeFromEverything(){
        try{
            DocTemp docTemp = this.heap.remove(); // remove from heap and save uri
            this.docTempSet.remove(docTemp.getUri());
            this.BTree.moveToDisk(docTemp.getUri()); // move from disk
            docsCount--;
        } catch (Exception e) {}
        // removeFromTrie(uri); // remove from trie
        // removeCommand(uri); // remove from CommandStack
    }

    private void addToTrie(URI uri){
        for(String s: getFromBTree(uri).getWords()){
            this.trie.put(s, uri);
        }
    }

    private void removeFromTrie(URI uri){
        for(String s: getFromBTree(uri).getWords()){
            this.trie.delete(s, uri);
        }
    }

    private Document getFromBTree(URI uri){
        Document doc = this.BTree.get(uri);
        if(doc == null){
            return null;
        }
        DocTemp docTemp = new DocTemp(uri);
        // sets nanoTime --> Now, everytime I call BTree.get I know that the time is being reset
        if(!this.docTempSet.contains(uri)){
            this.heap.insert(docTemp);
            docTempSet.add(uri);
            docsCount++;
            if(wasMaxDocsSet && this.docTempSet.size() > maxDocsStored){
                removeFromEverything();
            }
        }
        this.BTree.get(uri).setLastUseTime(System.nanoTime());
        this.heap.reHeapify(docTemp);
        return doc;
    }

    private void addToHeap(DocTemp docTemp){
        this.heap.insert(docTemp);
        this.docTempSet.add(docTemp.getUri());
        this.docTempMap.put(docTemp.getUri(), docTemp);
        getFromBTree(docTemp.getUri()).setLastUseTime(System.nanoTime());
        this.heap.reHeapify(docTemp);
        docsCount++;
    }

    private void removeFromHeap(DocTemp docTemp){
//        while(true){
//            MinHeapImpl tempHeap = new MinHeapImpl<>();
//            DocTemp tempDocTemp = this.heap.remove());
//            if(!tempDocTemp.getUri().equals(docTemp.getUri()){
//                tempHeap.insert(tempDocTemp);
//            }
//            tempHeap.reHeapify(docTemp);
//        }
        docTemp.setLastUse(Long.MIN_VALUE);
        this.heap.reHeapify(docTemp);
        this.heap.remove();
        this.docTempSet.remove(docTemp.getUri());
        this.docTempMap.remove(docTemp.getUri());
        docsCount--;
    }
    private StackImpl putBackStack(StackImpl temp, StackImpl current){
        while(temp.size() > 0){
            Undoable recent = (Undoable) temp.pop();
            current.push(recent);
        }
        return current;
    }
    private Set<GenericCommand> getCommandSetValues(CommandSet<GenericCommand> set){
        Set<GenericCommand> returnSet = new HashSet<>();
        Iterator itr = set.iterator();
        while(itr.hasNext()){
            GenericCommand gc = (GenericCommand) itr.next();
            returnSet.add(gc);
        }
        return returnSet;
    }

    private void outOfBtree(DocumentImpl doc){
        if(doc instanceof JsonSerializer<?>){
            removeFromTrie(doc.getKey());
        }
    }
}
