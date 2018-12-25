package com.example.tadar.friend;



import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.tadar.R;
import com.example.tadar.StaticCollection;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by lenovo on 2018/12/12.
 */
public class FriendsAdapter extends BaseAdapter {
    ArrayList<FriendInfor> friends;
    Context context;

    FriendsAdapter(Context context, ArrayList<FriendInfor> friends){
        this.context = context;
        this.friends = friends; // 因为这个东西给弄成静态了，所以产生了静态的效果。
    }

    public void add(FriendInfor friendInfor){
        System.out.println("qian"+friends.size());
        friends.add(friendInfor);
        System.out.println("hou"+friends.size());
    }


    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.friends_list_item,null);
        TextView name_cell = (TextView) view.findViewById(R.id.name_cell);
        name_cell.setText(friends.get(position).getName());
        Button deleteBtn = (Button) view.findViewById(R.id.delete_button_cell);
        final int btnPsition = position;
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        numberText.setText(friends.get(position).getPhonenum());
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticCollection.deleteFriend(position);
                //保存
                FriendsOperater operater = new FriendsOperater();
                operater.saveFriends(context, StaticCollection.getFriendCollection());
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
