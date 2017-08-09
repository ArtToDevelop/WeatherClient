package sigalov.arttodevelop.weatherclient.data;


import android.content.Context;

import java.util.Calendar;
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
        List<City> cityList = getCityListFromServer(cityName);

        City foundCity = null;

        for(City currentCity : cityList) {
            if(currentCity.getName().toLowerCase().equals(cityName.toLowerCase())) {
                foundCity = currentCity;
                break;
            }
        }

        return foundCity;
    }

    public List<City> getCityListFromServer(String foundCityString)
    {
        return synchronization.getCityList(foundCityString);
    }

    public List<City> getAllCityLocalList() {
        return storage.getAllCities();
    }

    public List<Weather> getAllWeatherLocalList() {
        return storage.getAllWeathers();
    }

    public void updateWeatherByCity(City city) throws Exception
    {
        Weather weather = synchronization.getWeather(city.getServerId());
        if(weather == null)
            return;

        weather.setCityId(city.getId());
        weather.setName(city.getName());
        weather.setDateRefresh(Calendar.getInstance().getTime());

        storage.updateWeather(weather);
    }

    public boolean addNewCity(City city) throws Exception
    {
        Weather weather = synchronization.getWeatherAsync(city.getServerId());
        if(weather == null)
            return false;

        weather.setCityId(city.getId());
        weather.setName(city.getName());

        storage.upsertCity(city);

        return true;
    }

    public void deleteCity(Integer localId)
    {
        storage.deleteCity(localId);
    }
}
