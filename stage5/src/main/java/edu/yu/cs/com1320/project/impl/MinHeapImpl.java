package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {

    public MinHeapImpl(){
        elements = (E[]) new Comparable[20];
    }

    @Override
    public void reHeapify(E element) {

//        boolean elementInHeap = false;
//        for(int i = 1; i < this.elements.length; i++){
//            if(this.elements[i] != null) {
//                if(this.elements[i].equals(element)) {
//                    elementInHeap = true;
//                    break;
//                }
//            }
//        }
//        if(elementInHeap == false){
//            throw new NoSuchElementException();
//        }

        getArrayIndex(element); // just checking if element is in the array - if not then will throw NoSuchElementException
        MinHeapImpl<E> newHeap = new MinHeapImpl<>();
        for(E e : this.elements){
            if(e != null){
                newHeap.insert(e);
            }
        }

        for(int i = 1; i < this.elements.length; i++){
            if(this.elements[i] != null) {
                this.elements[i] = newHeap.remove();
            }
        }
    }

    @Override
    protected int getArrayIndex(E element) {
        if (element == null) {
            throw new NoSuchElementException();
        }
        int index = 0;
        boolean inHeap = false;
        for(int i = 1; i < this.elements.length; i++){
            if(elements[i] != null) {
                if(elements[i].equals(element)) {
                    inHeap = true;
                    index = i;
                }
            }
        }
//        if(inHeap == false) {
//            throw new NoSuchElementException();
//        }
        return index;
    }

    @Override
    protected void doubleArraySize() {
        E[] doubleArray = (E[]) new Comparable[this.elements.length * 2];
        E[] temp = this.elements;
        this.elements = doubleArray; // double check with HashTable doubleArray where I had to put
        for(int i = 0; i < (this.elements.length/2); i++){
            doubleArray[i] = temp[i];
        }
    }
}
