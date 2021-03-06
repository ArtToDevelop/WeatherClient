package sigalov.arttodevelop.weatherclient.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import sigalov.arttodevelop.weatherclient.models.City;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + City.TableName + " (" +
                " 'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " 'server_id' TEXT, " +
                " 'name' TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE " + Weather.TableName + " (" +
                " 'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " 'city_id' INTEGER NOT NULL, " +
                " 'date_refresh' DATETIME, " +
                " 'name' TEXT, " +
                " 'temp' REAL, " +
                " 'wind_speed' REAL, " +
                " 'wind_deg' REAL, " +
                " FOREIGN KEY(city_id) REFERENCES " + City.TableName + "(id) ON DELETE CASCADE);");

        sqLiteDatabase.execSQL("CREATE TRIGGER 'city_weather_creation_trigger' " +
                "AFTER INSERT ON " + City.TableName + " " +
                "FOR EACH ROW " +
                "BEGIN " +
                "INSERT INTO " + Weather.TableName + " (city_id, name, date_refresh) VALUES(new.id, new.name, datetime()); " +
                "END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
