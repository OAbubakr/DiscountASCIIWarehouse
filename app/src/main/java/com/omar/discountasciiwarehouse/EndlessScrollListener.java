package com.omar.discountasciiwarehouse;

/**
 * Created by omari on 5/23/2017.
 */

import android.widget.AbsListView;


import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public class EndlessScrollListener implements OnScrollListener {

    private boolean isLoading;
    private boolean hasMorePages = true;
    private int pageNumber = 0;
    private RefreshList refreshList;




    public EndlessScrollListener(RefreshList refreshList) {

        this.isLoading = false;
        this.hasMorePages = true;
        this.refreshList = refreshList;
    }


    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount >= totalItemCount && !isLoading && hasMorePages) {
            isLoading = true;


            refreshList.onRefresh(pageNumber);
            pageNumber += 10;

        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public interface RefreshList {
        void onRefresh(int pageNumber);
    }
}


