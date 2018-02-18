package uz.myweather.RetrofitInterface;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import uz.myweather.PojoWeather.WeatherCall;

public interface RetroClient {

    @GET("data/2.5/group")
    Call<WeatherCall> getAllWeather(@QueryMap Map<String, String> options);

    @GET("data/2.5/forecast")
    Call<WeatherCall> getSinglWeather(@QueryMap Map<String, String> options);
}
