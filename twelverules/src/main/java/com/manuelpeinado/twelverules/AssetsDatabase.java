package com.manuelpeinado.twelverules;


import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public abstract class AssetsDatabase extends SQLiteAssetHelper implements IDatabase {
	private SQLiteDatabase db;
	private DatabaseTable[] tables;
	private String name;

	protected AssetsDatabase(Context context, String name, int version) {
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

	public void beginInsertTransaction() {
		db.beginTransaction();
	}

	public void endInsertTransaction(boolean success) {
		if (success) {
			db.setTransactionSuccessful();
		}
		db.endTransaction();
		for (DatabaseTable table : tables) {
			table.finishInsert();
		}
	}
	
	/**
	 * Provoca que se sobreescriba la base de datos existente si su versión es inferior a la proporcionada
	 * Útil cuando no necesitamos hacer una migración mediante script, sino que queremos reemplazar todos 
	 * los datos anteriores con datos nuevos
	 */
	public void forceUpgrade(int version) {
		setForcedUpgradeVersion(version);
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

