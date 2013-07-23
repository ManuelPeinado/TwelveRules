package com.manuelpeinado.sql_dsl;

import static com.manuelpeinado.sql_dsl.Constants.AND;
import static com.manuelpeinado.sql_dsl.Constants.EXISTS;
import static com.manuelpeinado.sql_dsl.Constants.LEFT_PARENTHESIS;
import static com.manuelpeinado.sql_dsl.Constants.OR;
import static com.manuelpeinado.sql_dsl.Constants.RIGHT_PARENTHESIS;
import static com.manuelpeinado.sql_dsl.Constants.SPACE;

public abstract class Criterion {
    protected final Operator operator;

    Criterion(Operator operator) {
        this.operator = operator;
    }
    
    public Criterion and(Criterion other) {
    	if (other == null) {
    		return this;
    	}
        return Criterion.and(this, other);
    }

    public static Criterion and(final Criterion criterion, final Criterion... criterions) {
        return new Criterion(Operator.and) {

            protected void populate(StringBuilder sb) {
                sb.append(criterion);
                for (Criterion criterion : criterions) {
                    sb.append(SPACE).append(AND).append(SPACE).append(criterion);
                }
            }
        };
    }

    public static Criterion or(final Criterion criterion, final Criterion... criterions) {
        return new Criterion(Operator.or) {

            protected void populate(StringBuilder sb) {
                sb.append(criterion);
                for (Criterion criterion : criterions) {
                    sb.append(SPACE).append(OR).append(SPACE).append(criterion.toString());
                }
            }
        };
    }

    public static Criterion exists(final Query query) {
        return new Criterion(Operator.exists) {

            protected void populate(StringBuilder sb) {
                sb.append(EXISTS).append(SPACE).append(LEFT_PARENTHESIS).append(query).append(RIGHT_PARENTHESIS);
            }
        };
    }

    public static Criterion not(Criterion criterion) {
        return new Criterion(null) {

            protected void populate(StringBuilder sb) {
                
            }
        };
    }

    protected abstract void populate(StringBuilder sb);

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(LEFT_PARENTHESIS);
        populate(builder);
        builder.append(RIGHT_PARENTHESIS);
        return builder.toString();
    }

}
