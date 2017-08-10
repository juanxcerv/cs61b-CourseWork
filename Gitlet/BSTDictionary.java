/* Implementation of a String Set using a BST.
 * @author Diana Akrami. */

public class BSTStringSet implements StringSet {

	private class Child {

		private String x;

		private String j;

		protected Child right;

		protected Child left;

		public Child(String y, String definition) {
			x = y;
			j = definition;
		}
		public String getValue() {
			return x;
		}

		public boolean contains(String s) {
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

		public void put(String s, String def) {
			int compare = s.compareTo(this.x);
			if (compare == 0) {
				this.defintion = def;
			}
			if (compare < 0) {
				if (left == null) {
					left = new Child(s, def);
				} else {
					left.put(s);
				}
			}
			if (compare > 0) {
				if (right == null) {
					right = new Child(s, def);
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

	public boolean contains(String s) {
		return entry.contains(s);
	}

	public void put(String s) {
		entry.put(s);
	}

	public void print() {
		entry.print();
	}
}