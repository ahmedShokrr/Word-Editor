package wordeditor.utils;

/**
 * Generic Stack implementation
 * Thread-safe version for concurrent operations
 */
public class Stack<E> {

    private final Object lock = new Object();
    public int count = 0;
    private int top = -1;
    private int capacity;
    private E[] stackArray;

    @SuppressWarnings("unchecked")
    public Stack(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
            stackArray = (E[]) new Object[capacity];
        } else {
            this.capacity = 100;
            stackArray = (E[]) new Object[100];
        }
    }

    public boolean push(E obj) {
        synchronized (lock) {
            if (top == capacity - 1) {
                System.out.println("Push of " + obj + " failed! Stack overflow.");
                return false;
            } else {
                top++;
                count++;
                stackArray[top] = obj;
                return true;
            }
        }
    }

    public E pop() {
        synchronized (lock) {
            E returnValue = null;

            if (!isEmpty()) {
                returnValue = stackArray[top];
                stackArray[top] = null;
                top--;
                count--;
            } else {
                System.out.println("Pop failed! Stack underflow.");
            }

            return returnValue;
        }
    }

    public E peek() {
        synchronized (lock) {
            if (isEmpty()) {
                System.out.println("Peek failed! Stack is empty.");
                return null;
            }
            return stackArray[top];
        }
    }

    public boolean isEmpty() {
        synchronized (lock) {
            return (top == -1);
        }
    }

    public int size() {
        synchronized (lock) {
            return count;
        }
    }

    public void clear() {
        synchronized (lock) {
            while (!isEmpty()) {
                pop();
            }
        }
    }
}
