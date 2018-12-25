package com.example.tadar.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.example.tadar.MainActivity;
import com.example.tadar.R;
import com.example.tadar.StaticCollection;
import com.example.tadar.enemy.EnemyActivity;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by lenovo on 2018/12/11.
 */
public class FriendActivity extends AppCompatActivity {

    ListView friendList;
//    StaticCollection collection;
    FriendsAdapter friendsAdapter;
    Button friendAddButton;
    Button btn_tadar;
    Button btn_enemies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);
        //获取组件信息
        initBtnTadar();
        initBtnEnemies();
        friendAddButton = (Button) findViewById(R.id.btn_friends_list_add);// 获取到button
        friendList = (ListView)findViewById(R.id.lvw_friends_list);

        //获取到数据
        if(StaticCollection.getFriendCollection() != null && StaticCollection.getFriendCollection().size() == 0){
            FriendsOperater operater = new FriendsOperater();
            StaticCollection.setFriendCollection(operater.loadFriends(this));
        }
        if(StaticCollection.getFriendCollection() == null){
            StaticCollection.setFriendCollection(new ArrayList<FriendInfor>());
        }
        friendsAdapter = new FriendsAdapter(FriendActivity.this,StaticCollection.getFriendCollection());
        friendList.setAdapter(friendsAdapter);

        //添加相应的监听事件
        friendAddButton.setOnClickListener(new friendAddButtonListener());
        friendList.setOnItemClickListener(new friendListItemListener());

    }

    //返回敌人界面
    public void initBtnEnemies(){
        btn_enemies = (Button) findViewById(R.id.btn_friends_list_enemies);
       // 需要到达敌人界面
        btn_enemies.setOnClickListener(new btnEnemiesListener());
    }
    class btnEnemiesListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(FriendActivity.this, EnemyActivity.class);
            startActivity(intent);
            finish();
        }
    }
    //返回雷达界面按钮
    public void initBtnTadar(){
        btn_tadar = (Button) findViewById(R.id.btn_friends_list_radar);
        btn_tadar.setOnClickListener(new btnTadarListener());
    }
    class btnTadarListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FriendActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    //添加按钮实现
    class friendAddButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            showAddDialog();
        }
    }
    public void showAddDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        final EditText name = (EditText)view.findViewById(R.id.txt_friend_name);
        final EditText number = (EditText) view.findViewById(R.id.txt_friend_number);
        Button ok = (Button)view.findViewById(R.id.btn_dialog_ok);
        Button close = (Button)view.findViewById(R.id.btn_dialog_close);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = name.getText().toString();
                String numberStr = number.getText().toString();
                if(nameStr.length() == 0 || numberStr.length() == 0){
                    Toast.makeText(FriendActivity.this,
                            "名字以及电话号码不能为空！请重新输入！",Toast.LENGTH_SHORT).show();
                }
                else{
                    FriendInfor newF = new FriendInfor();
                    newF.setName(nameStr);
                    newF.setPhonenum(numberStr);

                    //StaticCollection.addFriend(newF);
                    friendsAdapter.add(newF);
                    friendsAdapter.notifyDataSetChanged();
                    //保存
                    FriendsOperater operater = new FriendsOperater();
                    operater.saveFriends(FriendActivity.this,StaticCollection.getFriendCollection());
                    dialog.dismiss();
                }
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
    //list点击事件
    class friendListItemListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent  = new Intent(FriendActivity.this, FriendDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //如果是点击返回，则打开主页面
            Intent intent = new Intent(FriendActivity.this, MainActivity.class);
            startActivity(intent);
            FriendActivity.this.finish();
        }
        return true;
    }

}
