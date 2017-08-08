package sigalov.arttodevelop.weatherclient.data;


import android.content.Context;

import java.util.List;

import sigalov.arttodevelop.weatherclient.WeatherClientApplication;
import sigalov.arttodevelop.weatherclient.models.City;
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

    public boolean isCityExists(String cityName)
    {
        List<City> cityList = getCityList(cityName);

        boolean result = false;

        for(City currentCity : cityList) {
            if(currentCity.getName().toLowerCase().equals(cityName.toLowerCase())) {
                result = true;
                break;
            }
        }

        return result;
    }

    public List<City> getCityList(String foundCityString)
    {
        return synchronization.getCityList(foundCityString);
    }

    public void testRequest()
    {
        synchronization.testRequest();
    }
}
