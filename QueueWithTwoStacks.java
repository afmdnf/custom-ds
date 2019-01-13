import java.util.Stack;

public static class QueueWithTwoStacks<T> {
    Stack<T> stackNewestOnTop = new Stack<T>();
    Stack<T> stackOldestOnTop = new Stack<T>();

    public void enqueue(T value) { // Push onto newest stack
        stackNewestOnTop.push(value);
    }
    public T peek() {
        prepOld();
        return stackOldestOnTop.peek();
    }
    public T dequeue() {
        prepOld();
        return stackOldestOnTop.pop();
    }
    private void prepOld(){
        if (stackOldestOnTop.isEmpty()) {
            while(!stackNewestOnTop.isEmpty())
                stackOldestOnTop.push(stackNewestOnTop.pop());
        }
    }

    public static void main(String[] args) {
        QueueWithTwoStacks<Integer> queue = new QueueWithTwoStacks<Integer>();
        queue.enqueue(5);
        System.out.println(queue.peek());
        queue.enqueue(6);
        queue.enqueue(7);
        System.out.println(queue.dequeue());
        System.out.println(queue.peek());
        System.out.println(queue.dequeue());
        System.out.println(queue.dequeue());
    }
}
