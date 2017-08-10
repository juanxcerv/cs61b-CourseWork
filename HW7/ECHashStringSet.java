import java.util.ArrayList;

class ECHashStringSet extends BSTStringSet {
    /** Starting number of buckets. */
    private static final int START = 5;
    /** Loading factor. */
    private static final int LOAD_FACTOR = 5;
    /** Array of ArrayLists. */
    private ArrayList<String>[] buckets;
    /** Number of elements being hashed. */
    private int elements;
    /** max number of elements allowed before resizing. */
    private int max_elements;
    /** Default size array. */
    public ECHashStringSet() {
    	this(START);
    }

    /** makes an array with number of buckets passed in as argument with inital elements 0. */
    public ECHashStringSet(int bucketCount) {
        elements = 0;
        buckets = (ArrayList<String>[]) new ArrayList[bucketCount];
        for (int i = 0; i < bucketCount; i += 1) {
            buckets[i] = new ArrayList<String>();
        }
    }
    /** puts a string into an arrylist within the array corresponsing to the index found by hashchose. */
    public void put(String string) {
        elements += 1;
        int bucketSize = buckets.length;
        max_elements = bucketSize * LOAD_FACTOR;
        if (elements > max_elements) {
            int newBucketCount = bucketSize * 2;
            resize(newBucketCount);
        }
        long posHashCode = hash(string) & 0x7ffffff;
        long currBucket = posHashCode % bucketSize;
        ArrayList<String> bucket = buckets[currBucket];
        if (bucket.contains(string) == false) {
            bucket.add(string);
        }
    }

    /** Changes the size of the Array and adds everything into it again. */
    public void resize(int bucketCount) {
        ECHashStringSet updated = new ECHashStringSet(bucketCount);
        for (int i = 0; i < buckets.length; i++) {
            for (String s : buckets[i]) {
                updated.put(s);
            }
        }
        buckets = updated.buckets;
    }

    /** Checks to see if string is in each bucket. */
    public boolean contains(String string) {
        int bucketSize = buckets.length;
        int currBucket = string.hashCode() % bucketSize;
        ArrayList<String> bucket = buckets[bucketSize];
        return bucket.contains(string);
    }
    /** Hashing for 64 bit. */
    public static long hash(String string) {
        long h = 1125899906842597L;
        int len = string.length();
        for (int i = 0; i < len; i++) {
            h = 31*h + string.charAt(i);
        }
        return h;
    }
}
