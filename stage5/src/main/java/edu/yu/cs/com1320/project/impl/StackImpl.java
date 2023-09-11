package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private Node head = new Node();
    private int size = 0;
    private class Node<T>{
        T data;
        Node<T> next;

        private Node(){
        }

        private void setData(T data){
            this.data = data;
        }
        private T getData(){
            return this.data;
        }
        private void setNext(Node next){
            this.next = next;
        }
        private Node getNext(){
            return this.next;
        }
    }
    public StackImpl() {
    }

    @Override
    public void push(T element) {
        Node current = new Node();
        current.setData(element);
        //if (head.next == null) {
        //  current = head.next;
        //} else {
        current.next = head;
        head = current;
        // current.next = this.head.next;
        //this.head.next = current;
        //}
        size++;
    }

    @Override
    public T pop() {
        Node temp = head;
        head = head.next;
        //temp.next = head.next;
        if(temp != null) {
            size--;
        }
        return (T) temp.data;
    }

    @Override
    public T peek() {
        //return (T) head.next.data;
        return (T) head.data;
    }

    @Override
    public int size() {
        return size;
    }
}
