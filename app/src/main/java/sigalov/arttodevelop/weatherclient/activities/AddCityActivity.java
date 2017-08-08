package sigalov.arttodevelop.weatherclient.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.adapters.CityRecyclerAdapter;
import sigalov.arttodevelop.weatherclient.adapters.WeatherRecyclerAdapter;
import sigalov.arttodevelop.weatherclient.adapters.address.AddressAutoCompleteAdapter;
import sigalov.arttodevelop.weatherclient.adapters.address.AddressAutoCompleteTextView;
import sigalov.arttodevelop.weatherclient.data.DataManager;
import sigalov.arttodevelop.weatherclient.helpers.AlertDialogHelper;
import sigalov.arttodevelop.weatherclient.interfaces.OnCityDeleteListener;
import sigalov.arttodevelop.weatherclient.models.City;

public class AddCityActivity extends AppCompatActivity {

    private DataManager dataManager = DataManager.getInstance();

    private ProgressBar progressBar;
    private Button addButton, completeButton;

    private RecyclerView recyclerView;
    private CityRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private AddressAutoCompleteAdapter addressAutoCompleteAdapter;
    private AddressAutoCompleteTextView addressTextView;

    private InputMethodManager inputMethodManager;

    private ArrayList<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addressTextView = (AddressAutoCompleteTextView)findViewById(R.id.add_address_text_view);
        addressTextView.setThreshold(1);
        addressTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboardByView(view);
                }
            }
        });

        addressAutoCompleteAdapter = new AddressAutoCompleteAdapter(AddCityActivity.this);
        addressTextView.setAdapter(addressAutoCompleteAdapter);
        addressTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String diseaseString = (String) adapterView.getItemAtPosition(position);
                addressTextView.setText(diseaseString);
                addressTextView.setSelection(diseaseString.length());
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.add_progress_bar);
        addressTextView.setLoadingIndicator(progressBar);
        addressTextView.setAutocompleteDelay(100);

        addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCity();
            }
        });

        completeButton = (Button) findViewById(R.id.add_complete_button);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCityList();
            }
        });

        layoutManager = new LinearLayoutManager(this);
        adapter = new CityRecyclerAdapter(this, new OnCityDeleteListener() {
            @Override
            public void onDeleteCity(City city) {
                deleteCity(city);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.add_city_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addCity()
    {
        String currentInputText = addressTextView.getText().toString();
        if (currentInputText.isEmpty() || currentInputText.equals("null")) {
            AlertDialogHelper.showWarningDialog(this, "Внимание", "Необходимо ввести город!");
            return;
        }

        City foundCity = dataManager.getCityByString(currentInputText);

        if(foundCity == null) {
            AlertDialogHelper.showWarningDialog(this, "Внимание", "Введенный город не найден!");
            return;
        }

        if(isCityContainInList(foundCity)) {
            AlertDialogHelper.showWarningDialog(this, "Внимание", "Введенный город уже есть в списке!");
            return;
        }

        addressTextView.setText("");

        cityList.add(foundCity);
        updateAdapter();
    }

    private void deleteCity(City city)
    {
        City deleteCity = getCityOfList(city);

        if(deleteCity == null)
            return;

        cityList.remove(deleteCity);
        updateAdapter();
    }

    private City getCityOfList(City city)
    {
        City foundCity = null;

        for(City currentCity : cityList)
        {
            if(currentCity.equals(city))
            {
                foundCity = currentCity;
                break;
            }
        }

        return foundCity;
    }

    private boolean isCityContainInList(City city)
    {
        boolean isContains = false;

        for(City currentCity : cityList)
        {
            if(currentCity.getServerId().equals(city.getServerId()))
            {
                isContains = true;
                break;
            }
        }

        return isContains;
    }

    private void updateAdapter()
    {
        adapter.setData(cityList);
        adapter.notifyDataSetChanged();
    }

    private void saveCityList()
    {
        if(cityList.size() == 0)
        {
            AlertDialogHelper.showWarningDialog(this, "Внимание", "Должен быть добавлен хотя бы один город!");
            return;
        }

        List<City> newCityList = new ArrayList<>(cityList);
        for(City currentCity : newCityList)
        {
            try {
                if(dataManager.addNewCity(currentCity))
                    deleteCity(currentCity);
            }
            catch (Exception ex)
            {
                Log.e("saveCityList", ex.getMessage());
            }
        }

        if(cityList.size() != 0)
        {
            AlertDialogHelper.showWarningDialog(this,
                    "Внимание", "Не удалось сохранить некоторые города. Попробуйте повторить операцию позднее");
            return;
        }

        finish();
    }

    private void hideKeyboardByView(View view)
    {
        if(view == null)
            return;

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if(cityList.size() > 0)
        {
            AlertDialogHelper.showQuestionDialog(this,
                    "Внимание",
                    "Есть несохраненные города. Вы действительно хотите продолжить?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AddCityActivity.super.onBackPressed();
                        }
                    },
                    null);

            return;
        }

        super.onBackPressed();
    }
}
