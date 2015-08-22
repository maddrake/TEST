package com.jaewon.project1;

/**
 * Created by won on 2015-07-17.
 */
public class ForecastItem {
    private String text_forecast_date;
    private String text_forecast_temp;
    private String text;
    private String text_forecast_high_temp;
    private String text_forecast_low_temp;
    private int img;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getText_forecast_date() {
        return text_forecast_date;
    }

    public void setText_forecast_date(String text_forecast_date) {
        this.text_forecast_date = text_forecast_date;
    }

    public String getText_forecast_temp() {
        return text_forecast_temp;
    }

    public void setText_forecast_temp(String text_forecast_temp) {
        this.text_forecast_temp = text_forecast_temp;
    }

    public String getText_forecast_high_temp() {
        return text_forecast_high_temp;
    }

    public void setText_forecast_high_temp(String text_forecast_high_temp) {
        this.text_forecast_high_temp = text_forecast_high_temp;
    }

    public String getText_forecast_low_temp() {
        return text_forecast_low_temp;
    }

    public void setText_forecast_low_temp(String text_forecast_low_temp) {
        this.text_forecast_low_temp = text_forecast_low_temp;
    }
}