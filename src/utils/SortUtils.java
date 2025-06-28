package wordeditor.utils;

/**
 * Generic sorting utilities
 * Used for sorting character arrays and suggestions
 */
public class SortUtils {

    /**
     * Insertion sort implementation
     */
    public static <E extends Comparable<E>> void insertionSort(E[] array) {
        int n = array.length;

        for (int i = 1; i < n; i++) {
            E temp = array[i];
            int j = i - 1;

            while (j > -1 && (array[j].compareTo(temp) > 0)) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = temp;
        }
    }

    /**
     * Quick sort implementation for better performance on larger arrays
     */
    public static <E extends Comparable<E>> void quickSort(E[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private static <E extends Comparable<E>> void quickSort(E[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private static <E extends Comparable<E>> int partition(E[] array, int low, int high) {
        E pivot = array[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, high);
        return i + 1;
    }

    private static <E> void swap(E[] array, int i, int j) {
        E temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
