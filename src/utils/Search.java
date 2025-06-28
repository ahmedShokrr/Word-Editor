package wordeditor.utils;

import java.util.*;

/**
 * Generic binary search implementation
 * Used for efficient dictionary lookups
 */
public class Search {

    public static <E extends Comparable<E>> int binarySearch(E[] array, E target) {
        return binarySearch(array, target, 0, array.length - 1);
    }

    private static <E extends Comparable<E>> int binarySearch(E[] array, E target, int low, int high) {

        if (low > high)
            return -1;

        int mid = (low + high) / 2;
        int compareValue = array[mid].compareTo(target);

        if (compareValue == 0)
            return mid;
        else if (compareValue > 0)
            return binarySearch(array, target, low, mid - 1);
        else
            return binarySearch(array, target, mid + 1, high);
    }

    /**
     * Linear search for unsorted arrays
     */
    public static <E> int linearSearch(E[] array, E target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}
