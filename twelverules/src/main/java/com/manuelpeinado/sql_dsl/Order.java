package com.manuelpeinado.sql_dsl;

import static com.manuelpeinado.sql_dsl.Constants.SPACE;

public class Order {
    private final Field expression;
    private final OrderType orderType;
    private final boolean collate;

    private Order(Field expression) {
        this(expression, OrderType.ASC);
    }

    private Order(Field expression, OrderType orderType) {
    	this(expression, orderType, false);
    }
    
    private Order(Field expression, OrderType orderType, boolean collate) {
        this.expression = expression;
        this.orderType = orderType;
        this.collate = collate;
    }

    public static Order asc(Field expression) {
        return new Order(expression);
    }

    public static Order asc(Field expression, boolean collate) {
        return new Order(expression, OrderType.ASC, collate);
    }

    public static Order desc(Field expression) {
        return new Order(expression, OrderType.DESC);
    }

    public static Order desc(Field expression, boolean collate) {
        return new Order(expression, OrderType.DESC, collate);
    }

    @Override
    public String toString() {
        return expression + SPACE + (collate ? "COLLATE UNICODE " : "") + orderType;
    }
}
