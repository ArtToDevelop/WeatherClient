package sigalov.arttodevelop.weatherclient.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Storage {

    final String TAG = "Storage";
    final String databaseName = "db";

    Context context;

    DbOpenHelper dbOpenHelper;
    SQLiteDatabase db;
    DatabaseErrorHandler errorHandler;

    public Storage(Context context) {
        this.context = context;

        errorHandler = new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase sqLiteDatabase) {
                Log.d(TAG, "onCorruption");
            }
        };
    }

    public void dbCreate() {

        if(db != null && db.isOpen())
            return;

        if(dbOpenHelper == null)
            dbOpenHelper = new DbOpenHelper(context, databaseName, null, 1, errorHandler);

        db = dbOpenHelper.getWritableDatabase();
        db.enableWriteAheadLogging();
        db.execSQL("PRAGMA foreign_keys = ON");
    }

}
