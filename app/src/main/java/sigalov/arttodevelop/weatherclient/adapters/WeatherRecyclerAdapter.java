package sigalov.arttodevelop.weatherclient.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.WeatherViewHolder> {

    private List<Weather> dataList;

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView, temperatureTextView, windTextView, directionTextView;

        public WeatherViewHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.weather_item_name);
            temperatureTextView = (TextView) v.findViewById(R.id.weather_item_temperature);
            windTextView = (TextView) v.findViewById(R.id.weather_item_wind);
            directionTextView = (TextView) v.findViewById(R.id.weather_item_direction);
        }
    }

    public WeatherRecyclerAdapter() {
        dataList = new ArrayList<>();
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
        Weather item = dataList.get(position);

        holder.nameTextView.setText(item.getName());
        holder.temperatureTextView.setText(String.format(Locale.getDefault(), "Температура - %.0f °C", getCelciusByKelvinValue(item.getTemp())));
        holder.directionTextView.setText(String.format("Направление ветра - %s", getStringByWindDeg(item.getWindDeg())));
        holder.windTextView.setText(String.format(Locale.getDefault(), "Скорость ветра - %.0f м/с", item.getWindSpeed()));
    }

    @Override
    public int getItemCount() {
        if(dataList == null)
            return 0;

        return dataList.size();
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
