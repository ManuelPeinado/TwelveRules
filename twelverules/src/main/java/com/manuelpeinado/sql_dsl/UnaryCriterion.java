package com.manuelpeinado.sql_dsl;

import static com.manuelpeinado.sql_dsl.Constants.SPACE;

public class UnaryCriterion extends Criterion {
    protected final Field expression;
    protected final Object value;

    UnaryCriterion(Field expression, Operator operator, Object value) {
        super(operator);
        this.expression = expression;
        this.value = value;
    }

    protected void populate(StringBuilder sb) {
        beforePopulateOperator(sb);
        populateOperator(sb);
        afterPopulateOperator(sb);
    }

    public static Criterion eq(Field expression, Object value) {
        return new UnaryCriterion(expression, Operator.eq, value);
    }

    protected void beforePopulateOperator(StringBuilder sb) {
        sb.append(expression);
    }

    protected void populateOperator(StringBuilder sb) {
        sb.append(operator);
    }

    protected void afterPopulateOperator(StringBuilder sb) {
        sb.append(value == null ? "" : value);
    }

    public static Criterion neq(Field field, Object value) {
        return new UnaryCriterion(field, Operator.neq, value);
    }

    public static Criterion gt(Field field, Object value) {
        return new UnaryCriterion(field, Operator.gt, value);
    }

    public static Criterion lt(Field field, Object value) {
        return new UnaryCriterion(field, Operator.lt, value);
    }

    public static Criterion isNull(Field field) {
        return new UnaryCriterion(field, Operator.isNull, null) {
            @Override
            protected void populateOperator(StringBuilder sb) {
                sb.append(SPACE).append(operator);
            }
        };
    }

    public static Criterion isNotNull(Field field) {
        return new UnaryCriterion(field, Operator.isNotNull, null) {
            @Override
            protected void populateOperator(StringBuilder sb) {
                sb.append(SPACE).append(operator);
            }
        };
    }

    public static Criterion like(Field field, String value) {
        return new UnaryCriterion(field, Operator.like, value) {
            @Override
            protected void populateOperator(StringBuilder sb) {
                sb.append(SPACE).append(operator).append(SPACE);
            }
        };
    }
}
