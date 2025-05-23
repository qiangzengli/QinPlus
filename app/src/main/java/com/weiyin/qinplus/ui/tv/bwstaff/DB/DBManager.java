package com.weiyin.qinplus.ui.tv.bwstaff.DB;
/**
 * Created by Administrator on 2016/5/7 0007.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.os.Environment;
import android.util.Log;


import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.ui.tv.bwstaff.ScoreDataCoordinator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBManager {

    private int BUFFER_SIZE = 10000;

    public String db_file_name; //保存的数据库文件名

    public String PACKAGE_NAME = "com.weiyin.letvpiano";//工程包名

    public String DB_PATH = "/data"

            + Environment.getDataDirectory().getAbsolutePath() + "/"

            + PACKAGE_NAME;  //在手机里存放数据库的位置


    private SQLiteDatabase database;
    private Context context;
    private ScoreDataCoordinator _delegate;


    public void closeDatabase() {
        if (database != null) {
            this.database.close();
            database = null;
        }
        if (_delegate != null)
            _delegate = null;

    }

    public DBManager(Context context) {
        this.context = context;
    }

    public DBManager(Context context, String db_file_name) {
        String name = db_file_name;
        this.db_file_name = name;
        this.context = context;
        _delegate = ScoreDataCoordinator.sharedCoordinator();
    }

    public String getDb_file_name() {
        return db_file_name;
    }

    public void setDb_file_name(String db_file_name) {
        this.db_file_name = db_file_name;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public void openDatabase(String db_name) {
        File dbfile = new File(db_name);
        this.database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
    }

    public void openDatabase(int id) {
        System.out.println(DB_PATH + "/" + db_file_name);
        this.database = this.openDatabase(DB_PATH + "/" + db_file_name, id);
    }

    public SQLiteDatabase openDataBase(String path) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
        return db;
    }

    private SQLiteDatabase openDatabase(String dbfile, int id) {

        try {
            if (!(new File(dbfile).exists())) {
                //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(
                        id); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            return db;

        } catch (FileNotFoundException e) {
            LogUtil.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtil.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }

    public String getRecordsWhere(String name) {

        String feedback_status = "0";

        Cursor c = null;
        try {
            c = database.rawQuery("select * from XMLMessage where name =?", new String[]{name});
            if (c.moveToFirst()) {
                feedback_status = c.getString(6);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }
        return feedback_status;

    }

    public String getRecordsWhere(String name, int ins_no, int mea_no, int staff_no, int voice_no, int slot_no, String describe) {

        String feedback_status = "0";

        String this_ins_no = String.valueOf(ins_no);
        String this_mea_no = String.valueOf(mea_no);
        String this_staff_no = String.valueOf(staff_no);
        String this_voice_no = String.valueOf(voice_no);
        String this_slot_no = String.valueOf(slot_no);
        Cursor c = null;
        try {
            c = database.rawQuery("select * from XMLMessage where name =? and instrument_num =? and measure_num =? and staff_num =? and voice_num =? and slot_num =? and describe =?", new String[]{name, this_ins_no, this_mea_no, this_staff_no, this_voice_no, this_slot_no, describe});
            if (c.moveToFirst()) {
                feedback_status = c.getString(6);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }
        return feedback_status;

    }

    public void getNoteOnMessage() {
        if (_delegate == null) {
            return;
        }
        Cursor c = null;
        int value = 0;
        try {
            String query = "SELECT * from NoteOnMessage where name = ?";
            c = database.rawQuery(query, new String[]{"slot-block-amount"});
            if (c.moveToFirst()) {
                // 获取表中的第7 列，value 值
                value = c.getInt(7);
                _delegate.slotBlockAmount(value);
                // 这里代码貌似存在问题
                for (int i = 1; i <= value; i++) {
                    String query_select = "select * from NoteOnMessage where id_num = ?";
                    String this_value = String.valueOf(i);
                    c = database.rawQuery(query_select, new String[]{this_value});
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        int id_num = c.getInt(0);
                        // 如果 id_num ==0  ，再次设置 总slot 数量
                        if (id_num == 0) {
                            _delegate.slotBlockAmount(c.getInt(7));
                        } else {
                            // 构建当前音符的信息
                            //路程（tick）
                            int tick = c.getInt(1);
                            //小节号
                            int measuer = c.getInt(3);
                            // 没用到，先忽略含义
                            int staff = c.getInt(4);
                            //音乐中表示声部
                            int voice = c.getInt(5);
                            // 键值号
                            int noteNum = c.getInt(6);
                            // 音高
                            int pitch = c.getInt(7);
                            // 音符的X，通常是时间轴的位置，越靠右越晚
                            int x = c.getInt(8);
                            // 音符的Y，通常是音高的位置，越靠上，调越高
                            int y = c.getInt(9);
                            // 左右手
                            String handStr = c.getString(10);
                            int hand = 0;
                            if (handStr.equalsIgnoreCase("R")) {
                                //右手是1 左手为0
                                hand = 1;
                            }
                            if (handStr.equalsIgnoreCase("L")) {
                                hand = 0;
                            }

                            _delegate.noteIdNum(id_num, tick, measuer, staff, voice, noteNum, pitch, x, y, hand);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }
    }
}