package sigalov.arttodevelop.weatherclient.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import sigalov.arttodevelop.weatherclient.adapters.address.AddressAutoCompleteAdapter;
import sigalov.arttodevelop.weatherclient.adapters.address.AddressAutoCompleteTextView;
import sigalov.arttodevelop.weatherclient.models.City;

public class AddCityActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button addButton;

    private AddressAutoCompleteAdapter addressAutoCompleteAdapter;
    private AddressAutoCompleteTextView addressTextView;

    private InputMethodManager inputMethodManager;


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
                Log.i("addButton", "onClick");
            }
        });

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

    private void hideKeyboardByView(View view)
    {
        if(view == null)
            return;

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
