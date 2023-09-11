package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {

    private static final int alphabetSize = 128;
    private Node root;

    private static class Node<Value>{
        private Set<Value> values = new HashSet<>();
        private Node[] links = new Node[TrieImpl.alphabetSize];
    }
    public TrieImpl(){
        this.root = new Node();
    }
    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        if(key == null){
            throw new IllegalArgumentException();
        }
        if(val == null){
            return;
        }
        else {
            this.root = put(this.root, key, val, 0);
        }
    }

    private TrieImpl.Node put(TrieImpl.Node x, String key, Value val, int d)
    {
        //create a new node
        if (x == null)
        {
            x = new TrieImpl.Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            if(x.values == null) {
                Set<Value> vals = new HashSet<>();
                vals.add(val);
                x.values = vals;
            } else{
                x.values.add(val);
            }
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
//        if(c > 'A'){
//            x.links[c -'A'] = this.put(x.links[c-'A'], key, val, d +1);
//        }
//        else{
//
//        }
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }
    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE SENSITIVE.
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List getAllSorted(String key, Comparator comparator) {
        if(key == null || comparator == null){
            throw new IllegalArgumentException();
        }
        TrieImpl.Node x = this.get(this.root, key, 0);
//            if (x == null)
//            {
//                return null;
//            }
        if(x == null || x.values == null){
            List emptyList = new ArrayList<>();
            return emptyList;
        }
        List sortedList = new ArrayList<>(x.values);
        //List sortedList = Arrays.asList((Value) x.values); // The goal here is to create the list of items in the collection and then sort them
        //Comparator<Value> c = Collections.reverseOrder(); // is this the correct way i am supposed to use comparators?
        Collections.sort(sortedList, comparator);
        return sortedList;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */

    // the same concept as getAllSorted, but just have to start at the node that ends the prefix and take every word from there
    @Override
    public List getAllWithPrefixSorted(String prefix, Comparator comparator) {
        if(prefix == null || comparator == null){
            throw new IllegalArgumentException();
        }
        List prefixSort = new ArrayList<Value>();
        TrieImpl.Node x = this.get(this.root, prefix, 0);
        if (!prefix.isEmpty()) {
            this.collect(get(this.root, prefix, 0), prefix, (ArrayList<Value>) prefixSort);
        }
        //Comparator<Value> c = Collections.reverseOrder();
        Collections.sort(prefixSort, comparator);
        return prefixSort;
    }
    // Is this only going to return the first word from the prefix, or all of them
    private void collect(TrieImpl.Node x, String prefix, ArrayList<Value> list){
        if(x == null){
            return;
        }
        if(x.values != null){
            Set<Value> valuesSet = new HashSet<>(x.values);
            for (Value v : valuesSet) {
                list.add(v);
            }
        }
        for(char c = 0; c < TrieImpl.alphabetSize; c++){
            if(x.links[c] != null){
                // prefix.append(c);
                this.collect(x.links[c], prefix + c, list);
                //prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set deleteAllWithPrefix(String prefix) {
        if(prefix == null){
            throw new IllegalArgumentException();
        }
        Set<Value> deletedPrefix = new HashSet<Value>();
        recursiveDeleteWithPrefix(this.root, prefix, 0, deletedPrefix);
        return deletedPrefix;
    }

    private TrieImpl.Node recursiveDeleteWithPrefix(TrieImpl.Node x, String prefix, int d, Set<Value> set){
        if(x == null) {
            return null;
        }
        Set<Value> valuesSet = new HashSet<>(x.values);
        if(d == prefix.length()){
            for(Value v : valuesSet){
                set.add(v);
                //v = null; // does this set the doc to null
            }
//            set.add((Value) x.values);
            x.values = null; // does this set the doc to null
        }
        else{
            char c = prefix.charAt(d);
            x.links[c] = this.recursiveDeleteWithPrefix(x.links[c], prefix, d + 1, set);
        }
        if(x.values != null){
            for(Value v : valuesSet){
                set.add(v);
                //v = null;
            }
            //set.add((Value) x.values);
            x.values = null; // same as before
        }
        for(int c = 0; c < TrieImpl.alphabetSize; c++){
            if(x.links[c] != null){
                x.links[c].values = null; // does this work
                //     this.recursiveDeleteWithPrefix(x.links[c], set);
            }
        }
        return null; // does this delete the subtree
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set deleteAll(String key) {
        if(key == null){
            throw new IllegalArgumentException();
        }
        TrieImpl.Node deleted = this.get(this.root, key, 0); // does this return the entire collection at the Node?
        if(deleted == null || deleted.values == null){
            Set<Value> emptySet = new HashSet<>();
            return emptySet;
        }
        Set<Value> values = new HashSet<>(deleted.values);
        this.recursiveDelete(this.root, key, 0);
        return values;
    }

    private TrieImpl.Node recursiveDelete(TrieImpl.Node x, String key, int d){
        if(x == null){ // if I am passing in the root won't that always be null??
            return null;
        }
        if(d == key.length()){
            x.values = null;
        }
        else{
            char c = key.charAt(d);
            x.links[c] = this.recursiveDelete(x.links[c], key, d + 1);
        }
        if(x.values != null){
            return x;
        }
        for(int c = 0; c < TrieImpl.alphabetSize; c++){
            if(x.links[c] != null){
                return x;
            }
        }
        return null;
    }
    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     * if val is null throw IAE - done
     */
    @Override
    public Value delete(String key, Value val) {
        if(key == null || val == null){
            throw new IllegalArgumentException();
        }
        TrieImpl.Node deleted = this.get(this.root, key, 0);
        Value toReturn = null;
        if(deleted == null || deleted.values == null || !deleted.values.contains(val)){
            return null;
        }
        else{

            Set<Value> valuesSet = new HashSet<>(deleted.values);
            for(Value v : valuesSet){
                if(v.equals(val)){
                    toReturn = v;
                    deleted.values.remove(v);
                    if(deleted.values.isEmpty()){
                        deleteAll(key);
                    }
                }
            }
        }
        return toReturn;
    }

    private Node get(Node x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }
}