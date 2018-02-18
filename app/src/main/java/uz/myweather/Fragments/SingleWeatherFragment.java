package uz.myweather.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TintableCompoundButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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
import uz.myweather.Adapters.DetailWeatherCardsAdapter;
import uz.myweather.Adapters.WeatherCardsAdapter;
import uz.myweather.OnBackPressedListener;
import uz.myweather.PojoWeather.MyList;
import uz.myweather.PojoWeather.WeatherCall;
import uz.myweather.R;
import uz.myweather.RetrofitInterface.Const;
import uz.myweather.RetrofitInterface.RetroClient;

public class SingleWeatherFragment extends Fragment implements OnBackPressedListener {

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mView;
    private static final String TAG = "Log";
    private int position;
    private WeatherCall mWeatherCall;
    private TextView mCityWeather;
    private ImageView snglImg;
    private TextView mTemp;
    private TextView mDetail;
    private MyList mMyList;
    private DetailWeatherCardsAdapter mAdapter;
    private List<MyList> data = new ArrayList<>();

    public SingleWeatherFragment() {
    }

    @SuppressLint("ValidFragment")
    public SingleWeatherFragment(Context context, int position, MyList data) {
        this.mContext = context;
        this.position = position;
        this.mMyList = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_single_weather, container, false);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipeSingleFrag);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        if (mWeatherCall == null) {
            mSwipeRefreshLayout.setRefreshing(true);
            initDetailWeather();
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                initDetailWeather();
            }
        });
        mCityWeather = mView.findViewById(R.id.cityWeather);
        snglImg = mView.findViewById(R.id.snglImg);
        mTemp = mView.findViewById(R.id.snglTemp);
        mDetail = mView.findViewById(R.id.detail);
        mAdapter = new DetailWeatherCardsAdapter();
        RecyclerView rv = mView.findViewById(R.id.snglRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rv.setAdapter(mAdapter);
        refreshDetailWeatherData(data, data);
        setData();
        return mView;
    }

    private void initDetailWeather() {
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
        map.put("id", String.valueOf(Const.ids[position]));
        map.put("units", Const.units);
        map.put("appid", Const.appid);
        Call<WeatherCall> call = retroClient.getSinglWeather(map);
        Log.d(TAG, "initDetailWeather: init...");
        call.enqueue(new Callback<WeatherCall>() {
            @Override
            public void onResponse(Call<WeatherCall> call, Response<WeatherCall> response) {
                Log.d(TAG, "onResponse: " + response.code());
                Log.d(TAG, "onResponse: message" + response.body().getMessage());
                if (response.body().getCod().equals("200")) {
                    Log.d(TAG, "onResponse: city - " + response.body().getCity().getName());
                    mWeatherCall = response.body();
                    clearDate(mWeatherCall.getList());

                }
                mSwipeRefreshLayout.setRefreshing(false);
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

    private void clearDate(List<MyList> list) {
        List<MyList> newList1 = new ArrayList<>();
        List<MyList> newList2 = new ArrayList<>();
        for (MyList myList : list){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(myList.getDt_txt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String monthDay = (String) android.text.format.DateFormat.format("MM.dd", date);
            String monthDayToday = (String) android.text.format.DateFormat.format("MM.dd", new Date());
            Double d = Double.parseDouble(monthDay);
            Double today = Double.parseDouble(monthDayToday);
            if (d > today){
                String hour = (String) android.text.format.DateFormat.format("HH", date);
                if (hour.equals("09")){
                    newList1.add(myList);
                }
                if (hour.equals("21")){
                    newList2.add(myList);
                }
            }
        }
        // жизнь - боль с этим API
        refreshDetailWeatherData(newList1, newList2);
    }

    private void setData() {
        String sunrise = getCorrectTime(mMyList.getName(), mMyList.getSys().getSunrise());
        String sunset = getCorrectTime(mMyList.getName(), mMyList.getSys().getSunset());
        String cityWeather =
                mMyList.getName() + ", " + mMyList.getSys().getCountry() + "\n" +
                mMyList.getWeather().get(0).getMain() + ", " + mMyList.getWeather().get(0).getDescription();
        String detail =
                "Wind " + mMyList.getWind().getSpeed() + " m/s | "+ getDirection(mMyList) + "\n" +
                "Humidity " + mMyList.getMain().getHumidity() + "%\n" +
                "Pressure " + mMyList.getMain().getPressure() + "hPa\n" +
                "Sunrise " + sunrise + "\n" +
                "Sunset " + sunset;
        mCityWeather.setText(cityWeather);
        Picasso.with(mContext)
                .load("http://openweathermap.org/img/w/" + mMyList.getWeather().get(0).getIcon() + ".png")
                .error(R.mipmap.icon_white)
                .into(snglImg);
        mTemp.setText(mMyList.getMain().getTemp().intValue() + "ºC");
        mDetail.setText(detail);
    }

    private String getDirection(MyList myList) {
        int value = myList.getWind().getDeg().intValue();
        Log.d(TAG, "getDirection: " + value);
        if (value > 337 & value <= 360) {
            Log.d(TAG, "getDirection: 1");
            return "N";
        } else if (value > 0 & value <= 22){
            Log.d(TAG, "getDirection: 2");
            return "N";
        } else if (value > 22 & value <= 67){
            return "NO";
        } else if (value > 67 & value <= 112){
            return "O";
        } else if (value > 112 & value <= 157){
            return "SO";
        } else if (value > 157 & value <= 202){
            return "S";
        } else if (value > 202 & value <= 247){
            return "SW";
        } else if (value > 247 & value <= 292) {
            return "W";
        } else if (value > 292 & value <= 337){
            return "NW";
        }
        return "";
    }

    private String getCorrectTime(String locale, Long i) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        switch (locale){
            case "Tashkent":
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tashkent"));
                break;
            case "Hollywood":
                sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                break;
            case "Dubai":
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Dubai"));
                break;
            case "Moskva":
                sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT-3"));
                break;
            case "Miami Beach":
                sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT+5"));
                break;
            case "Paris":
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                break;
            case "Istanbul":
                sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT-3"));
                break;
            case "Shanghai":
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                break;
            case "Buenos Aires":
                sdf.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
                break;
            case "Barcelona":
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
                break;
        }
        Date netDate = new Date(i * 1000);
        String str = sdf.format(netDate);
        Log.d(TAG, "onCreateView: time " + str);
        Log.d(TAG, "onCreateView: time " + String.valueOf(i*1000));
        return str;
    }

    private void refreshDetailWeatherData(List<MyList> list1, List<MyList> list2) {
        Log.d(TAG, "refreshDetailWeatherData: single frag");
        mAdapter.setList(list1, list2, mContext);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = ((AppCompatActivity) mView.getContext()).getSupportFragmentManager();
        manager.popBackStack();
    }
}
