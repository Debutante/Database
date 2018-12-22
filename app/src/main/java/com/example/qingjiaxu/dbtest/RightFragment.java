package com.example.qingjiaxu.dbtest;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class RightFragment extends Fragment implements TextToSpeech.OnInitListener{
    protected Integer wordId;
    protected Button button;
    protected TextView text_id;
    protected Integer collect;
    protected WordsDBHelper mDbHelper;
    protected ImageButton readWordBtn;
    protected ImageButton readSampleBtn;
    private TextToSpeech textToSpeech;
    private String name;
    private String sample;

    public RightFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_display, container, false);
        mDbHelper = new WordsDBHelper(getActivity());
        button = view.findViewById(R.id.collect);

        if (getArguments() != null) {
            text_id=view.findViewById(R.id.wordId);
            TextView text_name=view.findViewById(R.id.name);
            TextView text_meaning=view.findViewById(R.id.meaning);
            TextView text_sample=view.findViewById(R.id.sample);


            wordId = getArguments().getInt("wordId");
            name = getArguments().getString("name");
            String meaning = getArguments().getString("meaning");
            sample = getArguments().getString("sample");
            collect = getArguments().getInt("collect");

            text_id.setText("" + wordId);
            text_name.setText(name);
            text_meaning.setText(meaning);
            text_sample.setText(sample);
            if (collect == 0) {
                button.setText("收藏单词");
                button.setBackgroundColor(Color.rgb(66, 205, 170));
            }
            else if (collect == 1) {
                button.setText("取消收藏");
                button.setBackgroundColor(Color.rgb(255, 192, 203));
            }

            Log.d(TAG, "onCreateView: "+wordId);
            Log.d(TAG, "onCreateView: "+name);
            Log.d(TAG, "onCreateView: "+meaning);
            Log.d(TAG, "onCreateView: "+sample);
            Log.d(TAG, "onCreateView: "+collect);
        }

        button = view.findViewById(R.id.collect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (collect == 0) {
                    button.setText("取消收藏");
                    button.setBackgroundColor(Color.rgb(255, 192, 203));
                    collect = 1;
                    mDbHelper.updateCollect(wordId, collect);
                    Toast.makeText(getActivity(), "收藏成功",Toast.LENGTH_SHORT).show();
                }

                else if (collect == 1) {
                    button.setText("收藏单词");
                    button.setBackgroundColor(Color.rgb(66, 205, 170));
                    collect = 0;
                    mDbHelper.updateCollect(wordId, collect);
                    Toast.makeText(getActivity(), "取消收藏成功",Toast.LENGTH_SHORT).show();

                }

            }
        });

        textToSpeech = new TextToSpeech(getContext(), this);
        readWordBtn = view.findViewById(R.id.read_word_button);
        readWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.setSpeechRate(0.6f);
                textToSpeech.speak(name, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        readSampleBtn = view.findViewById(R.id.read_sample_button);
        readSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.setSpeechRate(0.9f);
                textToSpeech.speak(sample, TextToSpeech.QUEUE_FLUSH, null);
            }
        });


        return view;
    }

    public static RightFragment getInstance(Word word){
        RightFragment rightFragment = new RightFragment();
        Bundle bundle = new Bundle();
        //将需要传递的字符串以键值对的形式传入bundle


        bundle.putInt("wordId",word.getWordId());
        bundle.putString("name",word.getName());
        bundle.putString("meaning",word.getMeaning());
        bundle.putString("sample",word.getSample());
        bundle.putInt("collect",word.getCollect());

        rightFragment.setArguments(bundle);

        return rightFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //原生的android貌似不支持中文。
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getContext(), R.string.error_info, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
