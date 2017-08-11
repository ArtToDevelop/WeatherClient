package sigalov.arttodevelop.weatherclient.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.adapters.WeatherRecyclerAdapter;
import sigalov.arttodevelop.weatherclient.data.DataManager;
import sigalov.arttodevelop.weatherclient.helpers.AlertDialogHelper;
import sigalov.arttodevelop.weatherclient.interfaces.OnProgressSyncChangeListener;
import sigalov.arttodevelop.weatherclient.interfaces.OnWeatherDeleteListener;
import sigalov.arttodevelop.weatherclient.models.City;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class MainActivity extends AppCompatActivity implements OnProgressSyncChangeListener {

    DataManager dataManager;

    private TextView infoTextView;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private WeatherRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FloatingActionButton addButton;

    private boolean isSyncNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = DataManager.getInstance();

        layoutManager = new LinearLayoutManager(this);
        adapter = new WeatherRecyclerAdapter(this, new OnWeatherDeleteListener() {
            @Override
            public void onWeatherDelete(Weather weather) {
                deleteWeather(weather);
            }
        });

        infoTextView = (TextView) findViewById(R.id.main_info_text_view);

        progressBar = (ProgressBar) findViewById(R.id.main_progress_bar);

        recyclerView = (RecyclerView) findViewById(R.id.main_city_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startSync();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        addButton = (FloatingActionButton) findViewById(R.id.main_fab_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddCityActivity();
            }
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startSync();
    }

    private void updateUI()
    {
        updateUI(dataManager.getAllWeatherLocalList());
    }

    private void updateUI(final List<Weather> weatherList)
    {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.setData(weatherList);
                adapter.notifyDataSetChanged();

                if(weatherList == null || weatherList.size() == 0)
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    infoTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    recyclerView.setVisibility(View.VISIBLE);
                    infoTextView.setVisibility(View.INVISIBLE);
                }

                adapter.setData(weatherList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void startSync()
    {
        if(isSyncNow)
            return;

        isSyncNow = true;

        onProgressSyncChange(0.0);
        new SyncTask(this, (double)progressBar.getMax()).execute();
    }

    private void goToAddCityActivity()
    {
        Intent intent = new Intent(this, AddCityActivity.class);
        startActivity(intent);
    }

    @Override
    public void onProgressSyncChange(final Double value) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(value == progressBar.getMax())
                {
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress((int)(value * 100 / progressBar.getMax()));
                }
            }
        });
    }

    private void deleteWeather(final Weather weather)
    {
        AlertDialogHelper.showQuestionDialog(this, getResources().getString(R.string.main_dialog_warning_title),
                String.format(getResources().getString(R.string.main_question_delete_city), weather.getName()),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataManager.deleteCity(weather.getCityId());

                        adapter.setData(dataManager.getAllWeatherLocalList());
                        adapter.notifyDataSetChanged();
                    }
                },
                null);
    }

    private class SyncTask extends AsyncTask<Void, Void, List<Weather>> {

        OnProgressSyncChangeListener progressListener;
        Double maxProgressValue;

        public SyncTask(OnProgressSyncChangeListener progressListener, Double maxProgressValue)
        {
            this.progressListener = progressListener;
            this.maxProgressValue = maxProgressValue;
        }

        @Override
        protected List<Weather> doInBackground(Void... params) {
            List<City> cityList = dataManager.getAllCityLocalList();

            if (cityList != null && cityList.size() > 0)
            {
                Double currentProgressValue = 0.0;
                Double progressChangeValue = (maxProgressValue / cityList.size());

                for(City city : cityList)
                {
                    try {
                        dataManager.updateWeatherByCity(city);
                    }
                    catch (Exception ex)
                    {
                        Log.e(String.format("Error sync city - %s", city.getName()), ex.getMessage());
                    }

                    currentProgressValue += progressChangeValue;
                    progressListener.onProgressSyncChange(currentProgressValue);
                }
            }

            return dataManager.getAllWeatherLocalList();
        }

        @Override
        protected void onPostExecute(List<Weather> weatherList) {
            super.onPostExecute(weatherList);
            swipeRefreshLayout.setRefreshing(false);

            updateUI(weatherList);

            isSyncNow = false;
            onProgressSyncChange((double)progressBar.getMax());
        }

    }
}
