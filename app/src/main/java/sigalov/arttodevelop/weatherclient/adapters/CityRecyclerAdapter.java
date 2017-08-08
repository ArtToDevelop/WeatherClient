package sigalov.arttodevelop.weatherclient.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.interfaces.OnCityDeleteListener;
import sigalov.arttodevelop.weatherclient.models.City;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityViewHolder> {

    private Context context;
    private ArrayList<City> cityList;

    private int lastPosition = -1;

    private OnCityDeleteListener listener;

    public static class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public ImageView city_delete_button;

        public CityViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.city_item_name);
            city_delete_button = (ImageView) view.findViewById(R.id.city_delete_button);
        }

        public void bind(final City city, final OnCityDeleteListener listener) {
            nameTextView.setText(city.getName());
            city_delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteCity(city);
                }
            });
        }

        public void clearAnimation() {
            nameTextView.getRootView().clearAnimation();
        }
    }

    public CityRecyclerAdapter(Context context, OnCityDeleteListener listener) {
        cityList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
    }

    public void setData(ArrayList<City> cityList) {
        this.cityList = cityList;
    }

    @Override
    public CityRecyclerAdapter.CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_city_item, parent, false);

        return new CityRecyclerAdapter.CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CityRecyclerAdapter.CityViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.bind(city, listener);

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    @Override
    public void onViewDetachedFromWindow(final CityRecyclerAdapter.CityViewHolder holder) {
        holder.clearAnimation();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
