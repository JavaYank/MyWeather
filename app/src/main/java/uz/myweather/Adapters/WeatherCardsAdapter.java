package uz.myweather.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uz.myweather.PojoWeather.MyList;
import uz.myweather.R;

public class WeatherCardsAdapter extends RecyclerView.Adapter<WeatherCardsAdapter.WeatherViewHolder> {

    private List<MyList> data = new ArrayList<>();
    private Context mContext;

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        holder.city.setText(String.valueOf(data.get(position).getName() + ", "
                + data.get(position).getSys().getCountry()));
        holder.temp.setText(String.valueOf(data.get(position).getMain().getTemp().intValue() + "ºC"));
        Picasso.with(mContext)
                .load("http://openweathermap.org/img/w/" + data.get(position).getWeather().get(0).getIcon() + ".png")
                .error(R.mipmap.icon_white)
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setList(List<MyList> list, Context context) {
        Log.d("Log", "setList: set");
        this.data = list;
        this.mContext = context;
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {

        TextView city;
        ImageView img;
        TextView temp;

        WeatherViewHolder(View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.city);
            img = itemView.findViewById(R.id.img);
            temp = itemView.findViewById(R.id.temp);

        }
    }
}
