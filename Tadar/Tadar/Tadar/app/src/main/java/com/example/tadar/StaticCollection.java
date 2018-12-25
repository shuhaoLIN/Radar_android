package com.example.tadar;

import com.example.tadar.enemy.EnemyInfor;
import com.example.tadar.friend.FriendInfor;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/12/12.
 * 这个类实现了存储两种人物的collection
 */
public class StaticCollection {
    private static ArrayList<FriendInfor> friendCollection = new ArrayList<>();
    private static ArrayList<EnemyInfor> enemyCollection = new ArrayList<>();

    public StaticCollection(){
        //先行创建后面进行使用就好了
//        friendCollection = new ArrayList<>();
//        enemyCollection = new ArrayList<>();
    }
    public static ArrayList<FriendInfor> getFriendCollection(){
        return friendCollection;
    }
    public static void setFriendCollection(ArrayList<FriendInfor> arrayList){
        friendCollection = arrayList;
    }
    public static void addFriend(FriendInfor infor){
        friendCollection.add(infor);
    }
    public static void deleteFriend(int position){
        friendCollection.remove(position);
    }
    public static void setFriend(int position, FriendInfor infor){
        friendCollection.set(position, infor);
    }


    public static ArrayList<EnemyInfor> getEnemyCollection(){
        return enemyCollection;
    }
    public static void setEnemyCollection(ArrayList<EnemyInfor> arrayList){
        enemyCollection = arrayList;
    }
    public static void addEnemy(EnemyInfor infor){
        enemyCollection.add(infor);
    }
    public static void deleteEnemy(int position){
        enemyCollection.remove(position);
    }
    public static void setEnemy(int position , EnemyInfor infor){
        enemyCollection.set(position, infor);
    }
}
