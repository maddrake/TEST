package com.jaewon.project1;

import android.media.MediaRecorder;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by won on 2015-07-17.
 */
public class myRecycleAdapter extends RecyclerView.Adapter<myRecycleAdapter.ViewHolder> {

    private List<ForecastItem> forecastItem;
    private int itemLayout;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView forecast_text;

        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.imgView);
            forecast_text = (TextView) itemView.findViewById(R.id.list_item_forecast_textview);
        }
    }
    public void add(ArrayList list, ForecastItem Item, String forecast_Text, ArrayList<String> description,int position) {

        String data = description.get(position);

        Item.setText(forecast_Text);

        switch (data) {
            case "thunderstorm" :
                Item.setImg(R.drawable.j);
                break;
            case "Rain" :
                Item.setImg(R.drawable.d);
                break;
            case "Clouds" :
                Item.setImg(R.drawable.h);
                break;
            default:
                Item.setImg(R.mipmap.ic_launcher);
                break;
        }
        list.add(Item);
    }


    public myRecycleAdapter(List<ForecastItem> items, int itemLayout) {
        this.forecastItem = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public myRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myRecycleAdapter.ViewHolder holder, int position) {
        ForecastItem item = forecastItem.get(position);
        holder.forecast_text.setText(item.getText());
        holder.img.setBackgroundResource(item.getImg());
        holder.itemView.setTag(item);
    }

    public ForecastItem getItem(ArrayList<ForecastItem> forecastItem, int position) {
        ForecastItem s = forecastItem.get(position);
        return s;
    }
    @Override
    public int getItemCount() {
    return forecastItem.size();
    }
}

