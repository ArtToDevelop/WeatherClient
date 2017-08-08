package sigalov.arttodevelop.weatherclient.adapters;


import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public abstract class AutoCompleteBaseAdapter extends BaseAdapter implements Filterable {

    protected final Context mContext;
    private List<String> itemList;

    private final List<OnNewFilterItems> onNewFilterItemsListener = new ArrayList<>();

    public AutoCompleteBaseAdapter(Context context) {
        mContext = context;
        itemList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return itemList != null ? itemList.size() : 0;
    }

    @Override
    public String getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract long getSelectItemIdByStringValue(String valueString);

    public abstract void setFilterItemId(Long id);

    protected abstract List<String> findItems(String itemValue);

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<String> items = findItems(constraint.toString());

                    filterResults.values = items;
                    filterResults.count = items.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {

                    ArrayList values = (ArrayList)results.values;

                    itemList = new ArrayList<>(values.size());
                    for (Object object : values) {
                        itemList.add(object != null ? object.toString() : null);
                    }

                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    protected void performFiltering()
    {
        for (OnNewFilterItems hl : onNewFilterItemsListener)
            hl.onNewFilterItems(this);
    }

    public void setOnNewFilterItemsListener(OnNewFilterItems toAdd) {
        onNewFilterItemsListener.add(toAdd);
    }

    public void removeOnNewFilterItemsListener(OnNewFilterItems toRemove) {
        onNewFilterItemsListener.remove(toRemove);
    }

    public interface OnNewFilterItems {
        void onNewFilterItems(AutoCompleteBaseAdapter adapter);
    }
}
