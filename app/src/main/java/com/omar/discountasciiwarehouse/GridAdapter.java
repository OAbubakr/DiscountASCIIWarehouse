package com.omar.discountasciiwarehouse;

/**
 * Created by omari on 5/22/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<Product> products;


    public void setFinised(boolean finised) {
        this.finised = finised;
    }

    private boolean finised = false;

    public GridAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            gridView = inflater.inflate(R.layout.grid_cell, null);
        } else {
            gridView = convertView;
        }

        if (position == products.size()) {

            TextView face = (TextView) gridView
                    .findViewById(R.id.face);
            face.setText("LOADING...");
            TextView buyNow = (TextView) gridView
                    .findViewById(R.id.buy_now);
            buyNow.setVisibility(View.INVISIBLE);
            buyNow.setText("BUY NOW!\n");
            TextView price = (TextView) gridView
                    .findViewById(R.id.price);
            price.setVisibility(View.INVISIBLE);
        } else {
            TextView face = (TextView) gridView
                    .findViewById(R.id.face);
            face.setText(products.get(position).getFace());

            TextView price = (TextView) gridView
                    .findViewById(R.id.price);
            price.setText("$" + products.get(position).getPrice() / 100);
            TextView buyNow = (TextView) gridView
                    .findViewById(R.id.buy_now);
            if (products.get(position).getStock() == 1)
                buyNow.setText("BUY NOW!" + "\n (Only 1 more in stock!)");
            else buyNow.setText("BUY NOW!\n");
            buyNow.setVisibility(View.VISIBLE);
            price.setVisibility(View.VISIBLE);
        }



        gridView.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                TextView txt = (TextView) view.findViewById(R.id.buy_now);
                txt.setBackgroundColor(Color.parseColor("#ff0000"));
                return true;

            }
        });

        return gridView;
    }

    @Override
    public int getCount() {
        if (finised) return products.size();
        else return products.size() + 1;

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}