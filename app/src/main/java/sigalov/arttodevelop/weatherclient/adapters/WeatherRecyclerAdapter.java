package sigalov.arttodevelop.weatherclient.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.WeatherViewHolder> {

    private ArrayList<Weather> dataList;

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

    public void setData(ArrayList<Weather> dataList)
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
        holder.temperatureTextView.setText(String.format("Температура - %s", item.getTemp()));
        holder.directionTextView.setText(String.format("Направление ветра - %s", item.getWindDeg()));
        holder.windTextView.setText(String.format("Скорость ветра - %s", item.getWindSpeed()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
