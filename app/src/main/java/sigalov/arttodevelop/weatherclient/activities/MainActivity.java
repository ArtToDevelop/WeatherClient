package sigalov.arttodevelop.weatherclient.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.data.DataManager;

public class MainActivity extends AppCompatActivity {

    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = DataManager.getInstance();


        dataManager.testRequest();


    }
}
