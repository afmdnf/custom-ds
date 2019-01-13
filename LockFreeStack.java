import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack<T> {

    private AtomicReference<Node<T>> stack;
    private AtomicInteger tries; // counts the number of setAndCompare calls

    public LockFreeStack() {
        stack = new AtomicReference<>();
        tries = new AtomicInteger();
    }

    public void push(T item) {
        Node<T> currentHead = null;
        Node<T> newHead = new Node<>(item);
        do {
            currentHead = stack.get();
            newHead.next = currentHead;
            tries.getAndIncrement();
        } while (!stack.compareAndSet(currentHead, newHead));
    }

    public T pop() {
        Node<T> currentHead = null;
        Node<T> newHead = null;
        do {
            currentHead = stack.get();
            if (currentHead == null)    return null;
            newHead = currentHead.next;
            tries.getAndIncrement();
        } while (!stack.compareAndSet(currentHead, newHead));
        return currentHead.value;
    }


    private class Node<T> {
        protected T value;
        protected Node<T> next;

        public Node() {
            this(null);
        }

        public Node(T value) {
            this.value = value;
            this.next = null;
        }
    }
}
