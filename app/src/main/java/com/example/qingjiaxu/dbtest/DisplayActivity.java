package com.example.qingjiaxu.dbtest;

import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class DisplayActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    protected Button button;
    protected Integer wordId;
    protected Integer collect;
    protected ImageButton readWordBtn;
    protected ImageButton readSampleBtn;
    protected WordsDBHelper mDbHelper;
    private TextToSpeech textToSpeech;
    private String name;
    private String sample;
    private static final String TAG = "DisplayActivity";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        textToSpeech.shutdown();
        mDbHelper.updateCollect(wordId, collect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mDbHelper = new WordsDBHelper(this);

        TextView text_id=findViewById(R.id.wordId);
        TextView text_name=findViewById(R.id.name);
        TextView text_meaning=findViewById(R.id.meaning);
        TextView text_sample=findViewById(R.id.sample);

        readWordBtn = findViewById(R.id.read_word_button);
        readSampleBtn = findViewById(R.id.read_sample_button);
        button=findViewById(R.id.collect);
        textToSpeech = new TextToSpeech(this, this);

        Intent intent=getIntent();

        wordId = intent.getIntExtra("wordId", -1);
        name = intent.getStringExtra("name");
        String meaning = intent.getStringExtra("meaning");
        sample = intent.getStringExtra("sample");
        collect = intent.getIntExtra("collect", -1);

        if (collect == 0) {
            button.setText("收藏单词");
            button.setBackgroundColor(Color.rgb(66, 205, 170));
        }
        else if (collect == 1) {
            button.setText("取消收藏");
            button.setBackgroundColor(Color.rgb(255, 192, 203));
        }
        text_id.setText(""+wordId);
        text_name.setText(name);
        text_meaning.setText(meaning);
        text_sample.setText(sample);
        Log.d(TAG, "*************我重启了*************");
        Log.d(TAG, "********我是collect，现在我是"+collect+"***********");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collect == 0) {
                    button.setText("取消收藏");
                    button.setBackgroundColor(Color.rgb(255, 192, 203));
                    collect = 1;
                    Toast.makeText(DisplayActivity.this, "收藏成功",Toast.LENGTH_SHORT).show();

                }

                else if (collect == 1) {
                    button.setText("收藏单词");
                    button.setBackgroundColor(Color.rgb(66, 205, 170));
                    collect = 0;
                    Toast.makeText(DisplayActivity.this, "取消收藏成功",Toast.LENGTH_SHORT).show();

                }

            }
        });

        readWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.setSpeechRate(0.6f);
                textToSpeech.speak(name, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        readSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.setSpeechRate(0.9f);
                textToSpeech.speak(sample, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //原生的android貌似不支持中文。
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, R.string.error_info, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
