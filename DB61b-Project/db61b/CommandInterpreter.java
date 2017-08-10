package db61b;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static db61b.Utils.*;
import static db61b.Tokenizer.*;

/** An object that reads and interprets a sequence of commands from an
 *  input source.
 *  @author Juan Cervantes */
class CommandInterpreter {
    /* STRATEGY.
     *
     *   This interpreter parses commands using a technique called
     * "recursive descent." The idea is simple: we convert the BNF grammar,
     * as given in the specification document, into a program.
     *
     * First, we break up the input into "tokens": strings that correspond
     * to the "base case" symbols used in the BNF grammar.  These are
     * keywords, such as "select" or "create"; punctuation and relation
     * symbols such as ";", ",", ">="; and other names (of columns or tables).
     * All whitespace and comments get discarded in this process, so that the
     * rest of the program can deal just with things mentioned in the BNF.
     * The class Tokenizer performs this breaking-up task, known as
     * "tokenizing" or "lexical analysis."
     *
     * The rest of the parser consists of a set of functions that call each
     * other (possibly recursively, although that isn't needed for this
     * particular grammar) to operate on the sequence of tokens, one function
     * for each BNF rule. Consider a rule such as
     *
     *    <create statement> ::= create table <table name> <table definition> ;
     *
     * We can treat this as a definition for a function named (say)
     * createStatement.  The purpose of this function is to consume the
     * tokens for one create statement from the remaining token sequence,
     * to perform the required actions, and to return the resulting value,
     * if any (a create statement has no value, just side-effects, but a
     * select clause is supposed to produce a table, according to the spec.)
     *
     * The body of createStatement is dictated by the right-hand side of the
     * rule.  For each token (like create), we check that the next item in
     * the token stream is "create" (and report an error otherwise), and then
     * advance to the next token.  For a metavariable, like <table definition>,
     * we consume the tokens for <table definition>, and do whatever is
     * appropriate with the resulting value.  We do so by calling the
     * tableDefinition function, which is constructed (as is createStatement)
     * to do exactly this.
     *
     * Thus, the body of createStatement would look like this (_input is
     * the sequence of tokens):
     *
     *    _input.next("create");
     *    _input.next("table");
     *    String name = name();
     *    Table table = tableDefinition();
     *    _input.next(";");
     *
     * plus other code that operates on name and table to perform the function
     * of the create statement.  The .next method of Tokenizer is set up to
     * throw an exception (DBException) if the next token does not match its
     * argument.  Thus, any syntax error will cause an exception, which your
     * program can catch to do error reporting.
     *
     * This leaves the issue of what to do with rules that have alternatives
     * (the "|" symbol in the BNF grammar).  Fortunately, our grammar has
     * been written with this problem in mind.  When there are multiple
     * alternatives, you can always tell which to pick based on the next
     * unconsumed token.  For example, <table definition> has two alternative
     * right-hand sides, one of which starts with "(", and one with "as".
     * So all you have to do is test:
     *
     *     if (_input.nextIs("(")) {
     *          _input.next();
     *                                   +
     *         // code to process "<name>,  )"
     *     } else {
     *         // code to process "as <select clause>"
     *     }
     *
     * or for convenience,
     *
     *     if (_input.nextIf("(")) {
     *                                   +
     *         // code to process "<name>,  )"
     *     } else {
     *     ...
     *
     * combining the calls to .nextIs and .next.
     *
     * You can handle the list of <name>s in the preceding in a number
     * of ways, but personally, I suggest a simple loop:
     *
     *     call name() and do something with it;
     *     while (_input.nextIs(",")) {
     *         _input.next(",");
     *         call name() and do something with it;
     *     }
     *
     * or if you prefer even greater concision:
     *
     *     call name() and do something with it;
     *     while (_input.nextIf(",")) {
     *         call name() and do something with it;
     *     }
     *
     * (You'll have to figure out what do with the names you accumulate, of
     * course).
     *
     */

    /** A new CommandParser executing commands read from INP, writing
     *  prompts on PROMPTER, if it is non-null, and using DATABASE
     *  to map names of tables to corresponding Tables. */
    CommandInterpreter(Map<String, Table> database,
                       Scanner inp, PrintStream prompter) {
        _input = new Tokenizer(inp, prompter);
        _database = database;
    }


    /** Parse and execute one statement from the token stream.  Return true
     *  iff the command is something other than quit or exit. */
    boolean statement() {
        switch (_input.peek()) {
        case "create":
            createStatement();
            break;
        case "load":
            loadStatement();
            break;
        case "exit": case "quit":
            exitStatement();
            return false;
        case "*EOF*":
            return false;
        case "insert":
            insertStatement();
            break;
        case "print":
            printStatement();
            break;
        case "select":
            selectStatement();
            break;
        case "store":
            storeStatement();
            break;
        default:
            throw error("unrecognizable command");
        }
        return true;
    }

    /** Parse and execute a create statement from the token stream. */
    private void createStatement() {
        _input.next("create");
        _input.next("table");
        String name = name();
        Table table = tableDefinition(name);
    }

    /** Parse and execute an exit or quit statement. Actually does nothing
     *  except check syntax, since statement() handles the actual exiting. */
    private void exitStatement() {
        if (!_input.nextIf("quit")) {
            _input.next("exit");
        }
    }

    /** Parse and execute an insert statement from the token stream. */
    private void insertStatement() {
        _input.next("insert");
        _input.next("into");
        Table table = tableName();
        _input.next("values");

        ArrayList<String> values = new ArrayList<>();
        values.add(literal());
        while (_input.nextIf(",")) {
            values.add(literal());
        }
        if (values.size() != table.numColumns()) {
            throw error("inserted row has wrong length.");
        }
        table.add(new Row(values.toArray(new String[values.size()])));
        _input.next(";");
    }

    /** Parse and execute a load statement from the token stream. */
    private void loadStatement() {
        _input.next("load");
        String name = name();
        Table table = Table.readTable(name);
        _database.put(name, table);
        System.out.println("Loaded" + " " + name + ".db");
        _input.next(";");
    }

    /** Parse and execute a store statement from the token stream.
    Store the data from the table named tname into the file tname.db.*/
    private void storeStatement() {
        _input.next("store");
        String name = _input.peek();
        Table table = tableName();
        table.writeTable(name);
        System.out.println("Stored" + " " + name + ".db");
        _input.next(";");
    }

    /** Parse and execute a print statement from the token stream. */
    private void printStatement() {
        _input.next("print");
        String name = name();
        _input.next(";");
        if (_database.containsKey(name)) {
            System.out.println("Contents of " + name + ":");
            _database.get(name).print();
        } else {
            throw error("This table does not exist in database");
        }
    }

    /** Parse and execute a select statement from the token stream. */
    private void selectStatement() {
        _input.next("select");
        ArrayList<Column> columns = new ArrayList<Column>();
        ArrayList<String> columnstr = new ArrayList<String>();
        columns.add(columnSelector());
        while (_input.nextIf(",")) {
            columns.add(columnSelector());
        }
        for (int i = 0; i < columns.size(); i++) {
            columnstr.add(columns.get(i).name());
        }
        if (_input.nextIf("as")) {
            table1 = selectClause(null);
            System.out.println("Search results:");
            table1.print();
        } else {
            _input.next("from");
            table1 = new Table(null, columnstr);
            ArrayList<TableIterator> tableiter = new ArrayList<TableIterator>();
            while (!_input.nextIs("where") && !_input.nextIs(";")) {
                if (_input.nextIf(",")) {
                    Table table = tableName();
                    tableiter.add(table.tableIterator());
                } else {
                    Table table = tableName();
                    tableiter.add(table.tableIterator());
                }
            }
            if (_input.nextIf("where")) {
                select(table1, columns, tableiter, conditionClause(tableiter));
            } else {
                select(table1, columns, tableiter, null);
            }
            System.out.println("Search results:");
            table1.print();
        }
    }

    /** Parse and execute a table definition for a Table named NAME,
     *  returning the specified table. */
    Table tableDefinition(String name) {
        ArrayList<String> columns = new ArrayList<String>();
        if (_input.nextIf("(")) {
            columns.add(_input.next());
            while (_input.nextIf(",")) {
                columns.add(_input.next());
            }
            table1 = new Table(name, columns);
            _input.next(")");
            _input.next(";");
        } else {
            _input.next("as");
            _input.next("select");
            ArrayList<Column> cColumns = new ArrayList<>();
            ArrayList<String> cColumnstr = new ArrayList<>();
            cColumns.add(columnSelector());
            while (_input.nextIf(",")) {
                cColumns.add(columnSelector());
            }
            for (int i = 0; i < cColumns.size(); i++) {
                cColumnstr.add(cColumns.get(i).name());
            }
            table1 = new Table(name, cColumnstr);
            _input.next("from");
            ArrayList<TableIterator> tableiter = new ArrayList<TableIterator>();
            Table table = tableName();
            tableiter.add(table.tableIterator());
            while (_input.nextIf(",")) {
                Table table2 = tableName();
                tableiter.add(table2.tableIterator());
            }
            if (_input.nextIf("where")) {
                select(table1, cColumns, tableiter, conditionClause(tableiter));
            } else {
                select(table1, cColumns, tableiter, null);
            }
        }
        _database.put(name, table1);
        return table1;
    }

    /** Parse and execute a select clause from the token stream, returning the
     *  resulting table, with name TABLENAME. */
    Table selectClause(String tableName) {
        ArrayList<Column> newColumns = new ArrayList<Column>();
        ArrayList<String> newColumnstr = new ArrayList<String>();
        newColumns.add(columnSelector());
        while (_input.nextIf(",")) {
            newColumns.add(columnSelector());
        }
        for (int i = 0; i < newColumns.size(); i++) {
            newColumnstr.add(newColumns.get(i).name());
        }
        return new Table(null, newColumnstr);
    }

    /** Parse and return a valid name (identifier) from the token stream.
     *  The identifier need not have a meaning. */
    String name() {
        return _input.next(Tokenizer.IDENTIFIER);
    }

    /** Parse valid column designation (name or table.name), and
     *  return as an unresolved Column. */
    Column columnSelector() {
        String name = _input.next();
        Column col1;
        if (_input.nextIf(".")) {
            col1 = new Column(_database.get(name), _input.next());
        } else {
            col1 = new Column(null, name);
        }
        return col1;
    }

    /** Parse and return a column designator, after resolving against
     *  ITERATORS. */
    Column columnSelector(List<TableIterator> iterators) {
        Column col = columnSelector();
        col.resolve(iterators);
        return col;
    }

    /** Parse a valid table name from the token stream, and return the Table
     *  that it designates, which must be loaded. */
    Table tableName() {
        String name = name();
        Table table = _database.get(name);
        if (table == null) {
            throw error("unknown table: %s", name);
        }
        return table;
    }

    /** Parse a literal and return the string it represents (i.e., without
     *  single quotes). */
    String literal() {
        String lit = _input.next(Tokenizer.LITERAL);
        return lit.substring(1, lit.length() - 1).trim();
    }

    /** Parse and return a list of Conditions that apply to TABLES from the
     *  token stream.  This denotes the conjunction (`and') of zero
     *  or more Conditions.  Resolves all Columns within the clause
     *  against ITERATORS. */
    List<Condition> conditionClause(List<TableIterator> iterators) {
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(condition(iterators));
        while (_input.nextIf("and")) {
            conditions.add(condition(iterators));
        }
        return conditions;
    }

    /** Parse and return a Condition that applies to ITERATORS from the
     *  token stream. */
    Condition condition(List<TableIterator> iterators) {
        Column col1 = columnSelector(iterators);
        String relation = _input.next();
        if (_input.nextIs(Tokenizer.LITERAL)) {
            String val2 = literal();
            return new Condition(col1, relation, val2);
        } else {
            Column col2 = columnSelector(iterators);
            return new Condition(col1, relation, col2);
        }
    }

    /** Fill TABLE with the result of selecting COLUMNS from the rows returned
     *  by ITERATORS that satisfy CONDITIONS.  ITERATORS must have size 1 or 2.
     *  All selected Columns and all Columns mentioned in CONDITIONS must be
     *  resolved to iterators listed among ITERATORS.  The number of
     *  COLUMNS must equal TABLE.columns(). */
    private void select(Table table, ArrayList<Column> columns,
                        List<TableIterator> iterators,
                        List<Condition> conditions) {
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).resolve(iterators);
        }
        if (iterators.size() > 1) {
            for (int j = 0; j < iterators.size(); j++) {
                while (iterators.get(1).hasRow()) {
                    while (iterators.get(0).hasRow()) {
                        String[] row1 = new String[columns.size()];
                        for (int i = 0; i < columns.size(); i++) {
                            if (conditions == null) {
                                row1[i] = columns.get(i).value();
                            } else {
                                if (Condition.test(conditions)) {
                                    row1[i] = columns.get(i).value();
                                }
                            }
                        }
                        if (row1[0] != null) {
                            table.add(new Row(row1));
                        }
                        iterators.get(0).next();
                    }
                    iterators.get(0).reset();
                    iterators.get(1).next();
                }
            }
        } else {
            while (iterators.get(0).hasRow()) {
                String[] row1 = new String[columns.size()];
                for (int i = 0; i < columns.size(); i++) {
                    if (conditions == null) {
                        row1[i] = columns.get(i).value();
                    } else {
                        if (Condition.test(conditions)) {
                            row1[i] = columns.get(i).value();
                        }
                    }
                }
                if (row1[0] != null) {
                    table.add(new Row(row1));
                }
                iterators.get(0).next();
            }
        }
        _input.next(";");
    }

    /** Advance the input past the next semicolon. */
    void skipCommand() {
        while (true) {
            try {
                while (!_input.nextIf(";") && !_input.nextIf("*EOF*")) {
                    _input.next();
                }
                return;
            } catch (DBException excp) {
                /* No action */
            }
        }
    }

    /** The command input source. */
    private Tokenizer _input;
    /** Database containing all tables. */
    private Map<String, Table> _database;
    /** List of Conditions for condition clause. */
    private Table table1;

}
