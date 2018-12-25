package com.example.tadar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.baidu.mapapi.model.LatLng;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Map;


/**
 * Created by lenovo on 2018/12/13.
 */
//通过逆地址解析获取到城市
public class GetAddressInfor {
    Double lat;
    Double lng;
    public GetAddressInfor(LatLng latLng){
        lat = latLng.latitude;
        lng = latLng.longitude;
    }
    public void getAddress(final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //组件网址
                    String url_s = "http://api.map.baidu.com/geocoder?output=json&location="+lng+",%20"+lat+
                            "&key=j0fSDg8PsX3FQhvLdN5o9FUL4NbRWlD5";
                    URL url = new URL(url_s);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //设置连接属性，不喜欢的话可以直接默认
                    conn.setConnectTimeout(5000); //设置连接最长时间
                    conn.setUseCaches(false); //数据不多时可以不使用缓存
                    //这里连接了
                    conn.connect();
                    //这里才真正获取到数据
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader input = new InputStreamReader(inputStream);
                    BufferedReader buffer = new BufferedReader(input);
                    if (conn.getResponseCode() == 200) {
                        //返回两百意味着ok
                        String inputLine;
                        StringBuffer resultData = new StringBuffer();
                        while ((inputLine = buffer.readLine()) != null) {
                            resultData.append(inputLine);
                        }//把所有的数据都存到inputLine中
                        String text = resultData.toString();
                        JSONObject jsonObject = new JSONObject(text);
                        //Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();

                        System.out.println(text);
                        JSONObject jsonAdd = (JSONObject)jsonObject.get("result");
                        System.out.println(jsonAdd.toString());
                        jsonAdd = (JSONObject)jsonAdd.get("addressComponent");
                        //System.out.println(jsonAdd.toString());

                        String city =(String) jsonAdd.get("city");
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("city",city);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
