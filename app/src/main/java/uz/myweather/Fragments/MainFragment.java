package uz.myweather.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uz.myweather.Adapters.WeatherCardsAdapter;
import uz.myweather.PojoWeather.MyList;
import uz.myweather.PojoWeather.Sys;
import uz.myweather.PojoWeather.WeatherCall;
import uz.myweather.R;
import uz.myweather.RecyclerItemClickListener;
import uz.myweather.RetrofitInterface.Const;
import uz.myweather.RetrofitInterface.RetroClient;

public class MainFragment extends Fragment {

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WeatherCardsAdapter mAdapter;
    private String TAG = "Log";
    private List<MyList> data = new ArrayList<>();

    public MainFragment() {
        Log.d(TAG, "onCreateView: Fragment 01");
    }

    @SuppressLint("ValidFragment")
    public MainFragment(Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_main, container, false);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipeMainFrag);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        mAdapter = new WeatherCardsAdapter();
        RecyclerView rv = mView.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rv.setAdapter(mAdapter);
        Log.d(TAG, "onCreateView: data isEmpty - " + String.valueOf(data.isEmpty()));
        if (data.isEmpty()){
            mSwipeRefreshLayout.setRefreshing(true);
            initWeatherCard();
        } else refreshWeatherData(data);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                initWeatherCard();
            }
        });
        rv.addOnItemTouchListener(new RecyclerItemClickListener(mContext, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: position " + position);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.containerFragment, new SingleWeatherFragment(mContext, position, data));
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        return mView;
    }

    private void initWeatherCard() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetroClient retroClient = retrofit.create(RetroClient.class);
        Map<String, String> map = new HashMap<>();
        map.put("id", Const.getIds());
        map.put("units", Const.units);
        map.put("appid", Const.appid);
        Call<WeatherCall> call = retroClient.getAllWeather(map);
        Log.d(TAG, "initWeatherCard: init...");
        call.enqueue(new Callback<WeatherCall>() {
            @Override
            public void onResponse(Call<WeatherCall> call, Response<WeatherCall> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.code() == 200) {
                    data = response.body().getList();
                    refreshWeatherData(data);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<WeatherCall> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                if (t instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(mContext, "Плохае соединение", Toast.LENGTH_SHORT).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshWeatherData(List<MyList> list) {
        Log.d(TAG, "refreshWeatherData: main frag");
        mAdapter.setList(list, mContext);
        mAdapter.notifyDataSetChanged();
    }
}
