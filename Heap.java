import java.util.Arrays;

public class Heap {
	private int capacity;
	private int size;
	private int[] items;

	public Heap(int initialCapacity) {
		capacity = initialCapacity;
		size = 0;
		items = new int[capacity];
	}

	private void ensureCapacity() {
		if (size == capacity) {
			capacity *= 2;
			items = Arrays.copyOf(items, capacity);
		}
	}

	private int getParentIndex(int index) { return (index - 1) / 2; }
	private int getLeftIndex (int index) { return 2*index + 1 ; }
	private int getRightIndex (int index) { return 2*index + 2 ; }

	private boolean hasParent(int index) { return getParentIndex(index) >= 0; }
	private boolean hasLeft(int index) { return getLeftIndex(index) < size; }
	private boolean hasRight(int index) { return getRightIndex(index) < size; }

	private int parent(int index) { return items[getParentIndex(index)]; }
	private int left(int index) { return items[getLeftIndex(index)]; }
	private int right(int index) { return items[getRightIndex(index)]; }

	private void swap(int k1, int k2) {
		int temp = items[k1];
		items[k1] = items[k2];
		items[k2] = temp;
	}

	public int peek() {
		if (size == 0) throw new IllegalStateException();
		return items[0];
	}
	
	public int poll() {
		if (size == 0) throw new IllegalStateException();
		int ans = items[0];
		items[0] = items[size - 1];
		size--;
		sink();
		return ans;
	}	

	public void insert(int item) {
		ensureCapacity();
		items[size++] = item;
		swim();
	}

	private void swim() {
		int index = size - 1;
		while (hasParent(index) && (parent(index) > items[index])) {
			swap(getParentIndex(index), index);
			index = getParentIndex(index);
		}
	}

	private void sink() {
		int index = 0;
		while (hasLeft(index)){
			int smallerIndex = getLeftIndex(index);
			if (hasRight(index) && (right(index) < left(index)))
				smallerIndex = getRightIndex(index);
			
			if (items[index] < items[smallerIndex])
				break;
			else
				swap(smallerIndex, index);
			
			index = smallerIndex;
		}
	}
}
