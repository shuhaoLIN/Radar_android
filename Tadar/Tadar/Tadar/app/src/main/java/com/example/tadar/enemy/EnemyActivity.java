package com.example.tadar.enemy;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.example.tadar.MainActivity;
import com.example.tadar.R;
import com.example.tadar.StaticCollection;
import com.example.tadar.friend.FriendActivity;

import java.util.ArrayList;

public class EnemyActivity extends AppCompatActivity {

    ListView enemylist; //lvw_enemies_list
    EnemiesAdapter enemiesAdapter;
    Button enemyAddButton; //btn_enemies_list_add
    Button btnFriend; //btn_enemies_list_friends
    Button btnRadar; //btn_enemies_list_radar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enemies_list);

        //获取组件
        enemylist = (ListView) findViewById(R.id.lvw_enemies_list);
        enemyAddButton = (Button)findViewById(R.id.btn_enemies_list_add);
        btnFriend = (Button) findViewById(R.id.btn_enemies_list_friends);
        btnRadar = (Button) findViewById(R.id.btn_enemies_list_radar);

        //获取数据
        if(StaticCollection.getEnemyCollection() != null && StaticCollection.getEnemyCollection().size() == 0){
            EnemiesOperater operater = new EnemiesOperater();
            StaticCollection.setEnemyCollection(operater.loadEnemies(EnemyActivity.this));
        }
        if(StaticCollection.getEnemyCollection() == null){
            StaticCollection.setEnemyCollection(new ArrayList<EnemyInfor>());
        }
        enemiesAdapter = new EnemiesAdapter(EnemyActivity.this, StaticCollection.getEnemyCollection());
        enemylist.setAdapter(enemiesAdapter);
        enemylist.setOnItemClickListener(new enemyListItemListener());

        enemyAddButton.setOnClickListener(new enemyAddButtonListener());
        btnFriend.setOnClickListener(new btnFriendListener());
        btnRadar.setOnClickListener(new btnRadarListener());
    }
    //打开页面的按钮的监听类
    class btnFriendListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(EnemyActivity.this, FriendActivity.class);
            startActivity(intent);
            finish();
        }
    }
    class btnRadarListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(EnemyActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //enemylist 的监听事件
    class enemyListItemListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(EnemyActivity.this, EnemyDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    //添加按钮的设置
    class enemyAddButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            showEnemyAddDialog();
        }
    }
    void showEnemyAddDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_enemy, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        final EditText name = (EditText)view.findViewById(R.id.txt_enemy_name);
        final EditText number = (EditText) view.findViewById(R.id.txt_enemy_number);
        Button ok = (Button)view.findViewById(R.id.btn_dialog_ok);
        Button close = (Button)view.findViewById(R.id.btn_dialog_close);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr = name.getText().toString();
                String numberStr = number.getText().toString();
                if(nameStr.length() == 0 || numberStr.length() == 0){
                    Toast.makeText(EnemyActivity.this,
                            "名字或者号码不能为空！请重新输入！",Toast.LENGTH_SHORT).show();
                }
                else {
                    EnemyInfor enemyInfor = new EnemyInfor();
                    enemyInfor.setName(nameStr);
                    enemyInfor.setPhonenum(numberStr);
                    //更新数据
                    enemiesAdapter.add(enemyInfor);
                    enemiesAdapter.notifyDataSetChanged();
                    //保存数据
                    EnemiesOperater operater = new EnemiesOperater();
                    operater.saveEnemies(EnemyActivity.this, StaticCollection.getEnemyCollection());

                    dialog.dismiss();
                }

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //如果是点击返回，则打开主页面
            Intent intent = new Intent(EnemyActivity.this, MainActivity.class);
            startActivity(intent);
            EnemyActivity.this.finish();
        }
        return true;
    }
}
