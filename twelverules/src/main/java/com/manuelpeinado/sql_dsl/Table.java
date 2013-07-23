package com.manuelpeinado.sql_dsl;

public class Table extends DBObject<Table> {

    protected Table(String expression) {
        super(expression);
    }

    public static Table table(String table) {
        return new Table(table);
    }

    public Field field(String fieldName) {
        if (hasAlias()) {
            return Field.field(alias + "." + fieldName);
        }
        return Field.field(expression+"."+fieldName);
    }
    
    public Query select(Field ... fields) {
    	return Query.select(fields).from(this);
    }
}
