package com.example.qingjiaxu.dbtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.ETC1;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected WordsDBHelper mDbHelper;
    protected ListView listView;
    protected List<Word> list;
    protected MyAdapter adapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.list_view);
        registerForContextMenu(listView);

        mDbHelper = new WordsDBHelper(this);
//        mDbHelper.getWritableDatabase();
//        mDbHelper.insertInto("apple","apple","this apple is very nice");
//        mDbHelper.insertInto("apple1","apple1","this apple is very nice!");
//        mDbHelper.insertInto("orange","orange","this orange is very nice!");
        list=mDbHelper.selectAll();
        adapter=new MyAdapter(list,getApplicationContext());
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                SearchDialog();
                break;
            case R.id.action_insert:
                InsertDialog();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView textId = null;
        TextView textWord = null;
        TextView textMeaning = null;
        TextView textSample = null;
        AdapterView.AdapterContextMenuInfo info = null;
        View itemView = null;

        switch (item.getItemId()){
            case R.id.action_delete:
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView=info.targetView;
                textId=itemView.findViewById(R.id.wordId);
                Log.i(TAG,"*********");
                Log.i(TAG, textId.getText().toString());
                Log.i(TAG,"*********");
                if(textId!=null){
                    Integer wordId = Integer.parseInt(textId.getText().toString());
                    DeleteDialog(wordId);
                }

                break;
            case R.id.action_update:
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView=info.targetView;
                textId=itemView.findViewById(R.id.wordId);
                textWord=itemView.findViewById(R.id.name);
                textMeaning=itemView.findViewById(R.id.meaning);
                textSample=itemView.findViewById(R.id.sample);
                if(textId!=null) {
                    Integer wordId = Integer.parseInt(textId.getText().toString());
                    String strWord = textWord.getText().toString();
                    String strMeaning = textMeaning.getText().toString();
                    String strSample = textSample.getText().toString();
                    UpdateDialog(wordId, strWord, strMeaning, strSample);
                }
                break;
        }
        return true;
    }

    private void InsertDialog(){
        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.insert, null);
        new AlertDialog.Builder(this)
                .setTitle("新增单词")
                .setView(linearLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strWord=((EditText)linearLayout.findViewById(R.id.txtWord)).getText().toString();
                String strMeaning=((EditText)linearLayout.findViewById(R.id.txtMeaning)).getText().toString();
                String strSample=((EditText)linearLayout.findViewById(R.id.txtSample)).getText().toString();

                mDbHelper.insertInto(strWord, strMeaning, strSample);

                list=mDbHelper.selectAll();
                adapter=new MyAdapter(list,getApplicationContext());
                listView.setAdapter(adapter);

            }
        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void SearchDialog() {
        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.search, null);
        new AlertDialog.Builder(this)
                .setTitle("查找单词")
                .setView(linearLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String txtSearchWord=((EditText)linearLayout.findViewById(R.id.txtSearchWord)).getText().toString();

                        Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                        intent.putExtra("searchWord", txtSearchWord);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void DeleteDialog(final Integer wordId){
        new AlertDialog.Builder(this)
                .setTitle("删除单词")
                .setMessage("是否真的删除单词？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mDbHelper.deleteInfo(wordId);

                        list=mDbHelper.selectAll();
                        adapter=new MyAdapter(list,getApplicationContext());
                        listView.setAdapter(adapter);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }


    private void UpdateDialog(final Integer wordId, final String strWord, final String strMeaning, final String strSample){
        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.insert, null);
        ((EditText)linearLayout.findViewById(R.id.txtWord)).setText(strWord);
        ((EditText)linearLayout.findViewById(R.id.txtMeaning)).setText(strMeaning);
        ((EditText)linearLayout.findViewById(R.id.txtSample)).setText(strSample);
        new AlertDialog.Builder(this)
                .setTitle("修改单词")
                .setView(linearLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strNewWord = ((EditText)linearLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strNewMeaning = ((EditText)linearLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strNewSample = ((EditText)linearLayout.findViewById(R.id.txtSample)).getText().toString();

                        mDbHelper.updateInfo(wordId, strNewWord, strNewMeaning, strNewSample);
                        list=mDbHelper.selectAll();
                        adapter=new MyAdapter(list,getApplicationContext());
                        listView.setAdapter(adapter);
                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mDbHelper.close();
    }


}
