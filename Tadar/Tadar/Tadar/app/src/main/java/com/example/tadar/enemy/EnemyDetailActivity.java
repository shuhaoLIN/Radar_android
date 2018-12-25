package com.example.tadar.enemy;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.example.tadar.GetAddressInfor;
import com.example.tadar.MainActivity;
import com.example.tadar.R;
import com.example.tadar.StaticCollection;
import com.example.tadar.friend.FriendActivity;
import com.example.tadar.friend.FriendDetailActivity;
import org.w3c.dom.Text;
import java.util.Date;

public class EnemyDetailActivity extends AppCompatActivity {

    int position;
    Handler handler;
    String city;
    EnemyInfor showEnemy;

    TextView enemyName; //txt_enemy_name
    TextView enemyPhoneNum; //txt_enemy_number
    TextView enemyAddress;//txt_enemy_long_lang 包括longitude/latitude
    TextView enemyAltitude; //txt_enemy_altitude
    TextView enemyAccuracy; // txt_enemy_accuracy
    TextView enemyCity;//txt_enemy_nearest_city
    TextView enemyLastUpdate;//txt_enemy_secs_last_update
    TextView enemyNextUpdate; //txt_enemy_secs_next_update

    Button btnEnemyList; //btn_enemies_list
    Button btnRadar; // btn_radar
    Button btnFriends; //btn_friends
    Button btnDelete; // btn_delete
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enemy_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("position");
        //上面获得到的position。代表着是哪一个数据
        showEnemy = new EnemyInfor();
        showEnemy = StaticCollection.getEnemyCollection().get(position);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                //获取整数加上字符串
                city = bundle.getString("city");
            }
        };
        GetAddressInfor getAddressInfor = new GetAddressInfor(new LatLng(showEnemy.getLongitude(),showEnemy.getLatitude()));
        getAddressInfor.getAddress(handler);
        initAllTextView();
        initAllButton();
    }
    void initAllTextView(){
        enemyName = (TextView) findViewById(R.id.txt_enemy_name); //txt_enemy_name
        enemyPhoneNum = (TextView) findViewById(R.id.txt_enemy_number); //txt_enemy_number
        enemyAddress = (TextView) findViewById(R.id.txt_enemy_long_lang);//txt_enemy_long_lang 包括longitude/latitude
        enemyAltitude = (TextView) findViewById(R.id.txt_enemy_altitude); //txt_enemy_altitude
        enemyAccuracy = (TextView) findViewById(R.id.txt_enemy_accuracy); // txt_enemy_accuracy
        enemyCity = (TextView) findViewById(R.id.txt_enemy_nearest_city);//txt_enemy_nearest_city
        enemyLastUpdate = (TextView) findViewById(R.id.txt_enemy_secs_last_update);
        enemyNextUpdate = (TextView) findViewById(R.id.txt_enemy_secs_next_update);


        enemyName.setText(showEnemy.getName());
        enemyPhoneNum.setText(showEnemy.getPhonenum());
        enemyAddress.setText(showEnemy.getLongitude()+"/"+showEnemy.getLatitude());
        //高度后面再试试
        enemyAltitude.setText(showEnemy.getAltitude()+"");
        enemyAccuracy.setText("<5m");
        enemyCity.setText(showEnemy.getCity());
        if(showEnemy.getUpdateDate() != null){
            enemyLastUpdate.setText((new Date().getTime() - showEnemy.getUpdateDate().getTime())/1000+"s");
        }

        enemyNextUpdate.setText("5s");
    }
    void initAllButton(){
        btnEnemyList = (Button)findViewById(R.id.btn_enemies_list);   //btn_enemies_list
        btnRadar = (Button)findViewById(R.id.btn_radar); // btn_radar
        btnFriends = (Button)findViewById(R.id.btn_friends); //btn_friends
        btnDelete = (Button)findViewById(R.id.btn_delete); // btn_delete

        btnEnemyList.setOnClickListener(new btnEnemyListListener());
        btnRadar.setOnClickListener(new btnRadarListener());
        btnFriends.setOnClickListener(new btnFriendsListener());
        btnDelete.setOnClickListener(new btnDeleteListener());
    }
    class btnEnemyListListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(EnemyDetailActivity.this, EnemyActivity.class);
            startActivity(intent);
            finish();
        }
    }
    class btnRadarListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(EnemyDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    class btnFriendsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(EnemyDetailActivity.this, FriendActivity.class);
            startActivity(intent);
            finish();
        }
    }
    class btnDeleteListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            showDeleteDialog(position);
        }
    }
    public void showDeleteDialog(final int position){
        View view = LayoutInflater.from(EnemyDetailActivity.this).inflate(R.layout.dialog_delete, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(EnemyDetailActivity.this).setView(view).create();

        TextView numberText = (TextView)view.findViewById(R.id.txt_friend_number);
        Button ok = (Button)view.findViewById(R.id.btn_dialog_ok);
        Button close = (Button)view.findViewById(R.id.btn_dialog_close);

        numberText.setText(StaticCollection.getEnemyCollection().get(position).getPhonenum());
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticCollection.deleteEnemy(position);
                //保存
                EnemiesOperater operater = new EnemiesOperater();
                //这里要设置敌人的保存操作
                operater.saveEnemies(EnemyDetailActivity.this, StaticCollection.getEnemyCollection());
                //打开enemy界面
                Intent intent = new Intent(EnemyDetailActivity.this, EnemyActivity.class);
                startActivity(intent);
                dialog.dismiss();
                finish();
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
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //如果是点击返回，则打开主页面
            Intent intent = new Intent(EnemyDetailActivity.this, EnemyActivity.class);
            startActivity(intent);
            EnemyDetailActivity.this.finish();
        }
        return true;
    }
}
