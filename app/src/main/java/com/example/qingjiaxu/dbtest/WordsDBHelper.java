package com.example.qingjiaxu.dbtest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class WordsDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "wordsdb.db";
    private final static int DATABASE_VERSION = 1;
    // 建表SQL
    private final static String SQL_CREATE_DATABASE = "create table words (id integer primary key autoincrement,word text,meaning text,sample text)";

    //删表SQL 
    private final static String SQL_DELETE_DATABASE = "DROP TABLE IF EXISTS words";

    public WordsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_DATABASE);
        onCreate(sqLiteDatabase);
    }
    public void insertInto(String name,String meaning,String sample){
        String  sql="insert into words ( word, meaning,sample) values (?,?,?)";
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(sql,new String[]{name,meaning,sample});
        System.out.println(name);
    }

    public List<Word> selectAll(){
        String sql="select * from words";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql,null);
        List<Word> list=new ArrayList<>();
        while(cursor.moveToNext()){
            Integer wordId=cursor.getInt(cursor.getColumnIndex("id"));
            String name=cursor.getString(cursor.getColumnIndex("word"));
            String mean=cursor.getString(cursor.getColumnIndex("meaning"));
            String sample=cursor.getString(cursor.getColumnIndex("sample"));
            Word w=new Word();
            w.setWordId(wordId);
            w.setMeaning(mean);
            w.setName(name);
            w.setSample(sample);
            list.add(w);

        }
        cursor.close();
        return list;
    }

    public List<Word> selectCertain(String strWordSearch){
        String sql="select * from words where word like ? order by word desc";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(sql,new String[]{"%"+strWordSearch+"%"});
        List<Word> list=new ArrayList<>();
        while(cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex("word"));
            String mean=cursor.getString(cursor.getColumnIndex("meaning"));
            String sample=cursor.getString(cursor.getColumnIndex("sample"));
            Word w=new Word();
            w.setMeaning(mean);
            w.setName(name);
            w.setSample(sample);
            list.add(w);

        }
        cursor.close();
        return list;
    }

    public void deleteInfo(Integer wordId){
        String sql="delete from words where id=" + wordId;
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(sql);

    }

    public void updateInfo(Integer wordId, String name,String meaning,String sample){
        String sql="update words set word=?,meaning=?,sample=? where id=" + wordId;
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(sql, new String[]{name, meaning, sample});
    }

}

