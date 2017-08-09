package sigalov.arttodevelop.weatherclient.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sigalov.arttodevelop.weatherclient.models.City;
import sigalov.arttodevelop.weatherclient.models.Weather;

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

    public synchronized int upsertCity(City city) {
        if (updateCity(city) == 0)
        {
            // Inserting
            ContentValues contentValues = cityToContentValues(city);
            city.setId((int)db.insertOrThrow(City.TableName, null, contentValues));
        }

        Log.d(TAG, "upsertCity: id=" + city.getId());
        return city.getId();
    }

    private synchronized Weather getWeather(final String cityId) {
        Cursor cursor = db.query(Weather.TableName, null, "city_id=" + cityId, null, null, null, null);
        cursor.moveToFirst();
        Weather city = getWeatherFromCursor(cursor);
        cursor.close();

        return city;
    }

    private synchronized City getCity(final String serverId) {
        Cursor cursor = db.query(City.TableName, null, "server_id=" + serverId, null, null, null, null);
        cursor.moveToFirst();
        City city = getCityFromCursor(cursor);
        cursor.close();

        return city;
    }

    public synchronized List<Weather> getAllWeathers() {
        Cursor cursor = db.query(Weather.TableName, null, null, null, null, null, "name ASC");
        return getWeatherListFromCursor(cursor);
    }

    public synchronized List<City> getAllCities() {
        Cursor cursor = db.query(City.TableName, null, null, null, null, null, "id ASC");
        return getCityListFromCursor(cursor);
    }

    public synchronized long updateWeather(Weather weather) {
        if (weather.getCityId() != null) {
            Weather currentItem = getWeather(weather.getCityId());

            if (currentItem != null)
                weather.setId(currentItem.getId());
        }

        ContentValues contentValues = weatherToContentValues(weather);

        return db.updateWithOnConflict(Weather.TableName, contentValues, "id = " + weather.getId(), null, SQLiteDatabase.CONFLICT_NONE);
    }

    private synchronized long updateCity(City item) {
        if (item.getServerId() != null) {
            City currentItem = getCity(item.getServerId());

            if (currentItem != null)
                item.setId(currentItem.getId());
        }

        ContentValues contentValues = cityToContentValues(item);

        return db.updateWithOnConflict(City.TableName, contentValues, "id = " + item.getId(), null, SQLiteDatabase.CONFLICT_NONE);
    }


    @Nullable
    private synchronized City getCityFromCursor(Cursor cursor) {
        if (cursor.isAfterLast())
            return null;

        return cursorToCity(new City(), cursor);
    }

    private synchronized City cursorToCity(@NonNull City city, @NonNull Cursor cursor) {

        city.setId(cursor.getInt(cursor.getColumnIndex("id")));
        city.setServerId(cursor.getString(cursor.getColumnIndex("server_id")));
        city.setName(cursor.getString(cursor.getColumnIndex("name")));

        return city;
    }

    private synchronized ContentValues cityToContentValues(City city) {
        ContentValues contentValues = new ContentValues();

        if(city.getId() != null && city.getId() != 0)
            contentValues.put("id", city.getId());

        contentValues.put("server_id", city.getServerId());
        contentValues.put("name", city.getName());

        return contentValues;
    }

    private synchronized ContentValues weatherToContentValues(Weather weather) {
        ContentValues contentValues = new ContentValues();

        if(weather.getId() != null && weather.getId() != 0)
            contentValues.put("id", weather.getId());

        contentValues.put("city_id", weather.getCityId());
        contentValues.put("date_refresh", weather.getDateRefresh().getTime());
        contentValues.put("name", weather.getName());
        contentValues.put("temp", weather.getTemp());
        contentValues.put("wind_speed", weather.getWindSpeed());
        contentValues.put("wind_deg", weather.getWindDeg());

        return contentValues;
    }

    private synchronized List<City> getCityListFromCursor(Cursor cursor) {
        List<City> result = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            City city = getCityFromCursor(cursor);
            result.add(city);
            cursor.moveToNext();
        }
        cursor.close();

        return result;
    }

    private synchronized List<Weather> getWeatherListFromCursor(Cursor cursor) {
        List<Weather> result = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Weather weather = getWeatherFromCursor(cursor);
            result.add(weather);
            cursor.moveToNext();
        }
        cursor.close();

        return result;
    }

    @Nullable
    private synchronized Weather getWeatherFromCursor(Cursor cursor) {
        if (cursor.isAfterLast())
            return null;

        return cursorToWeather(new Weather(), cursor);
    }

    private synchronized Weather cursorToWeather(@NonNull Weather weather, @NonNull Cursor cursor) {

        weather.setId(cursor.getInt(cursor.getColumnIndex("id")));
        weather.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
        weather.setName(cursor.getString(cursor.getColumnIndex("name")));
        weather.setTemp(cursor.getDouble(cursor.getColumnIndex("temp")));
        weather.setWindSpeed(cursor.getDouble(cursor.getColumnIndex("wind_speed")));
        weather.setWindDeg(cursor.getDouble(cursor.getColumnIndex("wind_deg")));

        int dateIndex = cursor.getColumnIndex("date_refresh");
        if (dateIndex >= 0 && !cursor.isNull(dateIndex)) {
            long dateAsTime = cursor.getLong(dateIndex);
            weather.setDateRefresh(new Date(dateAsTime));
        }

        return weather;
    }
}
