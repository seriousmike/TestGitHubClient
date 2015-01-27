package ru.seriousmike.testgithubclient.core;

/**
 * Created by mlapae01 on 27.01.2015.
 */

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.seriousmike.testgithubclient.R;


/**
 * Общий фрагмент списка со стандартными функциями
 */
public abstract class AbstractListFragment extends AlerterInterfaceFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG  = "sm_AbstractListFragment";

    protected abstract void setFlagFragmentRetained(boolean isRetained);
    protected abstract void setIsFragmentDataUpdating(boolean isRefreshing);

    protected abstract void loadFirstItems();
    protected abstract void loadMoreItems();

    protected abstract ArrayList getDataItems();

    protected abstract ListView getListView();

    private View mListStatusView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int FS_HIDDEN = 0;
    private static final int FS_LOADING = 2;
    private static final int FS_MESSAGE = 3;
    private static final int FS_BUTTON = 4;
    private String mCachedFooterMsg;
    private int mFooterStatus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG,"on detach");
        setFlagFragmentRetained(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        setFlagFragmentRetained(false);
    }





    /**
     * использует отдельную вьюху, которая будет выполнять роль футера для индикации загрузки или сообщения о пустом списке
     * решено не использовать setEmptyView, чтобы сохранить видимым HeaderView списка и не назначать его повторно в EmptyView
     * @param inflater LayoutInflater
     */
    protected void initListFooterView(LayoutInflater inflater) {
        mListStatusView = inflater.inflate(R.layout.list_message_view, getListView(), false);
        mListStatusView.findViewById(R.id.tvRepeatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFooterLoading();
                retryRequest();
            }
        } );
        switch(mFooterStatus) {
            case FS_BUTTON:
                showFooterButton();
                break;
            case FS_LOADING:
                showFooterLoading();
                break;
            case FS_MESSAGE:
                if(mCachedFooterMsg!=null)
                showFooterMessage(mCachedFooterMsg);
                break;
        }

    }

    protected void detachFooterFromList() {
        if(getListView().getFooterViewsCount()>0) {
            getListView().removeFooterView(mListStatusView);
            mFooterStatus = FS_HIDDEN;
        }
    }

    protected void attachFooterToList() {
        if(getListView().getFooterViewsCount()==0) {
            getListView().addFooterView(mListStatusView, null, false);
            getListView().setFooterDividersEnabled(false);
        }
    }

    public void showFooterButton() {
        mListStatusView.findViewById(R.id.tvRepeatButton).setVisibility(View.VISIBLE);
        mListStatusView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        mListStatusView.findViewById(R.id.tvEmptyMessage).setVisibility(View.GONE);
        attachFooterToList();
        mFooterStatus = FS_BUTTON;
    }

    public void showFooterMessage(String msg) {
        mListStatusView.findViewById(R.id.tvRepeatButton).setVisibility(View.GONE);
        mListStatusView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        mListStatusView.findViewById(R.id.tvEmptyMessage).setVisibility(View.VISIBLE);
        ((TextView)mListStatusView.findViewById(R.id.tvEmptyMessage)).setText(msg);
        attachFooterToList();
        mFooterStatus = FS_MESSAGE;
        mCachedFooterMsg = msg;
    }

    public void showFooterLoading() {
        mListStatusView.findViewById(R.id.tvRepeatButton).setVisibility(View.GONE);
        mListStatusView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        mListStatusView.findViewById(R.id.tvEmptyMessage).setVisibility(View.GONE);
        attachFooterToList();
        mFooterStatus = FS_LOADING;
    }



    protected void initSwipeRefreshLayout(SwipeRefreshLayout swipeRefresher) {
        mSwipeRefreshLayout = swipeRefresher;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_background_base), getResources().getColor(R.color.color_accent));
    }

    @Override
    public void onRefresh() {
        loadFirstItems();
    }

    public void cancelRefreshing() {
        if(mSwipeRefreshLayout!=null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void retryRequest() {
        if(mSwipeRefreshLayout.isRefreshing() || getDataItems().size()==0) {
            loadFirstItems();
        } else {
            loadMoreItems();
        }
    }



    @Override
    protected void processRequestFailure(int error_code) {
        ((AlertCaller)getActivity()).showAlertDialog(error_code, true, true, null, null);
    }







}

