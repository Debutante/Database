package com.example.qingjiaxu.dbtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<Word> list;
    private Context context;

    public MyAdapter(List<Word> list, Context context){
        this.list=list;
        this.context=context;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,null,false);
        TextView text_name=view.findViewById(R.id.name);
        TextView text_meaning=view.findViewById(R.id.meaning);
        TextView text_sample=view.findViewById(R.id.sample);
        TextView text_id=view.findViewById(R.id.wordId);
        TextView text_collect=view.findViewById(R.id.collect);

        text_id.setText(list.get(position).getWordId().toString());
        text_name.setText(list.get(position).getName());
        text_meaning.setText(list.get(position).getMeaning());
        text_sample.setText(list.get(position).getSample());
        text_collect.setText(list.get(position).getCollect().toString());

        return view;
    }
}
