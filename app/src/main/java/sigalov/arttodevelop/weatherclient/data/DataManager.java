package sigalov.arttodevelop.weatherclient.data;


import android.content.Context;

import java.util.List;

import sigalov.arttodevelop.weatherclient.WeatherClientApplication;
import sigalov.arttodevelop.weatherclient.models.City;
import sigalov.arttodevelop.weatherclient.models.Weather;
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

    public City getCityByString(String cityName)
    {
        List<City> cityList = getCityList(cityName);

        City foundCity = null;

        for(City currentCity : cityList) {
            if(currentCity.getName().toLowerCase().equals(cityName.toLowerCase())) {
                foundCity = currentCity;
                break;
            }
        }

        return foundCity;
    }

    public List<City> getCityList(String foundCityString)
    {
        return synchronization.getCityList(foundCityString);
    }

    public boolean addNewCity(City city) throws Exception
    {
        Weather weather = synchronization.getWeather(city.getServerId());
        if(weather == null)
            return false;

        weather.setCityId(city.getServerId());
        weather.setName(city.getName());

        //TODO: сохранение в базу
        //.....



        return true;
    }
}
