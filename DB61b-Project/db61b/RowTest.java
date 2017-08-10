package db61b;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import static db61b.Utils.*;
import java.util.ArrayList;

public class RowTest {
    /** Test the Row file. */
    @Test
    public void testRow() {
        Literal p = new Literal("peanut");
        Literal b = new Literal("butter");
        Literal j = new Literal("jelly");
        Literal t = new Literal("time");
        List<Column> pbjt = new ArrayList<Column>();
        pbjt.add(p);
        pbjt.add(b);
        pbjt.add(j);
        pbjt.add(t);

        String[] stuff = new String[4];
        Row row1 = new Row(stuff);
        Row row2 = row1.make(pbjt);

        assertEquals(row2.get(0), "peanut");
        assertEquals(row2.get(1), "butter");
    }
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(RowTest.class));
    }
}
