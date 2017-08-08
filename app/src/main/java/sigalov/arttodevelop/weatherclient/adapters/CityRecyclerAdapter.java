package sigalov.arttodevelop.weatherclient.adapters;


import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sigalov.arttodevelop.weatherclient.R;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityViewHolder> {

    private Context context;
    private ArrayList<String> dataList;

    private int lastPosition = -1;

    public static class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public ImageView city_delete_button;

        public CityViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.city_item_name);
            city_delete_button = (ImageView) view.findViewById(R.id.city_delete_button);
            city_delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("delete","success");
                }
            });
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
