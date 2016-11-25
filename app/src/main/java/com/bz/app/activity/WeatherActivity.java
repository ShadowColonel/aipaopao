package com.bz.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bz.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends BaseActivity {

    private TextView cityTx;
    private TextView pmTx;
    private TextView airQualityTx;
    private TextView weatherTx;
    private TextView temperatureTx;
    private ImageView weaImg;
    private TextView indexTx;
    private static final String API_KEY = "2xq06elpebeluaxe";
    private static final String LOG_TAG = "WeatherActivity";


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            HashMap<String, String> weatherMap = (HashMap<String, String>) msg.obj;
            String text = weatherMap.get("text");
            String code = weatherMap.get("code");
            String path = weatherMap.get("path");
            String temp = weatherMap.get("temperature");
            String pm25 = weatherMap.get("pm25");
            String quality = weatherMap.get("quality");
            String index = weatherMap.get("brief");

            String[] citys = path.split(",");
            String city = citys[citys.length - 2] + " " + citys[citys.length - 3];

            int resId = getResources().getIdentifier("weathercode" + code, "drawable", getPackageName());
            weaImg.setImageResource(resId);
            cityTx.setText(city);
            pmTx.setText(String.format(getResources().getString(R.string.pm), pm25));
            airQualityTx.setText(String.format(getResources().getString(R.string.airQuality), quality));
            indexTx.setText(String.format(getResources().getString(R.string.index), index));
            temperatureTx.setText(String.format(getResources().getString(R.string.temp), temp));
            weatherTx.setText(text);

            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather, "天气状况");

        init();

        SharedPreferences pref = getSharedPreferences("latlng", MODE_PRIVATE);
        String mLatStr = pref.getFloat("lat", 0f) + "";
        String mLngStr = pref.getFloat("lng", 0f) + "";

        Log.v(LOG_TAG, "latlng---->" + mLatStr + "," + mLngStr);

        setWeather(mLatStr, mLngStr);

    }

    private void init() {
        cityTx = (TextView) findViewById(R.id.header_weather_city_tx);
        pmTx = (TextView) findViewById(R.id.header_weather_pm2_5_tx);
        airQualityTx = (TextView) findViewById(R.id.header_weather_air_tx);
        weatherTx = (TextView) findViewById(R.id.header_weather_wea_tx);
        temperatureTx = (TextView) findViewById(R.id.header_weather_temperature_tx);
        indexTx = (TextView) findViewById(R.id.header_weather_index_tx);
        weaImg = (ImageView) findViewById(R.id.header_weather_img);
    }

    private void setWeather(final String latStr, final String lngStr) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();
                //天氣
                StringBuffer urlStr1 = new StringBuffer();
                String temp = API_KEY + "&location=" + latStr + ":" + lngStr + "&language=zh-Hans&unit=c";
                urlStr1.append("https://api.thinkpage.cn/v3/weather/now.json?key=");
                urlStr1.append(temp);

                //空氣質量
                StringBuffer urlStr2 = new StringBuffer();
                urlStr2.append("https://api.thinkpage.cn/v3/air/now.json?key=");
                urlStr2.append(temp);

                //生活指數
                StringBuffer urlStr3 = new StringBuffer();
                urlStr3.append("https://api.thinkpage.cn/v3/life/suggestion.json?key=");
                urlStr3.append(temp);

                Request request1 = new Request.Builder().url(urlStr1.toString()).build();
                Request request2 = new Request.Builder().url(urlStr2.toString()).build();
                Request request3 = new Request.Builder().url(urlStr3.toString()).build();

                Map<String, String> weatherMap = new HashMap<>();

                try {
                    Response responseWeather = client.newCall(request1).execute();
                    Response responseAir = client.newCall(request2).execute();
                    Response responseLife = client.newCall(request3).execute();

                    if (responseWeather.isSuccessful() && responseAir.isSuccessful() && responseLife.isSuccessful()) {
                        String weatherJsonStr = responseWeather.body().string();
                        String airJsonStr = responseAir.body().string();
                        String lifeJsonStr = responseLife.body().string();

                        JSONObject obj = new JSONObject(weatherJsonStr);
                        JSONArray results = obj.getJSONArray("results");
                        JSONObject obj1 = results.getJSONObject(0);
                        JSONObject location = obj1.getJSONObject("location");
                        String path = location.getString("path");  //所在城市地區
                        JSONObject now = obj1.getJSONObject("now");
                        String text = now.getString("text");  //天氣現象
                        String code = now.getString("code");  //天氣代碼
                        String temperature = now.getString("temperature");  //溫度

                        weatherMap.put("path", path);
                        weatherMap.put("text", text);
                        weatherMap.put("code", code);
                        weatherMap.put("temperature", temperature);

                        JSONObject obj2 = new JSONObject(airJsonStr);
                        JSONArray results2 = obj2.getJSONArray("results");
                        JSONObject obj3 = results2.getJSONObject(0);
                        JSONObject air = obj3.getJSONObject("air");
                        JSONObject city = air.getJSONObject("city");
                        String pm25 = city.getString("pm25");  //pm2.5
                        String quality = city.getString("quality");  //空氣質量

                        weatherMap.put("pm25", pm25);
                        weatherMap.put("quality", quality);

                        JSONObject obj4 = new JSONObject(lifeJsonStr);
                        JSONArray results3 = obj4.getJSONArray("results");
                        JSONObject obj5 = results3.getJSONObject(0);
                        JSONObject suggestion = obj5.getJSONObject("suggestion");
                        JSONObject sport = suggestion.getJSONObject("sport");
                        String brief = sport.getString("brief");  //運動指數

                        weatherMap.put("brief", brief);

                        Message message = new Message();
                        message.obj = weatherMap;
                        handler.sendMessage(message);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
