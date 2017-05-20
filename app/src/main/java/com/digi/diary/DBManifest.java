package com.digi.diary;

import com.digi.diary.db.tables.NotesTable;

/**
 * Created by anupama.sinha on 25-10-2016.
 */
public interface DBManifest {
    /**
     * Common constants for database and for all tables
     */
    public final static String DB_NAME = "myDb.db";
    public final static int DB_VERSION = 1;
    public final static int DB_INVALID_ID = -1;
    //table names...
    String[] TABLE_NAMES = new String[]{NotesTable.CATEOGTY_TABLE};
    // Table create queries...
    String[] CREATE_QUERIES = new String[]{NotesTable.CREATE_CATEGORY_TABLE};

    String[] DROP_QUERIES = new String[]{NotesTable.DROP_QUERY};

}
