package byog.Core;


import java.io.Serializable;

public class ArrayDeque<T> implements Deque<T>, Serializable {
    //private static final long serialVersionUID = 23483248274L;
    private AList deque;
    private int size;
    private static final int RFACTOR = 2;

    /**
     * creates an array list
     * stores first/last cursors (default: 0/1)
     * curFirst/Last points to first/last item
     */
    private class AList implements Serializable {
       // private static final long serialVersionUID = 39458047305L;
        private T[] array;
        private int first;
        private int curFirst;
        private int last;
        private int curLast;
        private int capacity;

        private AList(int boxes) {
            capacity = boxes;
            array = (T[]) new Object[capacity];
            first = 0;
            last = readNext(first);
            curFirst = last;
            curLast = first;
        }

        /**Update curFirst/curLast by replacement
         * x = 1 if going forward, x = 0 if going backward
         * ReadNext/Prev uses circular indexing
        */
        private void updateFirst(int x) {
            if (x == 1) {
                curFirst = first;
                first = readPrev(first);
            }
            if (x == 0) {
                first = curFirst;
                curFirst = readNext(curFirst);
            }
        }
        private void updateLast(int x) {
            if (x == 1) {
                curLast = last;
                last = readNext(last);
            }
            if (x == 0) {
                last = curLast;
                curLast = readPrev(curLast);
            }
        }

        //return item at index
        //check if deque is empty
        private T retriever(int index) {
            if (size == 0) { //overlap
                return null;
            }
            return array[index];
        }

        //put item to index
        private void itemPlacer(int index, T item) {
            array[index] = item;
        }

        //Reads circular index
        //iterates to next index in direction of first --> last
        private int readNext(int index) {
            if (index == array.length - 1) {
                return 0;
            }
            return index + 1;
        }

        //iterates to previous index in direction of last --> first
        private int readPrev(int index) {
            if (index == 0) {
                return array.length - 1;
            }
            return index - 1;
        }

        //transform deque index to array index
        private int convertIndex(int dIndex) {
            int aIndex = curFirst;
            for (int i = 0; i < dIndex; i += 1) {
                aIndex = readNext(aIndex);
            }
            return aIndex;
        }

        //reset cursors and item pointers after resizing
        private void cursorReset() {
            curFirst = 0;
            first = readPrev(curFirst);
            curLast = size - 1;
            last = readNext(curLast);
        }

        //reset pointers, put deque onto new array starting at 0
        //split data set into 2 if curFirst > curLast
        private void resize(int newCapacity) {
            T[] newArray = (T[]) new Object[newCapacity];
            int segment = array.length - curFirst;
            if (curFirst > curLast) {
                System.arraycopy(array, curFirst, newArray, 0, segment);
                System.arraycopy(array, 0, newArray, segment, size - segment);
            } else {
                System.arraycopy(array, curFirst, newArray, 0, size);
            }
            array = newArray;
            cursorReset();
            capacity = newCapacity;
        }
    }

    //creates empty ArrayDeque with 8 spaces and no item
    public ArrayDeque() {
        deque = new AList(8);
        size = 0;
    }

    //API
    @Override
    public void addFirst(T item) {
        if (size == deque.capacity) {
            deque.resize(size * RFACTOR);
        }
        deque.itemPlacer(deque.first, item);
        deque.updateFirst(1);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == deque.capacity) {
            deque.resize(size * RFACTOR);
        }
        deque.itemPlacer(deque.last, item);
        deque.updateLast(1);
        size += 1;
    }

    @Override
    public boolean isEmpty() { //remove conditional
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int index = deque.curFirst;
        if (size == 0) {
            System.out.print((String) null);
        }
        for (int i = 0; i < size; i += 1) {
            System.out.print(deque.retriever(index) + " ");
            index = deque.readNext(index);
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T firstItem = deque.retriever(deque.curFirst);
        deque.updateFirst(0);
        size -= 1;
        if ((size < deque.capacity / 4) && deque.capacity > 16) {
            deque.resize(deque.capacity / RFACTOR);
        }
        return firstItem;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T lastItem = deque.retriever(deque.curLast);
        deque.updateLast(0);
        size -= 1;
        if ((size < deque.capacity / 4) && deque.capacity > 16) {
            deque.resize(deque.capacity / RFACTOR);
        }
        return lastItem;
    }

    @Override
    public T get(int index) {
        int convertedIndex = deque.convertIndex(index);
        return deque.retriever(convertedIndex);
    }

}
