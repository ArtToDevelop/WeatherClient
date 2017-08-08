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

public class AddressAutoCompleteAdapter extends AutoCompleteBaseAdapter {
    private List<HintAddress> hintAddressList;

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

        for (HintAddress currentHintAddress : hintAddressList) {
            String hintAddressString = currentHintAddress.getName();

            if(hintAddressString.toLowerCase().contains(itemValue.toLowerCase()))
                result.add(hintAddressString);
        }

        return result;
    }

    public void setData(List<HintAddress> hintAddressList)
    {
        this.hintAddressList = hintAddressList;
    }

    @Override
    public long getSelectItemIdByStringValue(String valueString) {
        HintAddress hintAddress = getHintAddressByString(valueString, true);

        if(hintAddress == null)
            return -1;

        return hintAddress.getId();
    }

    @Override
    public void setFilterItemId(Long id) {

    }

    public HintAddress getHintAddressByString(String hintAddressString)
    {
        return getHintAddressByString(hintAddressString, false);
    }

    private HintAddress getHintAddressByString(String hintAddressString, boolean isExactMatch)
    {
        HintAddress hintAddress = null;

        for(HintAddress currentHintAddress : hintAddressList) {
            if((!isExactMatch && currentHintAddress.getName().toLowerCase().contains(hintAddressString.toLowerCase()))
                    || (isExactMatch && currentHintAddress.getName().toLowerCase().equals(hintAddressString.toLowerCase()))) {
                hintAddress = currentHintAddress;
                break;
            }
        }

        return hintAddress;
    }

}
