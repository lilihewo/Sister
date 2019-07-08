package com.lili.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lili.bean.Sister;
import com.lili.loader.PictureLoader;
import com.lili.loader.SisterApi;
import com.lili.sister.R;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button showBtn;
    private Button refreshBtn;
    private ImageView showImg;
    private TextView pageTextView;
    private TextView indexTextView;

    private ArrayList<Sister> data;
    private int curPos = 0; // 当前显示的是哪一张
    private int page = 1;	// 当前页数
    private PictureLoader loader;
    private SisterApi sisterApi;

    private SisterTask sisterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sisterApi = new SisterApi();
        loader = new PictureLoader();

        initData();
        initUI();
    }

    private void initData() {
        data = new ArrayList<>();
    }

    private void initUI() {
        showBtn = (Button) findViewById(R.id.btn_show);
        refreshBtn = (Button) findViewById(R.id.btn_refresh);
        showImg = (ImageView) findViewById(R.id.img_show);

        pageTextView = findViewById(R.id.text_view_page);
        indexTextView = findViewById(R.id.text_view_index);

        showBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);

        showNextPage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show:
                // 显示下一张图片
                showNextImage();
                break;
            case R.id.btn_refresh:
                // 显示下一页
                showNextPage();
                break;
        }
    }

    private void showNextImage() {
        if (data != null && !data.isEmpty()) {
            if (curPos > 9) {
                curPos = 0;
            }
            loader.load(showImg, data.get(curPos).getUrl());
            curPos++;
            indexTextView.setText("第" + curPos + "张");
        }
    }

    private void showNextPage() {
        sisterTask = new SisterTask();
        sisterTask.execute();
        curPos = 0;
    }

    private class SisterTask extends AsyncTask<Void, Void, ArrayList<Sister>> {

        @Override
        protected ArrayList<Sister> doInBackground(Void... params) {
            return sisterApi.fetchSister(10, page);
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            pageTextView.setText("第" + page + "页");

            data.clear();
            data.addAll(sisters);
            page++;

            showNextImage();
            curPos = 1;
            indexTextView.setText("第" + curPos + "张");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            sisterTask = null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sisterTask.cancel(true);
    }

}
