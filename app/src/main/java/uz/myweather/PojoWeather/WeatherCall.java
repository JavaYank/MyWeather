package uz.myweather.PojoWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherCall {

    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private Double message;
    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    @SerializedName("list")
    @Expose
    private List<MyList> list = null;
    @SerializedName("city")
    @Expose
    private City city;

    public List<MyList> getList() {
        return list;
    }

    public Double getMessage() {
        return message;
    }

    public String getCod() {
        return cod;
    }

    public City getCity() {
        return city;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
