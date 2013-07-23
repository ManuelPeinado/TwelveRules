package com.manuelpeinado.sql_dsl;

import static com.manuelpeinado.sql_dsl.Constants.AND;
import static com.manuelpeinado.sql_dsl.Constants.BETWEEN;
import static com.manuelpeinado.sql_dsl.Constants.COMMA;
import static com.manuelpeinado.sql_dsl.Constants.LEFT_PARENTHESIS;
import static com.manuelpeinado.sql_dsl.Constants.RIGHT_PARENTHESIS;
import static com.manuelpeinado.sql_dsl.Constants.SPACE;

public class Field extends DBObject<Field> {

    protected Field(String expression) {
        super(expression);
    }

    public static Field field(String expression) {
        return new Field(expression);
    }

    public Criterion eq(Object value) {
        return UnaryCriterion.eq(this, value);
    }

    public Criterion neq(Object value) {
        return UnaryCriterion.neq(this, value);
    }

    public Criterion gt(Object value) {
        return UnaryCriterion.gt(this, value);
    }

    public Criterion lt(final Object value) {
        return UnaryCriterion.lt(this, value);
    }

    public Criterion isNull() {
        return UnaryCriterion.isNull(this);
    }

    public Criterion isNotNull() {
        return UnaryCriterion.isNotNull(this);
    }

    public Criterion between(final Object lower, final Object upper) {
        final Field field = this;
        return new Criterion(null) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(BETWEEN).append(SPACE).append(lower).append(SPACE).append(AND)
                        .append(SPACE).append(upper);
            }
        };
    }

    public Criterion like(final String value) {
        return UnaryCriterion.like(this, value);
    }

    public <T> Criterion in(final T... value) {
        final Field field = this;
        return new Criterion(Operator.in) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(Operator.in).append(SPACE).append(LEFT_PARENTHESIS);
                for (T t : value) {
                    sb.append(t.toString()).append(COMMA);
                }
                sb.deleteCharAt(sb.length() - 1).append(RIGHT_PARENTHESIS);
            }
        };
    }

    public Criterion in(final Field expression, final Query query) {
        final Field field = this;
        return new Criterion(Operator.in) {

            protected void populate(StringBuilder sb) {
                sb.append(field).append(SPACE).append(Operator.in).append(SPACE).append(LEFT_PARENTHESIS).append(query)
                        .append(RIGHT_PARENTHESIS);
            }
        };
    }
}
