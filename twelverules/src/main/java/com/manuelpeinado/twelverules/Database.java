package com.manuelpeinado.twelverules;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public abstract class Database extends SQLiteOpenHelper implements IDatabase {
	private SQLiteDatabase db;
	private DatabaseTable[] tables;
	private String name;

	protected Database(Context context, String name, int version) {
		super(context, name, null, version);
		this.name = name;
	}

	protected abstract DatabaseTable[] getTables(SQLiteDatabase db);

	@Override
	public DatabaseTable[] getTables() {
		if (tables == null) {
			tables = getTables(getDatabase());
		}
		return tables;
	}

	private SQLiteDatabase getDatabase() {
		if (db == null) {
			db = getWritableDatabase();
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (DatabaseTable table : getTables(db)) {
			table.create();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (DatabaseTable table : getTables(db)) {
			table.upgrade();
		}
	}

	public void beginInsertTransaction() {
		getTables();
		db.beginTransaction();
	}

	public void endInsertTransaction(boolean success) {
		getTables();
		if (success) {
			db.setTransactionSuccessful();
		}
		db.endTransaction();
		for (DatabaseTable table : tables) {
			table.finishInsert();
		}
	}

	@Override
	public boolean copyToSdCard(Context context) { 
		return DbUtils.copyDbToSdCard(context, this);
	}

	@Override
	public String getName() {
		return name;
	}
}
