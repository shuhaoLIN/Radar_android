package com.example.tadar.enemy;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.tadar.R;
import com.example.tadar.StaticCollection;
import com.example.tadar.friend.FriendsOperater;

import java.util.ArrayList;

/**
 * Created by hasee on 2018/12/18.
 */

public class EnemiesAdapter extends BaseAdapter {
    ArrayList<EnemyInfor> enemies;
    Context context;
    EnemiesAdapter(Context context, ArrayList<EnemyInfor> enemies){
        this.context = context;
        this.enemies = enemies;// 因为这个东西给弄成静态了，所以产生了静态的效果。
    }
    public void add(EnemyInfor enemyInfor){
        System.out.println("enemy qian:"+enemies.size());
        enemies.add(enemyInfor);
        System.out.println("enemy hou:"+enemies.size());
    }
    @Override
    public int getCount() {
        return enemies.size();
    }

    @Override
    public Object getItem(int i) {
        return enemies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.enemies_list_item,null);
        TextView name_cell = (TextView) view.findViewById(R.id.name_cell);
        name_cell.setText(enemies.get(position).getName());
        Button deleteButton = (Button) view.findViewById(R.id.delete_button_cell);
        final int btnPsition = position;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(btnPsition);
            }
        });
        return view;
    }
    public void showDeleteDialog(final int position){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        TextView numberText = (TextView)view.findViewById(R.id.txt_friend_number);
        Button ok = (Button)view.findViewById(R.id.btn_dialog_ok);
        Button close = (Button)view.findViewById(R.id.btn_dialog_close);

        numberText.setText(enemies.get(position).getPhonenum());
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticCollection.deleteEnemy(position);
                //保存
                EnemiesOperater operater = new EnemiesOperater();
                //这里要设置敌人的保存操作
                operater.saveEnemies(context, StaticCollection.getFriendCollection());
                notifyDataSetInvalidated();
                dialog.dismiss();
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
}
