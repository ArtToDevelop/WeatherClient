package sigalov.arttodevelop.weatherclient.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import sigalov.arttodevelop.weatherclient.R;
import sigalov.arttodevelop.weatherclient.adapters.CityRecyclerAdapter;
import sigalov.arttodevelop.weatherclient.data.DataManager;
import sigalov.arttodevelop.weatherclient.models.Weather;

public class MainActivity extends AppCompatActivity {

    DataManager dataManager;

    private RecyclerView recyclerView;
    private CityRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = DataManager.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.main_city_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CityRecyclerAdapter();
        adapter.setData(getTestItems());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("swipeRefreshLayout", "onClick");

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
                Log.i("addButton", "onClick");
            }
        });

        dataManager.testRequest();
    }

    private ArrayList<Weather> getTestItems()
    {
        ArrayList<Weather> weatherList = new ArrayList<>();

        for(int i = 0; i < 20; i++)
        {
            Weather weather1 = new Weather();
            weather1.setName("Казань_" + i);
            weather1.setTemp(25.0);
            weather1.setWindDeg(180.0);
            weather1.setWindSpeed((double)i);

            weatherList.add(weather1);
        }

        return weatherList;
    }
}
