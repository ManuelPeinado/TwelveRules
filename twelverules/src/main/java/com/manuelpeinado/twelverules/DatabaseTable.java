package com.manuelpeinado.twelverules;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import com.manuelpeinado.sql_dsl.Field;
import com.manuelpeinado.sql_dsl.Query;
import com.manuelpeinado.sql_dsl.Table;


public class DatabaseTable {
	private InsertHelper insertHelper;
	private HashMap<String, Integer> insertHelperColumnIndices;
	
	private SQLiteDatabase db;
	private DatabaseColumn[] columns;
	private String name;

	protected DatabaseTable(String name, SQLiteDatabase db, DatabaseColumn[] columns) {
		this.name = name;
		this.db = db;
		this.columns = columns;
	} 

	public void create() {
		String statement = DbUtils.prepareCreateStatement(name, columns);
		db.execSQL(statement);
	}

	public void upgrade() {
		DbUtils.dropTable(db, name);
		create();
	}

	public Cursor queryAll() {
		return db.query(name, DatabaseColumn.asSelection(columns), null, null, null, null, null);
	}

	public Cursor queryFilteredByTextColumn(ColumnSet columns, DatabaseColumn filteredColumn, String filterText) {
		String where = String.format("%s LIKE '%s%%'", filteredColumn.getName(), filterText);
		return db.query(name, columns.get(), where, null, null, null, null);
	}
	
	public Cursor rawQuery(String sql) {
		return db.rawQuery(sql, null);
	}

	public Cursor query(Query query) {
		return rawQuery(query.toString());
	}

	public Cursor queryAll(DatabaseColumn column) {
		return queryAll(new ColumnSet(column));
	}

	public Cursor queryAll(ColumnSet columns) {
		return db.query(name, columns.get(), null, null, null, null, null);
	}

	public Cursor queryAllWithIdAlias(DatabaseColumn idAlias) {
		String[] selection = DatabaseColumn.asSelection(columns);
		selection = DatabaseColumn.addIdAliasToSelection(selection, idAlias);
		return db.query(name, selection, null, null, null, null, null);
	}
	
	public long insert(ContentValues values) {
		return db.insert(name, null, values);
	}

	public long insertWithHelper(ContentValues values) {
		if (insertHelper == null) {
			insertHelper = new InsertHelper(db, name);
			if (insertHelperColumnIndices == null) {
				insertHelperColumnIndices = new HashMap<String, Integer>();
			}
			insertHelperColumnIndices.clear();
			for (Entry<String, Object> keyValue : values.valueSet()) {
				String key = keyValue.getKey();
				insertHelperColumnIndices.put(key, insertHelper.getColumnIndex(key)); 
			}
		}
		insertHelper.prepareForInsert();
		for (Entry<String, Integer> keyValue : insertHelperColumnIndices.entrySet()) {
			String key = keyValue.getKey();
			int index = insertHelperColumnIndices.get(key);
			insertHelper.bind(index, values.getAsString(key));
		}
		return insertHelper.execute();
	}
	
	public void finishInsert() {
		if (insertHelper != null) {
			insertHelper.close();
			insertHelper = null;
		}
	}

	public boolean exists() {
		Cursor rs = null; 
		try{ 
			rs = db.rawQuery("SELECT * FROM " + name + " WHERE 1=0", null ); 
			return true; 
		}catch(Exception ex){ 
			return false; 
		}finally{ 
			DbUtils.close(rs); 
		} 	
	}
	
	public long countRows() {
		return DatabaseUtils.queryNumEntries(db, name);
	}
	
	public String getName() {
		return name;
	}
	
	public Query select() {
		return select(columns);
	}

	public Query select(DatabaseColumn ... columns) {
		Field[] fs = new Field[columns.length];
		for (int i = 0; i < columns.length; i++) {
			fs[i] = Field.field(columns[i].getName());
		}
		return Query.select(fs).from(Table.table(name));
	}

	public Cursor queryById(long id) {
		return query(select(columns).whereId(id));
	}
	
	public void clear() {
		DbUtils.clearTable(db, this);
	}
}
