package sigalov.arttodevelop.weatherclient.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import sigalov.arttodevelop.weatherclient.R;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityViewHolder> {

    private Context context;
    private ArrayList<String> dataList;

    private int lastPosition = -1;

    public static class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public CityViewHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.city_item_name);
        }

        public void clearAnimation()
        {
            nameTextView.getRootView().clearAnimation();
        }
    }

    public CityRecyclerAdapter(Context context) {
        dataList = new ArrayList<>();
        this.context = context;
    }

    public void setData(ArrayList<String> dataList)
    {
        this.dataList = dataList;
    }

    @Override
    public CityRecyclerAdapter.CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_city_item, parent, false);

        return new CityRecyclerAdapter.CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CityRecyclerAdapter.CityViewHolder holder, int position) {
        String item = dataList.get(position);

        holder.nameTextView.setText(item);

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onViewDetachedFromWindow(final CityRecyclerAdapter.CityViewHolder holder)
    {
        ((CityRecyclerAdapter.CityViewHolder)holder).clearAnimation();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
