package sigalov.arttodevelop.weatherclient.network;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sigalov.arttodevelop.weatherclient.data.Storage;
import sigalov.arttodevelop.weatherclient.helpers.GsonHelper;
import sigalov.arttodevelop.weatherclient.models.City;
import sigalov.arttodevelop.weatherclient.models.serverResponse.ServerErrorResponse;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class SynchronizationOkHttp {
    private static final String APP_ID = "1f0f2533eafbed171c8fba2865101d39";

    private static final String BASE_URI = "http://api.openweathermap.org";
    private static final String API_WEATHER = "data/2.5/weather";
    private static final String API_CITIES = "data/2.5/find";

    private static final String UPDATE_ITEM_REQUEST_TAG = "weatherRequest";
    private static final String SUCCESS_CODE = "200";
    private static final String UNAUTHORIZED_ERROR_CODE = "401";

    private OkHttpClient client;

    private Storage storage;

    public SynchronizationOkHttp(@NonNull Storage storage) {
        this.storage = storage;
    }

    public void initSync() {
        client = generateClient();
    }

    private static OkHttpClient generateClient() {
        return new OkHttpClient.Builder().build();
    }

    private static String getWeatherApiUrl(String relativeUrl) {
        return String.format("%s/%s%s&appid=%s", BASE_URI, API_WEATHER, relativeUrl, APP_ID);
    }

    private static String getCityApiUrl(String foundCityString) {
        return String.format("%s/%s%s&appid=%s", BASE_URI, API_CITIES, foundCityString, APP_ID);
    }

    public List<City> getCityList(String foundCityString)
    {
        try {
            return new CityListRequestTask(foundCityString).execute().get();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public Weather getWeather(Integer cityId) throws Exception
    {
       return new WeatherRequestTask(cityId).execute().get();
    }

    private class WeatherRequestTask extends AsyncTask<Void, Void, Weather> {

        Integer cityId;

        public WeatherRequestTask(Integer cityId)
        {
            this.cityId = cityId;
        }

        @Override
        protected Weather doInBackground(Void... params) {
            Weather weather = null;

            try {
                weather = getWeatherRequest(cityId);
            } catch (Exception ex) {
                Log.e("CityListRequestTask", "doInBackground: ", ex);
            }

            return weather;
        }
    }

    private class CityListRequestTask extends AsyncTask<Void, Void, List<City>> {

        String foundCityString;

        public CityListRequestTask(String foundCityString)
        {
            this.foundCityString = foundCityString;
        }

        @Override
        protected List<City> doInBackground(Void... params) {
            List<City> cityList = new ArrayList<>();

            try {
                cityList = getCityListRequest(foundCityString);

            } catch (Exception ex) {
                Log.e("CityRequestTask", "doInBackground: ", ex);
            }

            return cityList;
        }
    }

    private Weather getWeatherRequest(Integer cityId) throws Exception
    {
        Request request = new Request.Builder()
                .tag(UPDATE_ITEM_REQUEST_TAG)
                .url(getWeatherApiUrl(String.format("?id=%s", cityId)))
                .build();

        Response response = client.newCall(request).execute();
        String receivedText = response.body().string();

        checkResponse(receivedText, response);

        return getWeatherByJson(receivedText);
    }

    private List<City> getCityListRequest(String foundCityString) throws Exception
    {
        Request request = new Request.Builder()
                .tag(UPDATE_ITEM_REQUEST_TAG)
                .url(getCityApiUrl(String.format("?mode=json&type=like&q=%s&cnt=10", foundCityString)))
                .build();

        Response response = client.newCall(request).execute();
        String receivedText = response.body().string();

        checkResponse(receivedText, response);

        return getCityListByStringJson(receivedText);
    }

    public List<City> getCityListByStringJson(String data) throws JSONException {

        JSONObject jObj = new JSONObject(data);

        JSONArray jArr = jObj.getJSONArray("list");

        List<City> cityList = new ArrayList<>();

        for (int i=0; i < jArr.length(); i++) {

            JSONObject obj = jArr.getJSONObject(i);

            String name = obj.getString("name");
            Integer id = obj.getInt("id");

            City c = new City(id, name);

            cityList.add(c);
        }

        removeDuplicates(cityList);

        return cityList;
    }

    public void removeDuplicates(List<City> cityList) {
        final List<String> usedNames = new ArrayList<>();

        Iterator<City> it = cityList.iterator();
        while (it.hasNext()) {
            City city = it.next();
            final String name = city.getName();

            if (usedNames.contains(name)) {
                it.remove();

            } else {
                usedNames.add(name);
            }
        }

    }

    private void checkResponse(String receivedText, Response response) throws Exception {
        if (!response.isSuccessful()) {
            ServerErrorResponse serverErrorResponse = new Gson().fromJson(receivedText, ServerErrorResponse.class);

            if (response.code() == Integer.parseInt(UNAUTHORIZED_ERROR_CODE)) {
                throw new Exception("Unauthorized Exception");
            }

            String codeResponse = serverErrorResponse.getCode();

            if(codeResponse.equals(UNAUTHORIZED_ERROR_CODE)) {
                throw new Exception(String.format("Unauthorized Exception - %s", serverErrorResponse.getMessage()));
            }

            if(!codeResponse.equals(SUCCESS_CODE))
                throw new Exception(String.format("Exception. Error code - %s", codeResponse));
        }
    }

    private Weather getWeatherByJson(String json) throws Exception
    {
        JsonElement jsonElement;
        try {
            jsonElement = new JsonParser().parse(json);
        }
        catch(Exception ex)
        {
            throw new Exception(ex.getMessage());
        }

        if(!jsonElement.isJsonObject())
            throw new Exception("String is not JsonObject");

        JsonObject jObject = jsonElement.getAsJsonObject();

        return getWeatherResponseByJson(jObject);
    }


    public Weather getWeatherResponseByJson(@NonNull JsonObject jsonObject) throws Exception
    {
        Weather weatherResponse = new Weather();

        JsonObject jObjectMain = GsonHelper.getJsonObjectOrThrow(jsonObject, "main");
        JsonObject jObjectWind = GsonHelper.getJsonObjectOrThrow(jsonObject, "wind");

        weatherResponse.setTemp(GsonHelper.getAsDoubleOrThrow(jObjectMain, "temp"));
        weatherResponse.setWindSpeed(GsonHelper.getAsDoubleOrThrow(jObjectWind, "speed"));
        weatherResponse.setWindDeg(GsonHelper.getAsDoubleOrThrow(jObjectWind, "deg"));

        return weatherResponse;
    }


}
