package com.manuelpeinado.twelverules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import com.manuelpeinado.sql_dsl.Criterion;
import com.manuelpeinado.sql_dsl.Field;
import com.manuelpeinado.sql_dsl.Query;
import com.manuelpeinado.sql_dsl.UnaryCriterion;

public class DatabaseColumn {
	private final static int TYPE_FIRST = 0;
	public final static int TYPE_TEXT = 0;
	public final static int TYPE_INTEGER = 1;
	public final static int TYPE_LONG = 2;
	public final static int TYPE_REAL = 3;
	public final static int TYPE_LAST = 3;
	
	public final static int FLAG_PRIMARY_KEY = 0x1;
	public final static int FLAG_NOT_NULL = 0x2;

	public final static DatabaseColumn ID = new DatabaseColumn(BaseColumns._ID, TYPE_INTEGER, FLAG_NOT_NULL | FLAG_PRIMARY_KEY);

	private String name;
	private int type;
//	private boolean primaryKey;
//	private boolean notNull;

        public static boolean DEBUG = DbUtils.DEBUG;
	
	public DatabaseColumn(String name, int type) {
		this(name, type, 0);
	}
	
	public DatabaseColumn(String name, int type, int flags) {
		if (DEBUG)
			DbUtils.myAssert(type >= TYPE_FIRST && type <= TYPE_LAST);
		this.name = name;
		this.type = type;
		
//		this.primaryKey = (flags & FLAG_PRIMARY_KEY) == FLAG_PRIMARY_KEY;
//		this.notNull = (flags & FLAG_NOT_NULL) == FLAG_NOT_NULL;
	}
	
	public String getTypeString() {
		switch (type) {
		case TYPE_TEXT:
			return "TEXT";
		case TYPE_INTEGER:
			return "INTEGER";
		case TYPE_LONG:
			return "LONG";
		case TYPE_REAL:
			return "REAL";
		default:
			return null;
		}
	}

	public String getName() {
		return name;
	}

	public boolean isId() {
		return name.equals(ID.name);
	}
	
	public static String[] asSelection(DatabaseColumn ... columns) {
		String[] result = new String[columns.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = columns[i].getName();
		}
		return result;
	}
	
	public static String[] addIdAliasToSelection(String[] selection, DatabaseColumn idAlias) {
		List<String> list = new ArrayList<String>(Arrays.asList(selection));
		list.add(String.format("%s AS %s", idAlias.getName(), BaseColumns._ID));
		return list.toArray(selection); 
	}
	
	public void put(ContentValues dest, int value) {
		if (DEBUG) DbUtils.myAssert(type == TYPE_INTEGER);
		dest.put(name, value);
	}
	
	public void put(ContentValues dest, long value) {
		if (DEBUG) DbUtils.myAssert(type == TYPE_LONG);
		dest.put(name, value);
	}

	public void put(ContentValues dest, double value) {
		if (DEBUG) DbUtils.myAssert(type == TYPE_REAL);
		dest.put(name, value);
	}
	
	public void put(ContentValues dest, String value) {
		if (type == TYPE_INTEGER) {
			put(dest, Integer.parseInt(value));
			return;
		}
		if (DEBUG) DbUtils.myAssert(type == TYPE_TEXT);
		dest.put(name, value);
	}
	
	public int extractIntFrom(ContentValues values) {
		if (DEBUG) DbUtils.myAssert(type == TYPE_INTEGER);
		return values.getAsInteger(name);
	}

	public int extractIntFrom(Cursor cursor) {
		if (DEBUG) DbUtils.myAssert(type == TYPE_INTEGER);
		int index = cursor.getColumnIndex(name);
		return cursor.getInt(index);
	}
	
	public String extractStringFrom(Cursor cursor) {
		if (DEBUG) DbUtils.myAssert(type == TYPE_TEXT);
		int index = cursor.getColumnIndex(name);
		return cursor.getString(index);
	}
	
	public String extractStringFrom(ContentValues values) {
		if (DEBUG) DbUtils.myAssert(type == TYPE_TEXT);
		return values.getAsString(name);
	}	
	
	public boolean isPresentIn(Cursor cursor) {
		return cursor.getColumnIndex(name) != -1;
	}

	public int getType() {
		return type;
	}
	
	public Criterion contains(String text) {
		return Query.createContainsCriterion(this, text);
	}
	
	public Criterion eq(Object value) {
		return UnaryCriterion.eq(Field.field(name), value);
	}

	public Criterion notEq(Object value) {
		return UnaryCriterion.neq(Field.field(name), value);
	}
	
	public ColumnSet and(DatabaseColumn col) {
		return new ColumnSet(this).and(col);
	}
	
	public Cursor newMockCursor(String ... values) {
		MatrixCursor result = new MatrixCursor(new String[] { DatabaseColumn.ID.getName(), name });
		for (int i = 0; i < values.length; i++) {
			result.addRow(new Object[] { i + 1, values[i] });
		}
		return result;
	}

}
