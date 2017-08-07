package sigalov.arttodevelop.weatherclient.network;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sigalov.arttodevelop.weatherclient.data.Storage;
import sigalov.arttodevelop.weatherclient.helpers.GsonHelper;
import sigalov.arttodevelop.weatherclient.models.Response.ServerErrorResponse;
import sigalov.arttodevelop.weatherclient.models.Response.WeatherResponse;

public class SynchronizationOkHttp {
    private static final String APP_ID = "1f0f2533eafbed171c8fba2865101d39";

    private static final String BASE_URI = "http://api.openweathermap.org";
    private static final String API_PREFIX = "data/2.5/weather";

    private static final String UPDATE_ITEM_REQUEST_TAG = "weatherRequest";
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

    private static String getFullApiUrl(String relativeUrl) {
        return String.format("%s/%s%s&appid=%s", BASE_URI, API_PREFIX, relativeUrl, APP_ID);
    }

    public void testRequest()
    {
        new TestRequestTask().execute();
    }

    private class TestRequestTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                request();
            } catch (Exception ex) {
                Log.e("SyncError", "doInBackground: ", ex);
            }

            return null;
        }
    }

    private void request() throws Exception
    {
        Request request = new Request.Builder()
                .tag(UPDATE_ITEM_REQUEST_TAG)
                .url(getFullApiUrl(String.format("?q=%s", "London")))
                .build();

        Response response = client.newCall(request).execute();
        String receivedText = response.body().string();

        checkResponse(receivedText, response);

        parseWeatherJson(receivedText);
    }

    private void checkResponse(String receivedText, Response response) throws Exception {
        if (!response.isSuccessful()) {
            ServerErrorResponse serverErrorResponse = new Gson().fromJson(receivedText, ServerErrorResponse.class);

            if (response.code() == Integer.parseInt(UNAUTHORIZED_ERROR_CODE)) {
                throw new Exception("Unauthorized Exception");
            }

            if(serverErrorResponse.getCode().equals(UNAUTHORIZED_ERROR_CODE)) {
                throw new Exception(String.format("Unauthorized Exception - %s", serverErrorResponse.getMessage()));
            }

        }
    }

    private void parseWeatherJson(String json) throws Exception
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
        WeatherResponse weatherResponse = getWeatherResponseByJson(jObject);


        Log.i("parseWeatherJson", "success");
    }

    public WeatherResponse getWeatherResponseByJson(@NonNull JsonObject jsonObject) throws Exception
    {
        WeatherResponse weatherResponse = new WeatherResponse();

        JsonObject jObjectMain = GsonHelper.getJsonObjectOrThrow(jsonObject, "main");
        JsonObject jObjectWind = GsonHelper.getJsonObjectOrThrow(jsonObject, "wind");

        weatherResponse.setTemp(GsonHelper.getAsDoubleOrThrow(jObjectMain, "temp"));
        weatherResponse.setWindSpeed(GsonHelper.getAsDoubleOrThrow(jObjectWind, "speed"));
        weatherResponse.setWindDeg(GsonHelper.getAsDoubleOrThrow(jObjectWind, "deg"));

        return weatherResponse;
    }


}
