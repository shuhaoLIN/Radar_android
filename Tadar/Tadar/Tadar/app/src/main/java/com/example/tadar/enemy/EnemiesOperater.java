package com.example.tadar.enemy;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hasee on 2018/12/18.
 */

public class EnemiesOperater {
    private String file = "Enemies.dat";
    public ArrayList<EnemyInfor> loadEnemies(Context context){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        //初始化流对象
        try{
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (ArrayList<EnemyInfor>) ois.readObject();
        }catch (FileNotFoundException e){}
        catch (Exception e){
            e.printStackTrace();
            //反序列化失败 --- 删除缓存
            if(e instanceof InvalidClassException){
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        }finally {
            try{
                ois.close();
            }catch (Exception e){}
            try{
                fis.close();
            }catch (Exception e){}
        }
        return null;
    }
    public boolean saveEnemies(Context context, Serializable ser){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            //fos = context.openFileOutput(file,Context.MODE_APPEND);//实现追加内容功能
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch  ( Exception e ) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
