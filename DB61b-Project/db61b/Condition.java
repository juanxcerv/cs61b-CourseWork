package db61b;

import java.util.List;

/** Represents a single 'where' condition in a 'select' command.
 *  @author Juan Cervantes
*/
class Condition {

    /** Internally, we represent our relation as a 3-bit value whose
     *  bits denote whether the relation allows the left value to be
     *  greater than the right (GT), equal to it (EQ),
     *  or less than it (LT). */
    private static final int GT = 1, EQ = 2, LT = 4;

    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        _col1 = col1;
        _relation = relation;
        _col2 = col2;
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, new Literal(val2));
    }

    /** Assuming that ROWS are rows from the respective tables from which
     *  my columns are selected, returns the result of performing the test I
     *  denote. */
    boolean test() {

        if (_relation.equals("<")) {
            if (_col1.value().compareTo(_col2.value()) < 0) {
                return true;
            }
        }
        if (_relation.equals(">")) {
            if (_col1.value().compareTo(_col2.value()) > 0) {
                return true;
            }
        }
        if (_relation.equals("<=")) {
            if (_col1.value().compareTo(_col2.value()) <= 0) {
                return true;
            }
        }
        if (_relation.equals(">=")) {
            if (_col1.value().compareTo(_col2.value()) >= 0) {
                return true;
            }
        }
        if (_relation.equals("=")) {
            if (_col1.value().compareTo(_col2.value()) == 0) {
                return true;
            }
        }
        if (_relation.equals("!=")) {
            if (_col1.value().compareTo(_col2.value()) != 0) {
                return true;
            }
        }
        return false;
    }

    /** Return true iff all CONDITIONS are satified. */
    static boolean test(List<Condition> conditions) {
        for (int i = 0; i < conditions.size(); i++) {
            if (!conditions.get(i).test()) {
                return false;
            }
        }
        return true;
    }
    /** initialization for column 1 of conditions. */
    private Column _col1;
    /** initialization for relation string of conditions. */
    private String _relation;
    /** initialization for column 2 of conditions. */
    private Column _col2;
}
