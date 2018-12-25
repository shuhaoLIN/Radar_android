package com.example.tadar;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.tadar.enemy.EnemiesOperater;
import com.example.tadar.enemy.EnemyActivity;
import com.example.tadar.enemy.EnemyDetailActivity;
import com.example.tadar.enemy.EnemyInfor;
import com.example.tadar.friend.FriendActivity;
import com.example.tadar.friend.FriendInfor;
import com.example.tadar.friend.FriendsOperater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView sweepImage; //imageview_sweep

    public TextureMapView mMapView;
    public BaiduMap baiduMap;
    private Boolean isFirstLocation = true;
    public LocationClient mLocationClient;
    double MyLongitude;
    double MyLatitude;
    double MyAltitude;
    String MyCity;
    LatLng MyLatLng;

    //Handle
    Handler handler;

    //短信
    LocationReceiver locationReceiver;

    Button btn_locate;
    ToggleButton btn_refresh;
    Button btn_friends;
    Button btn_enemies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_main);
        mMapView = (TextureMapView) findViewById(R.id.mapView);
        baiduMap = mMapView.getMap(); // 获取到map待会好进行设置
        baiduMap.setMyLocationEnabled(true); //跟随当前位置信息进行移动的小光标

        //申请权限
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }else {
            requestLocation();
        }


        //初始化imageview_sweep
        sweepImage = (ImageView) findViewById(R.id.imageview_sweep);
        Animation animation = (Animation) AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.rotate_indefinitely);
        sweepImage.setAnimation(animation);
        //初始化Buttons
        btn_locate = (Button) findViewById(R.id.btn_locate);
        btn_refresh = (ToggleButton) findViewById(R.id.btn_refresh);
        btn_friends = (Button) findViewById(R.id.btn_friends);
        btn_enemies = (Button) findViewById(R.id.btn_enemies);

        btn_locate.setOnClickListener(new btnLocationListener());
        btn_friends.setOnClickListener(new btnFriendsListener());
        btn_refresh.setOnClickListener(new btnRefreshListener());
        btn_enemies.setOnClickListener(new btnEnemiesListener());
        //注册短信的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY); //设置最高优先级
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        locationReceiver = new LocationReceiver();
        registerReceiver(locationReceiver, intentFilter);

        //初始化数据
//        MyLocationData MyLocation =  baiduMap.getLocationData();
//        MyLatLng = new LatLng(MyLocation.latitude, MyLocation.longitude);
//        MyLatitude = MyLocation.latitude;
//        MyLongitude = MyLocation.longitude;
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                //获取整数加上字符串
                if(MyCity == null)
                    MyCity = bundle.getString("city");
                Toast.makeText(MainActivity.this, MyCity,Toast.LENGTH_LONG).show();
//                if (city.length() != 0) Toast.makeText(MainActivity.this,city,Toast.LENGTH_LONG).show();
//                else Toast.makeText(MainActivity.this,"no City",Toast.LENGTH_LONG).show();
            }
        };
        initFriendsAndEnemies();

    }
    //初始化数据进行展示。
    public void initFriendsAndEnemies(){
        FriendsOperater opFriend = new FriendsOperater();
        StaticCollection.setFriendCollection(opFriend.loadFriends(MainActivity.this));
        if(StaticCollection.getFriendCollection() == null){
            StaticCollection.setFriendCollection(new ArrayList<FriendInfor>());
        }
        EnemiesOperater opEnemy = new EnemiesOperater();
        StaticCollection.setEnemyCollection(opEnemy.loadEnemies(MainActivity.this));
        if(StaticCollection.getEnemyCollection() == null){
            StaticCollection.setEnemyCollection(new ArrayList<EnemyInfor>());
        }
//        if(StaticCollection.getFriendCollection() != null)
//            DrawFriends(StaticCollection.getFriendCollection(), baiduMap, mMapView);
//        else StaticCollection.setFriendCollection(new ArrayList<FriendInfor>());
//        if(StaticCollection.getEnemyCollection() != null)
//            DrawEnemies(StaticCollection.getEnemyCollection(), baiduMap, mMapView);
//        else StaticCollection.setEnemyCollection(new ArrayList<EnemyInfor>());
    }
    class btnLocationListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            MapStatus mapStatus = new MapStatus.Builder()
                    .target(MyLatLng).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
                    .newMapStatus(mapStatus);
            //改变地图状态
            baiduMap.setMapStatus(mapStatusUpdate);
        }
    }
    class btnRefreshListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            DrawFriends(StaticCollection.getFriendCollection(),baiduMap,mMapView);
            DrawEnemies(StaticCollection.getEnemyCollection(),baiduMap,mMapView);
            //将信息发送出去
            if(StaticCollection.getFriendCollection().size() != 0)
                for(FriendInfor friendInfor : StaticCollection.getFriendCollection()){
                    System.out.println(MyLongitude+","+MyLatitude);
                    sendMessage(MainActivity.this, "where are you?",friendInfor.getPhonenum());
                }
            if(StaticCollection.getEnemyCollection().size() != 0)
                for(EnemyInfor enemyInfor: StaticCollection.getEnemyCollection()){
                    sendMessage(MainActivity.this, "where are you?",enemyInfor.getPhonenum());
                }

        }
    }
    class btnEnemiesListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, EnemyActivity.class);
            startActivity(intent);
        }
    }
    public static void  sendMessage(Context context, String content , String phoneNum){
        SmsManager sms = SmsManager.getDefault();
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
        content = "RADAR,"+content; // 做一个标记，以便后面的分析
        //拟定发送的信息为RADAR,12.123,123.123
        sms.sendTextMessage(phoneNum, null, content, pi, null);
    }
    class btnFriendsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, FriendActivity.class);
            startActivity(intent);
        }
    }
    //广播的接收类
    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到的短信，然后更新friend列表
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus"); // 提取短信消息
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            String address = messages[0].getOriginatingAddress(); // 获取发送方号码
            //Toast.makeText(context, address,Toast.LENGTH_LONG).show();


            String fullMessage = "";
            for (SmsMessage message : messages) {
                fullMessage += message.getMessageBody(); // 获取短信内容
            }
            double longitude;
            double latitude;
            double altitude;
            String city;
            Date updateDate = new Date(System.currentTimeMillis());
            if(fullMessage.endsWith("where are you?")){//如果接收到的是要求发送的地址短信
                sendMessage(MainActivity.this, MyLongitude+","+MyLatitude+","+MyAltitude+","+MyCity,address);
            }
            else if(fullMessage.startsWith("RADAR")){//如果接收到的是地址短信，进行相应的地址更新操作
                //解析到相应的数据
//                Toast.makeText(context, fullMessage,Toast.LENGTH_LONG).show();
                String[] strings = fullMessage.split(",");
                longitude = Double.parseDouble(strings[1]);//经度
                latitude = Double.parseDouble(strings[2]); //纬度
                altitude = Double.parseDouble(strings[3]); // 高度
                city = strings[4];
                //完成数据的更新
                baiduMap.clear();
                for(int i=0;i<StaticCollection.getFriendCollection().size();i++){
                    FriendInfor friendInfor = StaticCollection.getFriendCollection().get(i);
                    //因为号码读出来是+86的，所以加以判断
                    if(friendInfor.getPhonenum().endsWith(address) || address.endsWith(friendInfor.getPhonenum())){
                        friendInfor.setLongitude(longitude);
                        friendInfor.setLatitude(latitude);
                        friendInfor.setAltitude(altitude);
                        friendInfor.setCity(city);

                        friendInfor.setUpdateDate(updateDate);
                        // Toast.makeText(context, friendInfor.getLongitude()+"\n"+friendInfor.getLatitude(),Toast.LENGTH_SHORT);

                        StaticCollection.setFriend(i, friendInfor);
                        break;
                    }
                }
                DrawFriends(StaticCollection.getFriendCollection(),baiduMap,mMapView);
                FriendsOperater op = new FriendsOperater();
                op.saveFriends(MainActivity.this, StaticCollection.getFriendCollection()); //执行保存操作,这个很重要！！！！！！！！！！！！！！！！！！！！！！

                for(int i=0;i<StaticCollection.getEnemyCollection().size();i++){
                    EnemyInfor enemyInfor = StaticCollection.getEnemyCollection().get(i);
                    //因为号码读出来是+86的，所以加以判断
                    if(enemyInfor.getPhonenum().endsWith(address) || address.endsWith(enemyInfor.getPhonenum())){
                        enemyInfor.setLongitude(longitude);
                        enemyInfor.setLatitude(latitude);
                        enemyInfor.setAltitude(altitude);
                        enemyInfor.setCity(city);

                        enemyInfor.setUpdateDate(updateDate);
                        // Toast.makeText(context, friendInfor.getLongitude()+"\n"+friendInfor.getLatitude(),Toast.LENGTH_SHORT);

                        StaticCollection.setEnemy(i, enemyInfor);
                        break;
                    }
                }
                DrawEnemies(StaticCollection.getEnemyCollection(),baiduMap,mMapView);
                EnemiesOperater op2 = new EnemiesOperater();
                op2.saveEnemies(MainActivity.this, StaticCollection.getEnemyCollection()); //执行保存操作,这个很重要！！！！！！！！！！！！！！！！！！！！！！

                abortBroadcast();
            }else{
                //因为不是我想要的短信
                return;
            }
            Toast.makeText(context, StaticCollection.getFriendCollection().size()+"个",Toast.LENGTH_SHORT).show();
        }
    }
    public void DrawFriends(ArrayList<FriendInfor> friendCollection, BaiduMap baiduMap,TextureMapView mMapView) {
        if(friendCollection != null)
        for(int i=0;i<friendCollection.size();i++) {
            //Toast.makeText(MainActivity.this, i+"",Toast.LENGTH_SHORT).show();
            FriendInfor friendInfor=friendCollection.get(i);

            LatLng cenpt = new LatLng(friendInfor.getLatitude(),friendInfor.getLongitude());//设定中心点坐标

            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.friend_marker);
            //准备 marker option 添加 marker 使用
            MarkerOptions markerOption = new MarkerOptions().icon(bitmap).position(cenpt);
            //获取添加的 marker 这样便于后续的操作
            Marker marker = (Marker) baiduMap.addOverlay(markerOption);

            OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(30).
                    fontColor(0xFFFF00FF).text(friendInfor.getName()).rotate(0).position(new LatLng(friendInfor.getLatitude()-0.0008,friendInfor.getLongitude()));
            baiduMap.addOverlay(textOption);
            //Toast.makeText(MainActivity.this, "finish",Toast.LENGTH_SHORT).show();

            OverlayOptions textOptionNum = new TextOptions().bgColor(0xAAFFFF00).fontSize(30).
                    fontColor(0xFFFF00FF).text(friendInfor.getPhonenum()).rotate(0).
                    position(new LatLng(friendInfor.getLatitude()-0.0016,friendInfor.getLongitude()));
            baiduMap.addOverlay(textOptionNum);

            float[] result = new float[1];
            if(MyAltitude != 0 )
                Location.distanceBetween(cenpt.latitude, cenpt.longitude, MyLatitude, MyLongitude,result);
           // double dis = DistanceUtil. getDistance(cenpt, new LatLng(MyLatitude,MyLongitude));
            OverlayOptions textOptionDis = new TextOptions().bgColor(0xAAFFFF00).fontSize(30).
                    fontColor(0xFFFF00FF).text("距离"+result[0]).rotate(0).
                    position(new LatLng(friendInfor.getLatitude()-0.0024,friendInfor.getLongitude()));
            baiduMap.addOverlay(textOptionDis);

            //画出线
            List<LatLng> points = new ArrayList<>();
            points.add(cenpt);
            points.add(MyLatLng);
            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                    .color(Color.GREEN).points(points);
            baiduMap.addOverlay(ooPolyline);
        }

    }
    public void DrawEnemies(ArrayList<EnemyInfor> enemyCollection, BaiduMap baiduMap, TextureMapView mMapView){
        if(enemyCollection != null)
        for(int i =0; i< enemyCollection.size();i++){
            EnemyInfor enemyInfor = enemyCollection.get(i);
            LatLng cenpt = new LatLng(enemyInfor.getLatitude(), enemyInfor.getLongitude());
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.enemy_marker);
            //准备 marker option 添加 marker 使用
            MarkerOptions markerOption = new MarkerOptions().icon(bitmap).position(cenpt);
            //获取添加的 marker 这样便于后续的操作
            Marker marker = (Marker) baiduMap.addOverlay(markerOption);

            if(enemyInfor.getName() != null){
                OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(30).
                        fontColor(0xFFFF00FF).text(enemyInfor.getName()).rotate(0).
                        position(new LatLng(enemyInfor.getLatitude()-0.0008, enemyInfor.getLongitude()));
                baiduMap.addOverlay(textOption);
            }
            if(enemyInfor.getPhonenum() != null){
                OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(30).
                        fontColor(0xFFFF00FF).text(enemyInfor.getPhonenum()).rotate(0).
                        position(new LatLng(enemyInfor.getLatitude()-0.0016, enemyInfor.getLongitude()));
                baiduMap.addOverlay(textOption);
            }
            //画出距离
            float[] result = new float[1];
            if(MyAltitude != 0 )
                Location.distanceBetween(cenpt.latitude, cenpt.longitude, MyLatitude, MyLongitude,result);
            //double dis = DistanceUtil. getDistance(cenpt, new LatLng(MyLatitude,MyLongitude));
            OverlayOptions textOptionDis = new TextOptions().bgColor(0xAAFFFF00).fontSize(30).
                    fontColor(0xFFFF00FF).text("距离"+result[0] +"").rotate(0).
                    position(new LatLng(enemyInfor.getLatitude()-0.0024,enemyInfor.getLongitude()));
            baiduMap.addOverlay(textOptionDis);
            //画出线
            List<LatLng> points = new ArrayList<>();
            points.add(cenpt);
            points.add(MyLatLng);
            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                    .color(0xAAFF0000).points(points);
            baiduMap.addOverlay(ooPolyline);
            //Toast.makeText(MainActivity.this, "finish",Toast.LENGTH_SHORT).show();
        }

    }

    //百度地图要实现的代码
    private void navigateTo (BDLocation location){
        MyLongitude = location.getLongitude();
        MyLatitude = location.getLatitude();//保存自身的地理位置
        MyCity = location.getCity();
        if(MyCity == null){
            GetAddressInfor getAddressInfor = new GetAddressInfor(new LatLng(MyLongitude, MyLatitude));
            getAddressInfor.getAddress(handler);
        }
        MyAltitude  = location.getAltitude();
        MyLatLng = new LatLng(MyLatitude, MyLongitude);
        System.out.println(MyLongitude+""+MyLatitude+""+MyCity+"   "+MyAltitude);
        if(isFirstLocation){
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocation = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude()); // 封装当前设备的位置信息
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);//让设备当前位置显示在地图上面
    }
    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setAddrType("all"); //定位到街道类型，只有设置为all才可以获取到city
        option.setOpenGps(true);
        option.setIsNeedAltitude(true);
        option.setIsNeedLocationDescribe(true);
        option.setNeedDeviceDirect(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if ( grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            return ;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this, "发生未知错误",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //Toast.makeText(MainActivity.this, "ok"+bdLocation.getLongitude(),Toast.LENGTH_LONG).show();
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation ||
                    bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                navigateTo(bdLocation);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        mMapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        unregisterReceiver(locationReceiver); //接触广播的注册
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //如果是点击返回，则打开主页面
            MainActivity.this.finish();
        }
        return true;
    }
}
