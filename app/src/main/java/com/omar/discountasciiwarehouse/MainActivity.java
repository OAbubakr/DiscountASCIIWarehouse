package com.omar.discountasciiwarehouse;

import android.net.http.HttpResponseCache;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    List<Product> products = new ArrayList<>();
    EndlessScrollListener endlessScrollListener;
    GridAdapter adapter;
    TextView params;
    CheckBox checkBox;
    boolean valid = true;
    Handler handler;
    int skip = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridview);
        params = (TextView) findViewById(R.id.params);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endlessScrollListener.isLoading())
                    valid = false;
                skip = 0;
                endlessScrollListener.setPageNumber(0);
                adapter.setFinised(false);

                products.clear();
                adapter.notifyDataSetChanged();

            }
        });
        params.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (endlessScrollListener.isLoading())
                    valid = false;
                skip = 0;
                endlessScrollListener.setPageNumber(0);
                adapter.setFinised(false);

                products.clear();
                adapter.notifyDataSetChanged();


            }
        });


        endlessScrollListener = new EndlessScrollListener(new EndlessScrollListener.RefreshList() {

            @Override
            public void onRefresh(int pageNumber) {

                loadNextDataFromApi(pageNumber);


            }
        });
        gridView.setOnScrollListener(endlessScrollListener);
        adapter = new GridAdapter(getApplicationContext(), products);
        gridView.setAdapter(adapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                adapter.notifyDataSetChanged();

                endlessScrollListener.setLoading(false);

            }
        };


        try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir(), "http");
            long httpCacheSize = 1 * 1024 * 1024; //
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }





    }

    public void loadNextDataFromApi(int offset) {
        skip = offset;
        Thread th = new Thread(new MyRunnable());
        th.start();

    }

    class MyRunnable implements Runnable {
        @Override
        public void run() {
            String result = null;
            try {
                int check = (checkBox.isChecked()) ? 1 : 0;

                URL url = new URL("http://74.50.59.155:5000/api/search?skip=" + skip + "&q=" + params.getText().toString().replace(" ", "+") + "&onlyInStock=" + check);


                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setUseCaches(true);
                httpCon.addRequestProperty("Cache-Control", "max-stale=3600" );
                httpCon.connect();
                InputStream is = httpCon.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                result = "[" + result.replace("}\n{", "},{") + "]";

                Gson gson = new Gson();

                Type listType = new TypeToken<ArrayList<Product>>() {
                }.getType();
                ArrayList<Product> temp = gson.fromJson(result, listType);
//

                if (temp.size() == 0) {
                    adapter.setFinised(true);
                }
                if (valid) {

                    products.addAll(temp);
                } else {
                    valid = true;
                }
                handler.sendEmptyMessage(1);


            } catch (Exception e) {

                e.printStackTrace();

            }

        }

    }


}
