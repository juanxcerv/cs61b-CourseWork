/* Implementation of a String Set using a BST.
 * @author Diana Akrami. */

public class BSTStringSet implements GenericSet {

	private class Child {

		private T x;

		protected Child right;

		protected Child left;

		public Child(T y) {
			x = y;
		}
		public T getValue() {
			return x;
		}

		public boolean contains(T s) {
			int compare = s.compareTo(this.x);
			if (compare == 0) {
				return true;
			}
			if (compare < 0) {
				if (left == null) {
					return false;
				}
				return left.contains(s);
			} else {
				if (right == null) {
					return false;
				}
				return right.contains(s);
			}

		}

		public void put(T s) {
			if (contains(s)) {
				return;
			}
			int compare = s.compareTo(this.x);
			if (compare < 0) {
				if (left == null) {
					left = new Child(s);
				} else {
					left.put(s);
				}
			}
			if (compare > 0) {
				if (right == null) {
					right = new Child(s);
				} else {

					right.put(s);
				}
			}

			}
			public void print() {
				System.out.println(this.x);
				if (this.right != null) {
					right.print();
				}
				if (this.right != null) {
					left.print();
				}
			}
		}

	private Child entry = new Child("dummy");

	public boolean contains(T s) {
		return entry.contains(s);
	}

	public void put(T s) {
		entry.put(s);
	}

	public void print() {
		entry.print();
	}
}