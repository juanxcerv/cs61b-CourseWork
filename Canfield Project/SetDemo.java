import java.util.HashSet;


public class SetDemo {

	public static void main(String... args) {
		Set<String> bears = new HashSet<String>();
		bears.add("baby");
		bears.add("bear");
		bears.add("papa");
		bears.add("bear");
		bears.add("mama");
		bears.add("bear");
		Iterator iter = bears.iterator();
		while (bears.hasNext()) {
			System.out.println(iter.next());
		}
	}
}
