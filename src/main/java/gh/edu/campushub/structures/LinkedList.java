package gh.edu.campushub.structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** A doubly linked list built from scratch (no java.util.LinkedList). */
public class LinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /** Inserts {@code value} immediately after the first node holding {@code target}. */
    public void insertAfter(T target, T value) {
        Node<T> node = findNode(target);
        if (node == null) {
            throw new NoSuchElementException("target not found: " + target);
        }
        Node<T> newNode = new Node<>(value);
        newNode.prev = node;
        newNode.next = node.next;
        if (node.next != null) {
            node.next.prev = newNode;
        } else {
            tail = newNode;
        }
        node.next = newNode;
        size++;
    }

    /** Removes the first node holding {@code value}. Returns true if a node was removed. */
    public boolean remove(T value) {
        Node<T> node = findNode(value);
        if (node == null) {
            return false;
        }
        unlink(node);
        return true;
    }

    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("list is empty");
        }
        T value = head.value;
        unlink(head);
        return value;
    }

    public T removeLast() {
        if (tail == null) {
            throw new NoSuchElementException("list is empty");
        }
        T value = tail.value;
        unlink(tail);
        return value;
    }

    public T peekFirst() {
        if (head == null) {
            throw new NoSuchElementException("list is empty");
        }
        return head.value;
    }

    public T peekLast() {
        if (tail == null) {
            throw new NoSuchElementException("list is empty");
        }
        return tail.value;
    }

    public boolean contains(T value) {
        return findNode(value) != null;
    }

    private Node<T> findNode(T value) {
        Node<T> cur = head;
        while (cur != null) {
            if (java.util.Objects.equals(cur.value, value)) {
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }

    private void unlink(Node<T> node) {
        Node<T> prev = node.prev;
        Node<T> next = node.next;
        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }
        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
        node.prev = node.next = null;
        size--;
    }

    public void clear() {
        head = tail = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> cursor = head;

            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T value = cursor.value;
                cursor = cursor.next;
                return value;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> cur = head;
        while (cur != null) {
            sb.append(cur.value);
            if (cur.next != null) sb.append(", ");
            cur = cur.next;
        }
        return sb.append(']').toString();
    }
}
