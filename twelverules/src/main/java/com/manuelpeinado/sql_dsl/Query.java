package com.manuelpeinado.sql_dsl;

import static com.manuelpeinado.sql_dsl.Constants.ALL;
import static com.manuelpeinado.sql_dsl.Constants.COMMA;
import static com.manuelpeinado.sql_dsl.Constants.FROM;
import static com.manuelpeinado.sql_dsl.Constants.GROUP_BY;
import static com.manuelpeinado.sql_dsl.Constants.LEFT_PARENTHESIS;
import static com.manuelpeinado.sql_dsl.Constants.ORDER_BY;
import static com.manuelpeinado.sql_dsl.Constants.RIGHT_PARENTHESIS;
import static com.manuelpeinado.sql_dsl.Constants.SELECT;
import static com.manuelpeinado.sql_dsl.Constants.SPACE;
import static com.manuelpeinado.sql_dsl.Constants.WHERE;
import static com.manuelpeinado.sql_dsl.Table.table;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.manuelpeinado.twelverules.DatabaseColumn;

public class Query {

    private Table table;
    private List<Criterion> criterions = new ArrayList<Criterion>();
    private List<Field> fields = new ArrayList<Field>();
    private List<Join> joins = new ArrayList<Join>();
    private List<Field> groupBies = new ArrayList<Field>();
    private List<Order> orders = new ArrayList<Order>();
    private List<Criterion> havings = new ArrayList<Criterion>();

    private Query(Field... fields) {
        this.fields.addAll(asList(fields));
    }

    public static Query select(Field... fields) {
        return new Query(fields);
    }

    public Query from(Table table) {
        this.table = table;
        return this;
    }

    public Query join(Join... join) {
        joins.addAll(asList(join));
        return this;
    }

    public Query where(Criterion criterion) {
        criterions.add(criterion);
        return this;
    }

    public Query whereEq(DatabaseColumn col, Object value) {
        criterions.add(UnaryCriterion.eq(Field.field(col.getName()), value));
        return this;
    }

    public Query whereNotEq(DatabaseColumn col, Object value) {
        criterions.add(UnaryCriterion.neq(Field.field(col.getName()), value));
        return this;
    }

    public Query whereStartsWith(DatabaseColumn column, String text) {
        return whereStartsWith(Field.field(column.getName()), text);
    }
    
    public Query whereStartsWith(Field field, String text) {
    	text = escapeSpecialChars(text);
        criterions.add(UnaryCriterion.like(field, String.format("'%s%%'", text)));
        return this;
    }

	private static String escapeSpecialChars(String text) {
		text = text.replace("'", "''");
		return text;
	}

    public Query whereContains(DatabaseColumn column, String text) {
        return whereContains(Field.field(column.getName()), text);
    }

    public Query whereContains(Field field, String text) {
    	text = escapeSpecialChars(text);
        criterions.add(createContainsCriterion(field, text));
        return this;
    }
    
	public static Criterion createContainsCriterion(DatabaseColumn column, String text) {
		return createContainsCriterion(Field.field(column.getName()), text);
	}

	public static Criterion createContainsCriterion(Field field, String text) {
		text = escapeSpecialChars(text);
		String query = String.format("'%%%s%%'", text);
		return UnaryCriterion.like(field, query);
	}

    public Query groupBy(Field... groupBy) {
        groupBies.addAll(asList(groupBy));
        return this;
    }

    public Query orderBy(Order... order) {
        orders.addAll(asList(order));
        return this;
    }
    
    public Query ascending(Field field) {
        return ascending(field, false);
    }
    
    public Query ascending(DatabaseColumn column) {
    	return ascending(column, false);
    }
    
    public Query ascending(Field field, boolean collate) {
        return orderBy(Order.asc(field, collate));
    }
    
    public Query ascending(DatabaseColumn column, boolean collate) {
        return orderBy(Order.asc(Field.field(column.getName()), collate));
    }

    public Query appendSelectFields(Field... fields) {
        this.fields.addAll(asList(fields));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && this.toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder();
        visitSelectClause(sql);
        visitFromClause(sql);
        visitJoinClause(sql);
        visitWhereClause(sql);
        visitGroupByClause(sql);
        visitOrderByClause(sql);
        return sql.toString();
    }

    private void visitOrderByClause(StringBuilder sql) {
        if (orders.isEmpty()) {
            return;
        }
        sql.append(ORDER_BY);
        for (Order order : orders) {
            sql.append(SPACE).append(order).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    private void visitGroupByClause(StringBuilder sql) {
        if (groupBies.isEmpty()) {
            return;
        }
        sql.append(GROUP_BY);
        for (Field groupBy : groupBies) {
            sql.append(SPACE).append(groupBy).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
        if (havings.isEmpty()) {
            return;
        }
        sql.append("HAVING");
        for (Criterion havingCriterion : havings) {
            sql.append(SPACE).append(havingCriterion).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    private void visitWhereClause(StringBuilder sql) {
        if (criterions.isEmpty()) {
            return;
        }
        sql.append(WHERE);
        for (Criterion criterion : criterions) {
            sql.append(SPACE).append(criterion).append(SPACE);
        }
    }

    private void visitJoinClause(StringBuilder sql) {
        for (Join join : joins) {
            sql.append(join).append(SPACE);
        }
    }

    private void visitFromClause(StringBuilder sql) {
        if (table == null) {
            return;
        }
        sql.append(FROM).append(SPACE).append(table).append(SPACE);
    }

    private void visitSelectClause(StringBuilder sql) {
        sql.append(SELECT).append(SPACE);
        if (fields.isEmpty()) {
            sql.append(ALL).append(SPACE);
            return;
        }
        for (Field field : fields) {
            sql.append(field).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    public Table as(String alias) {
        return table(LEFT_PARENTHESIS + this.toString() + RIGHT_PARENTHESIS).as(alias);
    }

    public Query having(Criterion criterion) {
        this.havings.add(criterion);
        return this;
    }
    
	public Query whereId(long id) {
		return where(UnaryCriterion.eq(Field.field("_id"), id));
	}

}
