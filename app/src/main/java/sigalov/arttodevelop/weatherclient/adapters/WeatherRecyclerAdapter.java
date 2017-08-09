package sigalov.arttodevelop.weatherclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.interfaces.OnCityDeleteListener;
import sigalov.arttodevelop.weatherclient.interfaces.OnWeatherDeleteListener;
import sigalov.arttodevelop.weatherclient.models.City;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.WeatherViewHolder> {

    private Context context;
    private List<Weather> dataList;

    private OnWeatherDeleteListener listener;

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView, dataTextView, temperatureTextView, windTextView, directionTextView;

        public ImageButton deleteButton;

        public WeatherViewHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.weather_item_name);
            dataTextView = (TextView) v.findViewById(R.id.weather_item_date);
            temperatureTextView = (TextView) v.findViewById(R.id.weather_item_temperature);
            windTextView = (TextView) v.findViewById(R.id.weather_item_wind);
            directionTextView = (TextView) v.findViewById(R.id.weather_item_direction);
            deleteButton = (ImageButton) v.findViewById(R.id.weather_delete_button);
        }

        public void bind(Context context, final Weather weather, final OnWeatherDeleteListener listener) {
            nameTextView.setText(weather.getName());
            dataTextView.setText(utcToLocalTime(weather.getDateRefresh()));

            temperatureTextView.setText(String.format(Locale.getDefault(),
                            context.getResources().getString(R.string.weather_item_name),
                            getCelciusByKelvinValue(weather.getTemp())));

            directionTextView.setText(String.format(
                    context.getResources().getString(R.string.weather_item_direction),
                    getStringByWindDeg(context, weather.getWindDeg())));

            windTextView.setText(String.format(Locale.getDefault(),
                    context.getResources().getString(R.string.weather_item_speed),
                    weather.getWindSpeed()));

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onWeatherDelete(weather);
                }
            });
        }

        private String utcToLocalTime(Date date)
        {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(tz);
            return sdf.format(date);
        }

        private Double getCelciusByKelvinValue(Double kelvin)
        {
            return kelvin - 273.15;
        }

        private String getStringByWindDeg(Context context, Double windDeg)
        {
            String degString = windDeg.toString();

            if(windDeg >= 0 && windDeg < 45)
                return context.getResources().getString(R.string.weather_direction_angle_0_45);

            if(windDeg >= 45 && windDeg < 90)
                return context.getResources().getString(R.string.weather_direction_angle_45_60);

            if(windDeg >= 90 && windDeg < 135)
                return context.getResources().getString(R.string.weather_direction_angle_90_135);

            if(windDeg >= 135 && windDeg < 180)
                return context.getResources().getString(R.string.weather_direction_angle_135_180);

            if(windDeg >= 180 && windDeg < 225)
                return context.getResources().getString(R.string.weather_direction_angle_180_225);

            if(windDeg >= 225 && windDeg < 270)
                return context.getResources().getString(R.string.weather_direction_angle_225_270);

            if(windDeg >= 270 && windDeg < 315)
                return context.getResources().getString(R.string.weather_direction_angle_270_315);

            if(windDeg >= 315)
                return context.getResources().getString(R.string.weather_direction_angle_315);

            return degString;
        }
    }

    public WeatherRecyclerAdapter(Context context, OnWeatherDeleteListener listener) {
        dataList = new ArrayList<>();
        this.context = context;
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
        holder.bind(context, weather, listener);
    }

    @Override
    public int getItemCount() {
        if(dataList == null)
            return 0;

        return dataList.size();
    }

}
