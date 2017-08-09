package sigalov.arttodevelop.weatherclient.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.interfaces.OnCityDeleteListener;
import sigalov.arttodevelop.weatherclient.interfaces.OnWeatherDeleteListener;
import sigalov.arttodevelop.weatherclient.models.City;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.WeatherViewHolder> {

    private List<Weather> dataList;

    private OnWeatherDeleteListener listener;

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView, temperatureTextView, windTextView, directionTextView;

        public ImageButton deleteButton;

        public WeatherViewHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.weather_item_name);
            temperatureTextView = (TextView) v.findViewById(R.id.weather_item_temperature);
            windTextView = (TextView) v.findViewById(R.id.weather_item_wind);
            directionTextView = (TextView) v.findViewById(R.id.weather_item_direction);
            deleteButton = (ImageButton) v.findViewById(R.id.weather_delete_button);
        }

        public void bind(final Weather weather, final OnWeatherDeleteListener listener) {
            nameTextView.setText(weather.getName());
            temperatureTextView.setText(String.format(Locale.getDefault(), "Температура - %.0f °C", getCelciusByKelvinValue(weather.getTemp())));
            directionTextView.setText(String.format("Направление ветра - %s", getStringByWindDeg(weather.getWindDeg())));
            windTextView.setText(String.format(Locale.getDefault(), "Скорость ветра - %.0f м/с", weather.getWindSpeed()));
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onWeatherDelete(weather);
                }
            });
        }

        private Double getCelciusByKelvinValue(Double kelvin)
        {
            return kelvin - 273.15;
        }

        private String getStringByWindDeg(Double windDeg)
        {
            String degString = windDeg.toString();

            if(windDeg >= 0 && windDeg < 45)
                return "северный";

            if(windDeg >= 45 && windDeg < 90)
                return "северо-восточный";

            if(windDeg >= 90 && windDeg < 135)
                return "восточный";

            if(windDeg >= 135 && windDeg < 180)
                return "юго-восточный";

            if(windDeg >= 180 && windDeg < 225)
                return "южный";

            if(windDeg >= 225 && windDeg < 270)
                return "юго-западный";

            if(windDeg >= 270 && windDeg < 315)
                return "западный";

            if(windDeg >= 315)
                return "северо-западный";

            return degString;
        }
    }

    public WeatherRecyclerAdapter(OnWeatherDeleteListener listener) {
        dataList = new ArrayList<>();
        this.listener = listener;
    }

    public void setData(List<Weather> dataList)
    {
        this.dataList = dataList;
    }

    @Override
    public WeatherRecyclerAdapter.WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_weather_item, parent, false);

        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherRecyclerAdapter.WeatherViewHolder holder, int position) {
        Weather weather = dataList.get(position);
        holder.bind(weather, listener);
    }

    @Override
    public int getItemCount() {
        if(dataList == null)
            return 0;

        return dataList.size();
    }

}
