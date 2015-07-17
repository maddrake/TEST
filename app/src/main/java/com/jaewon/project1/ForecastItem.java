package com.jaewon.project1;

/**
 * Created by won on 2015-07-17.
 */
public class ForecastItem {
    private String text;
    private int img;

    public int getImg() {
        return img;
    }

    public String getText() {
        return text;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "BananaItem {" + "icon = " + img + ",name" + text + " }";
    }
}
