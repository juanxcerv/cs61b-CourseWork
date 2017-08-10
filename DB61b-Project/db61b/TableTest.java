package db61b;

import static org.junit.Assert.*;
import org.junit.Test;

public class TableTest {

    @Test
    public void testTable() {
        String[] columnTitles = {"Name", "Sport", "Color"};
        String[] row1 = {"Juan", "Running", "Blue"};
        String[] row2 = {"Jimmy", "Golf", "Pink"};
        String[] row3 = {"Kendrick", "Drugs", "Red"};
        Table table1 = new Table("Stuff", columnTitles);
        Row trow1 = new Row(row1);
        Row trow2 = new Row(row2);
        Row trow3 = new Row(row3);

        table1.add(trow1);
        table1.add(trow2);
        assertEquals(2, table1.size());
    }
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(TableTest.class));
    }
}
