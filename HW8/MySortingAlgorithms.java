import java.util.Arrays;

/**
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Distribution Sort adopted from Algorithms, 
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }        
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
          int N = Math.min(k, array.length);

            for (int i = 0; i < N; i++) {
                for (int j = i; j > 0 && array[j] < array[j-1]; j--) {
                    swap(array, j, j-1);
                }                
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
          int N = Math.min(k, array.length);
            for (int i = 0; i < N; i++) {
                int min = i;
                for (int j = i+1; j < N; j++) {
                    if (array[j] < array[min]) {
                        min = j;
                    }
                }
                swap(array, i, min);
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {            
          int[] aux = new int[array.length];            
          int hi = Math.min(k - 1, array.length - 1);
          sort(array, aux, 0, hi);
        }
        private static void merge(int[] a, int[] aux, int lo, int mid, int hi) {
            for (int k = lo; k <= hi; k++) {
              aux[k] = a[k]; 
            }
            int i = lo, j = mid+1;
            for (int k = lo; k <= hi; k++) {
                if      (i > mid)              a[k] = aux[j++];
                else if (j > hi)               a[k] = aux[i++];
                else if (aux[j] < aux[i])      a[k] = aux[j++];
                else                           a[k] = aux[i++];
            }  
        }

        // mergesort a[lo..hi] using auxiliary array aux[lo..hi]
        private static void sort(int[] a, int[] aux, int lo, int hi) {
            if (hi <= lo) return;
            int mid = lo + (hi - lo) / 2;
            sort(a, aux, lo, mid);
            sort(a, aux, mid + 1, hi);
            merge(a, aux, lo, mid, hi);
        }
        

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Distribution Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class DistributionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int maxVal = Integer.MIN_VALUE;
            int N = Math.min(k, array.length);

            for (int i = 0; i < N; i += 1) {
                if (array[i] > maxVal) {
                    maxVal = array[i];
                }
            }

            int counts[] = new int[maxVal + 1];

            for (int i = 0; i < N; i += 1) {
                int dataPoint = array[i];
                counts[dataPoint] += 1;
            }

            int startingPoints[] = new int[maxVal + 1];
            for (int i = 1; i <= maxVal; i += 1) {
                startingPoints[i] = startingPoints[i-1] + counts[i-1];
            }

            int[] sorted = new int[N];
            for (int i = 0; i < N; i += 1) {
                int dataPoint = array[i];
                int targetLocation = startingPoints[dataPoint];
                sorted[targetLocation] = dataPoint;
                startingPoints[dataPoint] += 1;
            }
           
           for (int i = 0; i < N; i += 1) {
                array[i] = sorted[i];
           }
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Distribution Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // TODO   
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }        
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // TODO   
        }

        @Override
        public String toString() {
            return "Quicksort";
        }        
    }

    /*
     * Your LSD Sort implementation, treating ints
     * as a sequence of 8 bit characters. Or if you want 
     * to do less bit-hacking, you can treat them as strings
     * of decimal digits.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        /** For an example implementation, see Kevin Wayne and 
          * Bob Sedgewick's Algorithms textbook.
          *
          * http://algs4.cs.princeton.edu/51radix/LSD.java.html
          */
        public void sort(int[] a, int k) {
            // TODO
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * Tricky: Your MSD Sort implementation, treating ints
     * as a string of 8 bit characters.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        /** For an example implementation, see Kevin Wayne and 
          * Bob Sedgewick's Algorithms textbook.
          *
          * http://algs4.cs.princeton.edu/51radix/MSD.java.html
          */
        public void sort(int[] a, int k) {
            // TODO
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    // swap a[i] and a[j]
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}


