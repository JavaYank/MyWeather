package uz.myweather.RetrofitInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Const {

    public static String BASE_URL = "http://api.openweathermap.org/";
    public static String appid = "e6d71a62bfa5f43844006056ac01eacd";
    public static String units = "metric";
    public static int[] ids = {1512569, 5357527, 292223, 524894, 4164143, 6455259, 745044, 1796236, 3435910, 6356055};
    public static String tashkent = "1512569";
    public static String hollywood = "1512569";
    public static String dubai = "1512569";
    public static String moskva = "1512569";
    public static String miami_beach = "1512569";
    public static String paris = "1512569";
    public static String istanbul = "1512569";
    public static String shanghai = "1512569";
    public static String buenos_aires = "1512569";
    public static String barselona = "1512569";
    public static List<String> url;

    public static String getIds() {
        String s = Arrays.toString(ids);
        s = s.replace("[", "");
        s = s.replace("]", "");
        s = s.replace(" ", "");
        return s;
    }

    public static String getImgUrl(int position){
        url = new ArrayList<>(10);
        url.add("https://c1.staticflickr.com/5/4706/39582495234_09b973d92a_n_d.jpg");
        url.add("https://cdn.pixabay.com/photo/2013/09/23/13/37/hollywood-185245_640.jpg?attachment");
        return url.get(position);
    }
}
