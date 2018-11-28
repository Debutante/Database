package com.example.qingjiaxu.dbtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);

        ListView listView = findViewById(R.id.search_list_view);
        Intent intent=getIntent();
        String searchWord = intent.getStringExtra("searchWord");

        WordsDBHelper mDbHelper = new WordsDBHelper(this);
        List<Word> list = mDbHelper.selectCertain(searchWord);

        if (list.size() > 0){
            MyAdapterMin adapter=new MyAdapterMin(list,getApplicationContext());
            listView.setAdapter(adapter);
        }
        else Toast.makeText(SearchActivity.this,"没有找到", Toast.LENGTH_LONG).show();

    }
}
