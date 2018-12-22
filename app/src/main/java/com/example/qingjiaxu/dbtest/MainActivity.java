package com.example.qingjiaxu.dbtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.ETC1;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    protected WordsDBHelper mDbHelper;
    protected ListView listView;
    protected List<Word> list;
    protected MyAdapter adapter;
    protected FragmentTransaction fragmentTransaction;
    protected FloatingActionButton fab;
    private int errorCode;
    private String translationText;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        fab = findViewById(R.id.favorite);
        registerForContextMenu(listView);

        Log.d(TAG, "onCreate: ****我是主函数，现在我跑了一趟****");

        mDbHelper = new WordsDBHelper(this);
//        mDbHelper.getWritableDatabase();
//        mDbHelper.insertInto("apple","apple","this apple is very nice");
//        mDbHelper.insertInto("apple1","apple1","this apple is very nice!");
//        mDbHelper.insertInto("orange","orange","this orange is very nice!");
        list=mDbHelper.selectAll();
        adapter=new MyAdapter(list,getApplicationContext());
        listView.setAdapter(adapter);

        // 判断横屏
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 添加框架
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            RightFragment rightFragment = new RightFragment();
            fragmentTransaction.add(R.id.right_layout, rightFragment);
            fragmentTransaction.commit();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word w = list.get(position);
//                Toast.makeText(MainActivity.this, w.getName(), Toast.LENGTH_SHORT).show();
                // 判断横屏竖屏
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {


//                    Word word = new Word(w.getWordId(), w.getName(), w.getMeaning(), w.getSample());
                    replaceFragment(w);

                } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Intent intent = new Intent(MainActivity.this, DisplayActivity.class);

                    intent.putExtra("wordId", w.getWordId());
                    intent.putExtra("name", w.getName());
                    intent.putExtra("meaning", w.getMeaning());

                    intent.putExtra("sample", w.getSample());
                    intent.putExtra("collect", w.getCollect());
                    Log.d(TAG, "onItemClick: "+w.getName());

                    startActivity(intent);
                }

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Intent intent = new Intent(MainActivity.this, CollectActivity.class);
                    startActivity(intent);
                }
                else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Intent intent = new Intent(MainActivity.this, CollectActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 我又重新启动啦");
        list=mDbHelper.selectAll();
        adapter=new MyAdapter(list,getApplicationContext());
        listView.setAdapter(adapter);
    }

    public void replaceFragment(Word word){
        fragmentTransaction= getSupportFragmentManager().beginTransaction();
        //通过调用RightFragment中的getInstance方法传修改文本
        RightFragment rightFragment = RightFragment.getInstance(word);
        //此时使用add方法会造成右侧fragment中文本重叠（未设置BackGround时）
        fragmentTransaction.replace(R.id.right_layout, rightFragment);
        fragmentTransaction.commit();
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
            case R.id.action_online:
                OnlineDialog();;
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

    private void OnlineDialog() {
        //final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.insert, null);
        final View outerView = LayoutInflater.from(this).inflate(R.layout.search, null);
        new AlertDialog.Builder(this)
                .setView(outerView)
                .setTitle("在线查词")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String strWord=((EditText)outerView.findViewById(R.id.txtSearchWord)).getText().toString();
                        if (strWord.isEmpty())
                            Toast.makeText(MainActivity.this, "请先输入要查询的单词", Toast.LENGTH_SHORT).show();
                        else requestTranslation(strWord);

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

    private void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    //http://fanyi.youdao.com/translate?&doctype=json&type=EN2TOZH_CN&i=lean
    //{"type":"EN2ZH_CN","errorCode":0,"elapsedTime":0,"translateResult":[[{"src":"lean","tgt":"精益"}]]}
    private int parseJSONWithJsonObject(String jsonData) {
        try {
            JSONObject jsonObject1 = new JSONObject(jsonData);
            errorCode = jsonObject1.getInt("errorCode");
            if (errorCode == 0) {
                JSONArray jsonArray1 = jsonObject1.getJSONArray("translateResult");
                JSONArray jsonArray2 = jsonArray1.getJSONArray(0);
                JSONObject jsonObject2 = jsonArray2.getJSONObject(0);
                translationText = jsonObject2.getString("tgt");
                Log.e(TAG, "parseJSONWithJsonObject: "+translationText);
                //Toast.makeText(this, translationText, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            //Toast.makeText(this, "获取翻译结果失败", Toast.LENGTH_SHORT).show();
        }
        return errorCode;
    }

    public void requestTranslation(final String wordContent) {
        String exchangeUrl = "http://fanyi.youdao.com/translate?&doctype=json&type=EN2TOZH_CN&i=" + wordContent;
        sendOkHttpRequest(exchangeUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取翻译结果失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final int result = parseJSONWithJsonObject(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == 0) {
                            Toast.makeText(MainActivity.this, translationText, Toast.LENGTH_LONG).show();
                            mDbHelper.insertInto(wordContent, translationText, "Hey,"+wordContent+".");

                            list=mDbHelper.selectAll();
                            adapter=new MyAdapter(list,getApplicationContext());
                            listView.setAdapter(adapter);
                        }
                        else Toast.makeText(MainActivity.this, "获取翻译结果失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
