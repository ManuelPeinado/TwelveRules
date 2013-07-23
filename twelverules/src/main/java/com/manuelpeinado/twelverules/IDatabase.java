package com.manuelpeinado.twelverules;

import android.content.Context;

public interface IDatabase {
	String getName();
	boolean copyToSdCard(Context context);
	void beginInsertTransaction();
	void endInsertTransaction(boolean success);
	DatabaseTable[] getTables();
}
