/** A binary search tree. */
public class BST {

	protected int label;
	protected BST left, right;
/** A leaf node with given LABEL */
	public BST(int label) { 
		this(label, null, null);
	}
/** Fetch the label of this node. */
	public int label();
/** Fetch the left (right) child of this. */
	public BST left() {
		return left;
	}
	
	public BST right() {
		return right;
	}

	/** True iff label L is in T. */
	public static boolean isIn(BST T, int L) {
		return find (T, L) != null; 
	}
/** Delete the instance of label L from T that is closest to
* to the root and return the modified tree. The nodes of
* the original tree may be modified. */
	public static BST remove(BST T, int L) {
        if (T == null) {
            return null;
        }
        if (L < T.label) {
            T.left = remove(T.left, L);
        } else if (L > T.label) {
        T.right = remove(T.right, L);
        }
        // Otherwise, we’ve found L
        else if (T.left == null) {
            return T.right;
        } else if (T.right == null) {
            return T.left;
        } else {
            T.right = swapSmallest(T.right, T);
            return T;
        }
	}
/** Insert the label L into T, returning the modified tree.
* The nodes of the original tree may be modified.... */
    static BST insert(BST T, int L) {
        if (T == null) {
            return new BST (L, null, null);
        }
        if (L < T.label) {
            T.left = insert(T.left, L);
        } else {
            T.right = insert(T.right, L);
        }
        return T;
    }
    /** The highest node in T that contains the
      * label L, or null if there is none. */
    public static BST find(BST T, int L) {
        if (T == null || L == T.label) {
            return T;
        } else if (L < T.label) {
            return find(T.left, L);
        } else {
            return find(T.right, L);
        }
    }
    /** This constructor is private to force all BST creation
      * to be done by the insert method. */
    private BST(int label, BST left, BST right) {
        this.label = label; 
        this.left = left; 
        this.right = right;
    }
}
