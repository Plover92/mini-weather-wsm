package cn.edu.pku.wangsimin.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangsimin.app.MyApplication;
import cn.edu.pku.wangsimin.bean.City;
import cn.edu.pku.wangsimin.bean.TodayWeather;
import cn.edu.pku.wangsimin.util.NetUtil;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;
    private ProgressBar mProgressBar;
    private ImageView mShareBtn;
    private ImageView mLocateBtn;

    private ImageView mCitySelect;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv4,R.id.iv5};

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch(msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private MyApplication cityApplication;
    private List<City> mcity;
    private ArrayList<String> cityName = new ArrayList<String>();
    private ArrayList<String> cityId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        mShareBtn = (ImageView) findViewById(R.id.title_share);
        mShareBtn.setOnClickListener(this);

        mLocateBtn = (ImageView) findViewById(R.id.title_location);
        mLocateBtn.setOnClickListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.title_update_progress);

        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK!", Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了!", Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();
        initDots();
    }

    void initDots(){
        dots = new ImageView[views.size()];
        for(int i=0; i<views.size(); i++){
            dots[i]=(ImageView)findViewById(ids[i]);
        }
    }

    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.three_days1,null));
        views.add(inflater.inflate(R.layout.three_days2,null));
        vpAdapter = new ViewPagerAdapter(views,this);
        vp = (ViewPager)findViewById(R.id.next_week_weather);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {
        for(int a=0; a<ids.length; a++){
            if(a == i){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            } else{
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度:"+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"～"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());

        if(todayWeather.getPm25() != null) {
            int pm25 = Integer.parseInt(todayWeather.getPm25());
            if(pm25>0&&pm25<=50){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            }
            else if(pm25>50&&pm25<=100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }
            else if(pm25>100&&pm25<=150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            }
            else if(pm25>150&&pm25<=200){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            }
            else if(pm25>200&&pm25<=300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
            else if(pm25>300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
        }

        switch(todayWeather.getType()){
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
        }
        mUpdateBtn.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                mShareBtn.getLayoutParams();
        layoutParams.addRule(RelativeLayout.LEFT_OF,R.id.title_update_btn);
        mShareBtn.setLayoutParams(layoutParams);

        Toast.makeText(MainActivity.this,"更新成功!",Toast.LENGTH_SHORT).show();

    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather != null){
                            if(xmlPullParser.getName().equals("city")){
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("updatetime")){
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("shidu")){
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("wendu")){
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("pm25")){
                                //String pm25 = xmlPullParser.getName();
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("quality")){
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0){
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 0){
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }else if(xmlPullParser.getName().equals("date") && dateCount == 0){
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }else if(xmlPullParser.getName().equals("high") && highCount == 0){
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }else if(xmlPullParser.getName().equals("low") && lowCount == 0){
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if(xmlPullParser.getName().equals("type") && typeCount == 0){
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }

                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }
    /**
     *
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str = reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather",responseStr);

                    todayWeather = parseXML(responseStr);
                    if(todayWeather != null){
                        Log.d("myWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view){

        if(view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this, SelectCity.class);
            //startActivity(i);
            startActivityForResult(i,1);
        }

        if(view.getId() == R.id.title_update_btn){

            mUpdateBtn.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) mShareBtn.getLayoutParams();
            layoutParams.addRule(RelativeLayout.LEFT_OF,R.id.title_update_progress);
            mShareBtn.setLayoutParams(layoutParams);

            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            }else
            {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了!", Toast.LENGTH_LONG).show();
            }
        }
        if(view.getId() == R.id.title_location){
            mLocationClient = new LocationClient(getApplicationContext());
            // 设置定位参数
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);//打开GPS
            option.setCoorType("bd09ll");//默认gcj02，设置返回的定位结果坐标系
            //设置获取地址信息
            option.setIsNeedAddress(true);
            mLocationClient.setLocOption(option);
            //注册监听函数
            mLocationClient.registerLocationListener(myListener);
            //调用此方法开始定位
            mLocationClient.start();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            }else
            {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location == null){
                return;
            }
            Log.d("myWeather",location.getLocType()+"");
            if(location.getLocType() == BDLocation.TypeNetWorkLocation){//网络定位结果
                Toast.makeText(MainActivity.this,"定位成功:您位于"+location.getCity()
                        +location.getDistrict(), Toast.LENGTH_SHORT).show();
            }else if(location.getLocType() == BDLocation.TypeOffLineLocationFail
                    || location.getLocType() == BDLocation.TypeOffLineLocation){
                Toast.makeText(MainActivity.this, "定位失败，请检查网络是否开启", Toast.LENGTH_SHORT).show();
            }
            cityApplication = (MyApplication) getApplication();
            mcity = cityApplication.getCityList();
            for(int i = 0; i < mcity.size(); i++){
                cityName.add(mcity.get(i).getCity());
                cityId.add(mcity.get(i).getNumber());
            }
            String district = location.getDistrict().substring(0,location.getCity().length()-1);
            String name = location.getCity().substring(0,location.getCity().length()-1);
            int index = cityName.indexOf(district);
            if(index == -1){
                index = cityName.indexOf(name);
            }
            String citycode = cityId.get(index);
            Log.d("myWeather",citycode);
            if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORN_NONE) {
                queryWeatherCode(citycode);
            }
            SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
            editor.putString("main_city-code",citycode);
            editor.commit();
        }
    }
}

