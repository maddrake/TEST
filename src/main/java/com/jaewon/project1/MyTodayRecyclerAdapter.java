package com.jaewon.project1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyTodayRecyclerAdapter extends RecyclerView.Adapter<MyTodayRecyclerAdapter.ViewHolder> {

    private List<ForecastItem> forecastItem;
    private int itemLayout;

    public MyTodayRecyclerAdapter(List<ForecastItem> items, int itemLayout) {
        this.forecastItem = items;
        this.itemLayout = itemLayout;
    }

    public void additems(ArrayList list, ForecastItem Item, String forecast_Text,
                         ArrayList<String> description,ArrayList<String> date,
                         ArrayList<String> highTemp, ArrayList<String> lowTemp, int position) {

        String weatherText= description.get(position);
        String dateText = date.get(position);
        String high_temp = highTemp.get(position);
        String low_temp = lowTemp.get(position);

        Item.setText_forecast_date(dateText);
        Item.setText_forecast_temp(weatherText);
        Item.setText_forecast_high_temp(high_temp);
        Item.setText_forecast_low_temp(low_temp);

        switch (weatherText) {
            case "thunderstorm" :
                Item.setImg(R.drawable.j);
                break;
            case "Rain" :
                Item.setImg(R.drawable.d);
                break;
            case "Clouds" :
                Item.setImg(R.drawable.h);
                break;
            case "Clear" :
                Item.setImg(R.drawable.c);
                break;
            default:
                Item.setImg(R.mipmap.ic_launcher);
                break;
        }
        list.add(Item);
    }

    @Override
    public MyTodayRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyTodayRecyclerAdapter.ViewHolder holder, int position) {
        ForecastItem item = forecastItem.get(position);
        holder.forecast_text.setText(item.getText_forecast_date());
        holder.forecast_text2.setText(item.getText_forecast_temp());
        holder.forecast_text3.setText(item.getText_forecast_high_temp());
        holder.forecast_text4.setText(item.getText_forecast_low_temp());

        holder.img.setBackgroundResource(item.getImg());
        holder.itemView.setTag(item);
    }

    public ForecastItem getItem(ArrayList<ForecastItem> forecastItem, int position) {
        ForecastItem forecastItem1 = forecastItem.get(position);
        return forecastItem1;
    }

    @Override
    public int getItemCount() {
        return forecastItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView forecast_text;
        public TextView forecast_text2;
        public TextView forecast_text3;
        public TextView forecast_text4;

        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.list_item_icon_today);
            forecast_text = (TextView) itemView.findViewById(R.id.list_item_forecast_textview_today);
            forecast_text2 = (TextView) itemView.findViewById(R.id.list_item_date_textview_today);
            forecast_text3 = (TextView) itemView.findViewById(R.id.list_item_high_textview_today);
            forecast_text4 = (TextView) itemView.findViewById(R.id.list_item_low_textview_today);

        }
    }
}