public class PriorityQueue<T>
{
    private static class Entry<T> {
        int priority;
        T data;
        
        Entry(int priority, T data) {
            this.priority = priority;
            this.data = data;
        }
    }
    
    private Entry<T>[] heap;
    private int size;
    private static final int INITIAL_CAPACITY = 10;
    
    //stupid IDE things :(
    @SuppressWarnings("unchecked")
    public PriorityQueue()
    {
        // gotta do this cast because Java doesn't like generic arrays
        heap = (Entry<T>[]) new Entry[INITIAL_CAPACITY];
        size = 0;
    }
    //:p
    @SuppressWarnings("unchecked")
    private void resize() {
        // double the array size when we run out of room
        Entry<T>[] newHeap = (Entry<T>[]) new Entry[heap.length * 2];
        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }
    
    public void insert(int priority, T data)
    {
        // make room if needed
        if (size == heap.length) {
            resize();
        }
        
        // stick the new entry at the end
        heap[size] = new Entry<>(priority, data);
        int current = size;
        size++;
        
        // Bubble up: keep swapping with parent until heap property is restored
        while (current > 0) {
            int parent = (current - 1) / 2;
            // if parent is bigger, we're done
            if (heap[current].priority <= heap[parent].priority) {
                break;
            }
            
            // swap with parent and keep going up
            Entry<T> temp = heap[current];
            heap[current] = heap[parent];
            heap[parent] = temp;
            
            current = parent;
        }
    }
    
    public T retrieveMax()
    {
        if (size == 0) {
            return null;  // empty heap
        }
        
        // save the max value to return later
        T maxData = heap[0].data;
        
        //move last element to root and shrink size
        size--;
        heap[0] = heap[size];
        
        int current = 0;
        
        // Bubble down: keep swapping with larger child
        while (2 * current + 1 < size) {
            int left = 2 * current + 1;
            int right = 2 * current + 2;
            int largest = left;
            
            // pick the larger child (if right exists and is bigger)
            if (right < size && heap[right].priority > heap[left].priority) {
                largest = right;
            }
            
            // if current is bigger than both children, we're done
            if (heap[current].priority >= heap[largest].priority) {
                break;
            }
            
            // swap with larger child and keep going down
            Entry<T> temp = heap[current];
            heap[current] = heap[largest];
            heap[largest] = temp;
            
            current = largest;
        }
        
        return maxData;
    }
    
    public void print()
    {
        if (size > 0) {
            printRecursive(0, 0);
        }
    }
    
    private void printRecursive(int curr, int currDepth)
    {
        if (curr >= size) return;
        
        // in-order traversal: left, root, right
        // this gives the tree structure with indentation
        
        // left subtree
        printRecursive(2 * curr + 1, currDepth + 1);
        
        // current node with indents
        for (int i = 0; i < currDepth; i++) {
            System.out.print("    ");
        }
        System.out.println(heap[curr].priority + " : " + heap[curr].data);
        
        // right subtree
        printRecursive(2 * curr + 2, currDepth + 1);
    }
}