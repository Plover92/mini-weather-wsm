package cn.edu.pku.wangsimin.bean;

/**
 * Created by wangsimin on 16/12/26.
 */

public class WeekWeather {

    private String date;
    private String high;
    private String low;
    private String type;
    private String fengli;

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getType() {
        return type;
    }

    public String getFengli() {
        return fengli;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }
}
