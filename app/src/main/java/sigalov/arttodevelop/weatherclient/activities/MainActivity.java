package sigalov.arttodevelop.weatherclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.adapters.WeatherRecyclerAdapter;
import sigalov.arttodevelop.weatherclient.data.DataManager;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class MainActivity extends AppCompatActivity {

    DataManager dataManager;

    private TextView infoTextView;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private WeatherRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = DataManager.getInstance();

        layoutManager = new LinearLayoutManager(this);
        adapter = new WeatherRecyclerAdapter();

        infoTextView = (TextView) findViewById(R.id.main_info_text_view);

        progressBar = (ProgressBar) findViewById(R.id.main_progress_bar);

        recyclerView = (RecyclerView) findViewById(R.id.main_city_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SyncTask().execute();
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

        new SyncTask().execute();
    }

    private void goToAddCityActivity()
    {
        Intent intent = new Intent(this, AddCityActivity.class);
        startActivity(intent);
    }

    private class SyncTask extends AsyncTask<Void, Void, List<Weather>> {

        @Override
        protected List<Weather> doInBackground(Void... params) {
            return dataManager.getAllWeatherLocalList();
        }

        @Override
        protected void onPostExecute(List<Weather> weatherList) {
            super.onPostExecute(weatherList);
            swipeRefreshLayout.setRefreshing(false);

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
    }
}
