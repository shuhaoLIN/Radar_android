//package com.example.tadar;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.telephony.SmsMessage;
//import android.text.format.DateFormat;
//import android.widget.Toast;
//import com.baidu.mapapi.map.*;
//import com.baidu.mapapi.model.LatLng;
//import com.example.tadar.friend.FriendInfor;
//
//
//import java.util.ArrayList;
//import java.util.Date;
///**
// * Created by lenovo on 2018/12/16.
// */
//public class LocationReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //接收到的短信，然后更新friend列表
//        Bundle bundle = intent.getExtras();
//        Object[] pdus = (Object[]) bundle.get("pdus"); // 提取短信消息
//        SmsMessage[] messages = new SmsMessage[pdus.length];
//        for (int i = 0; i < messages.length; i++) {
//            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//        }
//        String address = messages[0].getOriginatingAddress(); // 获取发送方号码
//        String fullMessage = "";
//        for (SmsMessage message : messages) {
//            fullMessage += message.getMessageBody(); // 获取短信内容
//        }
//        double longitude;
//        double latitude;
//        Date updateDate = new Date(System.currentTimeMillis());
//        if(fullMessage.startsWith("TADAR")){
//            //解析到相应的数据
//            Toast.makeText(context, fullMessage,Toast.LENGTH_LONG).show();
//            String[] strings = fullMessage.split(",");
//            longitude = Double.parseDouble(strings[1]);
//            latitude = Double.parseDouble(strings[2]);
//        }else{
//            //因为不是我想要的短信
//            return;
//        }
//        //完成数据的更新
//        for(FriendInfor friendInfor : StaticCollection.friendCollection){
//            if(friendInfor.getPhonenum() == address){
//                friendInfor.setLongitude(longitude);
//                friendInfor.setLatitude(latitude);
//                friendInfor.setUpdateDate(updateDate);
//                break;
//
//            }
//        }
//
////        DrawShops(StaticCollection.friendCollection,MainActivity.getBaiduMap(), MainActivity.getmMapView());
//        abortBroadcast();
//    }
//    public void DrawShops(ArrayList<FriendInfor> friendCollection, BaiduMap baiduMap,TextureMapView mMapView)
//    {
//        for(int i=1;i<friendCollection.size();i++) {
//            FriendInfor friendInfor=friendCollection.get(i);
//
//            LatLng cenpt = new LatLng(friendInfor.getLatitude(),friendInfor.getLongitude());//设定中心点坐标
//
//            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.friend_marker);
//            //准备 marker option 添加 marker 使用
//            MarkerOptions markerOption = new MarkerOptions().icon(bitmap).position(cenpt);
//            //获取添加的 marker 这样便于后续的操作
//            Marker marker = (Marker) baiduMap.addOverlay(markerOption);
//
//            OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(20).
//                    fontColor(0xFFFF00FF).text(friendInfor.getName()).rotate(0).position(cenpt);
//            baiduMap.addOverlay(textOption);
//        }
//
//    }
//}
