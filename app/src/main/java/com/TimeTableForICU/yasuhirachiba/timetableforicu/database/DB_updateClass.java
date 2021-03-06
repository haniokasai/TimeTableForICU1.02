package com.TimeTableForICU.yasuhirachiba.timetableforicu.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by YasuhiraChiba on 2017/02/01.
 */
public class DB_updateClass {


    static public boolean insert2017syllabus(Context context){
        try {
            UpdateOpenHelper helper = new UpdateOpenHelper(context, "syllabus2019", "syllabus2019.db");
            SQLiteDatabase dbSub = helper.getReadableDatabase();

            openHelper mainHelper = new openHelper(context);
            SQLiteDatabase dbMain = mainHelper.getReadableDatabase();


            System.out.println("open db  success");


//TODO 試しにやってみる。
            dbMain.execSQL("DROP TABLE IF EXISTS 'Spring2019'");
            dbMain.execSQL("DROP TABLE IF EXISTS 'Autumn2019'");
            dbMain.execSQL("DROP TABLE IF EXISTS 'Winter2019'");

            String subPath = context.getDatabasePath("syllabus2019").toString();

            dbMain.execSQL("ATTACH DATABASE '" + subPath + "' AS tempDb");

            dbMain.beginTransaction();


            dbMain.execSQL("CREATE TABLE " + "Spring2019" + " AS SELECT * FROM tempDb." + "Spring2019");
            dbMain.execSQL("CREATE TABLE " + "Autumn2019" + " AS SELECT * FROM tempDb." + "Autumn2019");
            dbMain.execSQL("CREATE TABLE " + "Winter2019" + " AS SELECT * FROM tempDb." + "Winter2019");



            dbMain.setTransactionSuccessful();
            dbMain.endTransaction();



            dbMain.close();
            dbSub.close();



            System.out.println("update syllabus  success");
            return true;
        }catch (Exception e){
            System.out.print("update syllabus not success");
            return false;
        }



    }





    static class UpdateOpenHelper extends SQLiteOpenHelper{

      //  private static final String DB_FILE_NAME = "DB_for_timetable_12.db";
       // private static final String DB_NAME = "db5";
        private static final int DB_VERSION = 1;

        private final Context context;
        private final File dbPath;
        private boolean createDatabase = false;

        private final String dbFileName;

        UpdateOpenHelper(Context context, String dbName, String dbFileName) {
            super(context, dbName, null, DB_VERSION);
            this.context = context;
            this.dbPath = context.getDatabasePath(dbName);
            this.dbFileName = dbFileName;
        }

        @Override
        public synchronized SQLiteDatabase getReadableDatabase() {
            SQLiteDatabase database = super.getReadableDatabase();
            if (createDatabase) {
                try {
                    database = copyDatabase(database);
                } catch (IOException ignored) {
                }
            }
            return database;
        }

        @Override
        public synchronized SQLiteDatabase getWritableDatabase() {
            SQLiteDatabase database = super.getWritableDatabase();
            if (createDatabase) {
                try {
                    database = copyDatabase(database);
                } catch (IOException ignored) {
                }
            }
            return database;
        }

        private SQLiteDatabase copyDatabase(SQLiteDatabase database) throws IOException {
            // dbがひらきっぱなしなので、書き換えできるように閉じる
            database.close();

            // コピー！
            InputStream input = context.getAssets().open(dbFileName);
            OutputStream output = new FileOutputStream(this.dbPath);
            copy(input, output);

            createDatabase = false;
            // dbを閉じたので、また開く
            return super.getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            super.onOpen(db);
            // getWritableDatabase()したときに呼ばれてくるので、
            // 初期化する必要があることを保存する
            this.createDatabase = true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        // CopyUtilsからのコピペ
        private void copy(InputStream input, OutputStream output) throws IOException {
            byte[] buffer = new byte[1024 * 4];
            int count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
        }
    }
}
