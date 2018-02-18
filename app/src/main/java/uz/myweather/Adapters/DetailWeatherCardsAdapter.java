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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uz.myweather.PojoWeather.MyList;
import uz.myweather.R;

public class DetailWeatherCardsAdapter extends RecyclerView.Adapter<DetailWeatherCardsAdapter.DetailWeatherViewHolder> {

    private List<MyList> data1 = new ArrayList<>();
    private List<MyList> data2 = new ArrayList<>();
    private Context mContext;

    @Override
    public DetailWeatherCardsAdapter.DetailWeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_single, parent, false);
        return new DetailWeatherCardsAdapter.DetailWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailWeatherCardsAdapter.DetailWeatherViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date_ = null;
        try {
            date_ = sdf.parse(data1.get(position).getDt_txt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf_ = new SimpleDateFormat("dd.MM.yyyy");
        String date = sdf_.format(date_);
        String temp = "in the morning " + data1.get(position).getMain().getTemp().intValue() + "ºC\n" +
                "in the evening " + data2.get(position).getMain().getTemp().intValue() + "ºC";
        holder.cityS.setText(date);
        holder.tempS.setText(temp);
        Picasso.with(mContext)
                .load("http://openweathermap.org/img/w/" + data1.get(position).getWeather().get(0).getIcon() + ".png")
                .error(R.drawable.icon_white)
                .into(holder.imgS);

    }

    @Override
    public int getItemCount() {
        return data2.size();
    }

    public void setList(List<MyList> list1, List<MyList> list2, Context context) {
        Log.d("Log", "setList: set");
        this.data1 = list1;
        this.data2 = list2;
        this.mContext = context;
    }

    static class DetailWeatherViewHolder extends RecyclerView.ViewHolder {

        TextView cityS;
        ImageView imgS;
        TextView tempS;

        DetailWeatherViewHolder(View itemView) {
            super(itemView);
            cityS = itemView.findViewById(R.id.singleCity);
            imgS = itemView.findViewById(R.id.singleImg);
            tempS = itemView.findViewById(R.id.singleTemp);

        }
    }
}
