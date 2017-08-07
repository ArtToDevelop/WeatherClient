package sigalov.arttodevelop.weatherclient.data;


import android.content.Context;
import android.util.Log;

import sigalov.arttodevelop.weatherclient.WeatherClientApplication;
import sigalov.arttodevelop.weatherclient.network.SynchronizationOkHttp;

public class DataManager {

    private static DataManager instance;

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private Context applicationContext;

    private SynchronizationOkHttp synchronization;
    private Storage storage;

    private DataManager() {

        applicationContext = WeatherClientApplication.getContext();

        storage = new Storage(applicationContext);
        storage.dbCreate();

        synchronization = new SynchronizationOkHttp(storage);
        synchronization.initSync();
    }

    public void testRequest()
    {
        synchronization.testRequest();
    }
}
