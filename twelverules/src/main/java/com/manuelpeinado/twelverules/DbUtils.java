package com.manuelpeinado.twelverules;

import java.util.Arrays;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.content.Context;


public class DbUtils {
	public static boolean DEBUG = true;

	public static void myAssert(boolean condition) {
		if (!condition) {
			throw new RuntimeException("Failed assertion");
		}
	}

	public static <T> String join(Iterable<T> items, String separator) {
		StringBuilder builder = new StringBuilder();
		for (T item : items) {
			builder.append(item);
			builder.append(separator);
		}
		int length = builder.length();
		if (length > 0) {
			builder.replace(length - separator.length(), length, "");
		}
		return builder.toString();
	}

	public static boolean copyFile(String inFile, String outFile) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(inFile);
			out = new FileOutputStream(outFile);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			closeInputStream(in);
			closeOutputStream(out);
		}
	}

	public static void closeInputStream(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeOutputStream(OutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static void close(Cursor c) {
		if (c != null) {
			c.close();
		}
	}

	public static int getColumnAsInt(Cursor c, String columnName) {
		int index = c.getColumnIndex(columnName);
		return c.getInt(index);
	}

	public static long getColumnAsLong(Cursor c, String columnName) {
		int index = c.getColumnIndex(columnName);
		return c.getLong(index);
	}

	public static String getColumnAsString(Cursor c, String columnName) {
		int index = c.getColumnIndex(columnName);
		return c.getString(index);
	}

	public static boolean deleteEntryById(SQLiteDatabase db, String tableName, long entry) {
		return db.delete(tableName, "_id = ?", new String[] { Long.toString(entry) }) == 1;
	}

	public static void dropTable(SQLiteDatabase db, String tableName) {
		db.execSQL("DROP TABLE IF EXISTS " + tableName);
	}

	public static String prepareCreateStatement(String tableName, DatabaseColumn[] columns) {
		String result = String.format("CREATE TABLE %s (%%s)", tableName);
		String[] args = new String[columns.length];
		for (int i = 0; i < columns.length; ++i) {
			DatabaseColumn column = columns[i];
			if (column.isId()) {
				args[i] = "_id INTEGER PRIMARY KEY AUTOINCREMENT";
				continue;
			}
			String columnName = column.getName();
			String columnText = column.getTypeString();
			args[i] = String.format("%s %s", columnName, columnText);
		}
		return String.format(result, join(Arrays.asList(args), ","));
	}

	public static boolean copyDbToSdCard(Context context, IDatabase db) {
		try {
			String sdDir = Environment.getExternalStorageDirectory().getPath();
			String pkgName = context.getPackageName();
			String currentDBPath = String.format("/data/data/%s/databases/%s", pkgName, db.getName());
			String backupDBPath = String.format("%s/%s.sqlite", sdDir, db.getName());
			return copyFile(currentDBPath, backupDBPath);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void clearTable(SQLiteDatabase db, DatabaseTable table) {
		db.execSQL(String.format("delete from %s", table.getName()));
		if (DEBUG)
			myAssert(table.countRows() == 0);
	}
}
