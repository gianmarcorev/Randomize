package com.projectzulu.randomize.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by gianmarco on 26/01/16.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "randomize";

    // We have a first table storing all the lists
    // and a second one storing all the elements, each one bound to a list by a list id

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ListsTable.TABLE_CREATE);
        db.execSQL(ElementsTable.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing for now
    }

    public class ListsTable implements BaseColumns {
        public static final String TABLE_NAME = "lists";

        public static final String COLUMN_NAME = "name";

        public static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL);";
    }

    public class ElementsTable implements BaseColumns {
        public static final String TABLE_NAME = "elements";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LIST_ID = "list_id";
        public static final String COLUMN_ENABLED = "enabled";

        public static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_LIST_ID + " INTEGER NOT NULL, " +
                        COLUMN_ENABLED + " INTEGER NOT NULL);";
    }
}
