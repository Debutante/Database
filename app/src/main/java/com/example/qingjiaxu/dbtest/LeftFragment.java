package com.example.qingjiaxu.dbtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

public class LeftFragment extends Fragment {
    private static final String TAG = "LeftFragment";

//    protected WordsDBHelper mDbHelper;
//    protected ListView listView;
//    protected List<Word> list;
//    protected MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.title_frag, container, false);
//        listView = view.findViewById(R.id.list_view);
//        mDbHelper = new WordsDBHelper(getActivity());
//        list=mDbHelper.selectAll();
//        adapter=new MyAdapter(list,getActivity());
//        listView.setAdapter(adapter);
//        Log.d(TAG, "onCreateView: 这些代码都跑啦");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
