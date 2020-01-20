package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.net.TCPServer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements Presentation {
    @BindView(R.id.button)
    Button mButton;
    @BindView(R.id.list_view)
    RecyclerView mListView;

    Map<Integer, String> statuses = new HashMap<>();

    @BindView(R.id.textView)
    TextView mTextView;
    TCPServer mServer;

    boolean buttonToggler = false;

    private Adapter mAdapter;

    private List<Data> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mServer = new TCPServer(9875, this);
        mAdapter = new Adapter(new ArrayList<Data>());
        mAdapter.setHasStableIds(true);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);
    }

    @OnClick(R.id.button)
    public void onClick(View v) {
        if (!buttonToggler){
            mServer.start();
        } else {
            try {
                mServer.stopServer();

            } catch (Exception e ){
                e.printStackTrace();
            }
        }
        buttonToggler = !buttonToggler;

        //mButton.setEnabled();
    }

    @Override
    public void addText(@NonNull final String text) {
//        mTextView.addText(text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mList.add(0, new Data(text));
                mAdapter.update(mList);
            }
        });

    }
    @Override
    public void clearPingText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("");
            }
        });
    }

    @Override
    public void addPingText(final String text, int index) {
        String el = statuses.get(index);
        statuses.put(index, text);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String tmp = "";
                for (Map.Entry<Integer, String> s: statuses.entrySet()) {
                    tmp += s.getValue() + "\n";
                }
               mTextView.setText(/*mTextView.getText() + "\n" +*/ tmp);
            }
        });
    }

    static class Adapter extends RecyclerView.Adapter<ViewHolder> {
        List<Data> mList;

        public Adapter(List<Data> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ViewHolder(inflater.inflate(R.layout.viewholder, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public long getItemId(int position) {
            return mList.get(position).getId();
        }

        public void update(@NonNull List<Data> data) {

            mList.clear();
            mList.addAll(data);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text)
        TextView mTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull Data data) {
            mTextView.setText(data.getName());
        }
    }

    static class Data {
        static int id = 0;
        private String mName;
        private int mId;

        public int getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public Data(@NonNull String mName) {
            this.mName = mName;
            mId = ++id;
        }
    }


}


