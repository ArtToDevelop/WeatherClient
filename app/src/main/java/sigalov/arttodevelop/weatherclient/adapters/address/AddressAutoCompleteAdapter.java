package sigalov.arttodevelop.weatherclient.adapters.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.adapters.AutoCompleteBaseAdapter;
import sigalov.arttodevelop.weatherclient.data.DataManager;
import sigalov.arttodevelop.weatherclient.models.City;

public class AddressAutoCompleteAdapter extends AutoCompleteBaseAdapter {

    DataManager dataManager = DataManager.getInstance();

    private List<City> hintAddressList;

    public AddressAutoCompleteAdapter(Context context) {
        super(context);

        hintAddressList = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.dropdown_address_item, parent, false);
        }

        String hintAddressString = getItem(position);
        ((TextView) convertView.findViewById(R.id.addressTextViewItem)).setText(hintAddressString);

        return convertView;
    }

    protected List<String> findItems(String itemValue) {
        List<String> result = new ArrayList<>();

        hintAddressList = dataManager.getCityList(itemValue);

        if(hintAddressList == null)
            return new ArrayList<>();

        for (City currentHintAddress : hintAddressList) {
            String hintAddressString = currentHintAddress.getName();

            if(hintAddressString.toLowerCase().contains(itemValue.toLowerCase()))
                result.add(hintAddressString);
        }

        //TODO: запрос на сервер для получения списка городов

        return result;
    }


    @Override
    public long getSelectItemIdByStringValue(String valueString) {
        City hintAddress = getHintAddressByString(valueString, true);

        if(hintAddress == null)
            return -1;

        return hintAddress.getId();
    }

    @Override
    public void setFilterItemId(Long id) {

    }

    public City getHintAddressByString(String hintAddressString)
    {
        return getHintAddressByString(hintAddressString, false);
    }

    private City getHintAddressByString(String hintAddressString, boolean isExactMatch)
    {
        City hintAddress = null;

        for(City currentHintAddress : hintAddressList) {
            if((!isExactMatch && currentHintAddress.getName().toLowerCase().contains(hintAddressString.toLowerCase()))
                    || (isExactMatch && currentHintAddress.getName().toLowerCase().equals(hintAddressString.toLowerCase()))) {
                hintAddress = currentHintAddress;
                break;
            }
        }

        return hintAddress;
    }

}
