package com.example.tadar.friend;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.example.tadar.GetAddressInfor;
import com.example.tadar.MainActivity;
import com.example.tadar.R;
import com.example.tadar.StaticCollection;
import com.example.tadar.enemy.EnemyActivity;

import org.w3c.dom.Text;

import java.util.Date;

public class FriendDetailActivity extends AppCompatActivity {

    int position ;
    FriendInfor showFriend;
    String city;
    Handler handler;
    TextView friendName;//txt_friend_name
    TextView friendPhoneNum;//txt_friend_number
    TextView friendAddress;//txt_friend_long_lang 包括latitude/longitude
    TextView friendAltitude;//txt_friend_altitude
    TextView friendAccuracy;// txt_friend_accuracy
    TextView friendCity; //txt_friend_nearest_city
    TextView friendLastUpdate;//txt_friend_secs_last_update
    TextView friendNextUpdate; //txt_friend_secs_next_update

    Button btnFriendsList; //btn_friends_list
    Button btnRadar; //btn_radar
    Button btnEnemies; //btn_enemies
    Button btnDelete; // btn_delete
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("position");
        //上面获得到的position。代表着是哪一个数据
        showFriend = new FriendInfor();
        showFriend = StaticCollection.getFriendCollection().get(position); // 获得当前的数据，后面进行展示

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                //获取整数加上字符串
                city = bundle.getString("city");
            }
        };
        GetAddressInfor getAddressInfor = new GetAddressInfor(new LatLng(showFriend.getLongitude(), showFriend.getLatitude()));
        getAddressInfor.getAddress(handler);
        initAllTextView();
        initAllButton();
    }
    public void initAllTextView(){
        friendName = (TextView) findViewById(R.id.txt_friend_name);//txt_friend_name
        friendPhoneNum = (TextView) findViewById(R.id.txt_friend_number);//txt_friend_number
        friendAddress = (TextView) findViewById(R.id.txt_friend_long_lang);//txt_friend_long_lang 包括latitude/longitude
        friendAltitude = (TextView) findViewById(R.id.txt_friend_altitude);//txt_friend_altitude
        friendAccuracy = (TextView) findViewById(R.id.txt_friend_accuracy);// txt_friend_accuracy
        friendCity = (TextView) findViewById(R.id.txt_friend_nearest_city); //txt_friend_nearest_city
        friendLastUpdate = (TextView) findViewById(R.id.txt_friend_secs_last_update);
        friendNextUpdate = (TextView) findViewById(R.id.txt_friend_secs_next_update);

        friendName.setText(showFriend.getName());
        friendPhoneNum.setText(showFriend.getPhonenum());
        friendAddress.setText(showFriend.getLatitude()+"/"+showFriend.getLongitude());
        //friendAltitude
        friendAltitude.setText(showFriend.getAltitude()+"");
        friendAccuracy.setText("<5m");
        friendCity.setText(showFriend.getCity());
        if(showFriend.getUpdateDate() != null){
            friendLastUpdate.setText((new Date().getTime()- showFriend.getUpdateDate().getTime())/1000+"s");
        }
        friendNextUpdate.setText("5s");
    }
    public void initAllButton(){
        btnFriendsList = (Button) findViewById(R.id.btn_friends_list) ; //btn_friends_list
        btnRadar = (Button) findViewById(R.id.btn_radar); //btn_radar
        btnEnemies = (Button) findViewById(R.id.btn_enemies); //btn_enemies
        btnDelete = (Button) findViewById(R.id.btn_delete); // btn_delete

        btnFriendsList.setOnClickListener(new btnFriendsListListener());
        btnRadar.setOnClickListener(new btnRadarListener());
        btnEnemies.setOnClickListener(new btnEnemiesListener());
        btnDelete.setOnClickListener(new btnDeleteListener());
    }
    class btnFriendsListListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(FriendDetailActivity.this, FriendActivity.class);
            startActivity(intent);
            finish();
        }
    }
    class btnRadarListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FriendDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    class btnEnemiesListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(FriendDetailActivity.this, EnemyActivity.class);
            startActivity(intent);
            finish();
        }
    }
    class  btnDeleteListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            showDeleteDialog(position);
        }
    }
    public void showDeleteDialog(final int position){
        View view = LayoutInflater.from(FriendDetailActivity.this).inflate(R.layout.dialog_delete, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(FriendDetailActivity.this).setView(view).create();
        TextView numberText = (TextView)view.findViewById(R.id.txt_friend_number);
        Button ok = (Button)view.findViewById(R.id.btn_dialog_ok);
        Button close = (Button)view.findViewById(R.id.btn_dialog_close);

        numberText.setText(StaticCollection.getFriendCollection().get(position).getPhonenum());
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticCollection.deleteFriend(position); //删除好友
                //保存
                FriendsOperater operater = new FriendsOperater();
                operater.saveFriends(FriendDetailActivity.this, StaticCollection.getFriendCollection());
                //打开friendlist
                Intent intent = new Intent(FriendDetailActivity.this, FriendActivity.class);
                startActivity(intent);
                dialog.dismiss();
                finish();//关闭当前页面
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击返回，则返回list
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //如果是点击返回，则打开主页面
            Intent intent = new Intent(FriendDetailActivity.this, FriendActivity.class);
            startActivity(intent);
            FriendDetailActivity.this.finish();
        }
        return true;
    }
}
