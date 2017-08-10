package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author Juan Cervantes
 */
class Table implements Iterable<Row> {
    /** A new Table named NAME whose columns are give by COLUMNTITLES,
     *  which must be distinct (else exception thrown). */
    Table(String name, String[] columnTitles) {
        _name = name;
        _titles = columnTitles;
        for (int i = 0; i < _titles.length; i++) {
            for (int j = 0; j < _titles.length; j++) {
                if (i == j) {
                    continue;
                } else if (columnTitles[i] == _titles[j]) {
                    throw error("Can't have identical columns my friend");
                }
            }
        }
    }

    /** A new Table named NAME whose column names are give by COLUMNTITLES. */
    Table(String name, List<String> columnTitles) {
        this(name, columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    int numColumns() {
        return _titles.length;
    }

    /** Returns my name. */
    String name() {
        return _name;
    }

    /** Returns a TableIterator over my rows in an unspecified order. */
    TableIterator tableIterator() {
        return new TableIterator(this);
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return rows.iterator();
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    String title(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    int columnIndex(String title) {
        for (int i = 0; i < numColumns(); i++) {
            if (title.compareTo(this.title(i)) == 0) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    int size() {
        return rows.size();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    boolean add(Row row) {
        if (rows.contains(row)) {
            return false;
        } else {
            rows.add(row);
            return true;
        }
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(header, columnNames);
            String currentRow = input.readLine();
            while (currentRow != null) {
                String[] rowItems = currentRow.split(",");
                Row tableRow = new Row(rowItems);
                table.add(tableRow);
                currentRow = input.readLine();
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            for (int i = 0; i < this.numColumns(); i++) {
                sep = sep + _titles[i] + ",";
            }
            sep += "\n";
            output.append(sep);
            sep = "";
            for (Row currr : rows) {
                for (int i = 0; i < currr.size(); i++) {
                    sep = sep + currr.get(i) + ",";
                }
                sep += "\n";
                output.append(sep);
                sep = "";
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the smtandard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        for (Row currRow : rows) {
            String line = "  ";
            for (int i = 0; i < currRow.size(); i++) {
                line += currRow.get(i);
                line += " ";
            }
            System.out.println(line);
        }
    }
    /** My name. */
    private final String _name;
    /** My column titles. */
    private String[] _titles;
    /** My set of rows. */
    private HashSet<Row> rows = new HashSet<Row>();
}

