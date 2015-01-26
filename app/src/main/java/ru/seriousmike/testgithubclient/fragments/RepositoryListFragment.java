package ru.seriousmike.testgithubclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.activities.RepositoryActivity;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.Repository;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;
import ru.seriousmike.testgithubclient.helpers.Helper;

/**
 * Created by SeriousM on 14.01.2015.
 */
public class RepositoryListFragment extends AlerterInterfaceFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "sm_RepositoryListFragment";

    private ListView mListView;
    private ArrayList<Repository> mRepos;
    private SwingBottomInAnimationAdapter mAdapter;
    private View mListStatusView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int PER_PAGE = 30;
    private int mPage;
    private boolean mIsUpdating = false;
    private boolean mEndOfTheList = false;
    private boolean mIsRetained = false;

    public RepositoryListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG,"on detach");
        mIsRetained = true;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        mIsRetained = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        Log.i(TAG,"onCreateView ... isDetached "+(isDetached()?" true ":" false "));

        View layout = inflater.inflate(R.layout.fragment_repository_list, parent, false);

        if(mRepos==null) mRepos = new ArrayList<>();
        mListView = (ListView) layout.findViewById(R.id.listRepositories);

        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefresher);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_background_base), getResources().getColor(R.color.color_accent) );

        View header = inflater.inflate(R.layout.list_header_repositories, mListView, false);
        String userName = GitHubAPI.getInstance().getCurrentUser().login;
        if(GitHubAPI.getInstance().getCurrentUser().name!=null && !GitHubAPI.getInstance().getCurrentUser().name.equals("")) {
            userName = GitHubAPI.getInstance().getCurrentUser().name + " ("+userName+")";
        }
        ((TextView)header.findViewById(R.id.tvUserName)).setText(getString(R.string.greetings)+"\n"+userName);

        header.findViewById(R.id.tvLogout).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AlertCaller)getActivity()).showAlertDialog(AlertDialogFragment.EVENT_LOGOUT, true, true, getString(R.string.logout), null);
            }
        } );

        mListView.addHeaderView(header, null, false);
        mListView.setHeaderDividersEnabled(true);

        mAdapter = new SwingBottomInAnimationAdapter(new RepositoryListAdapter(getActivity(), mRepos));
        mAdapter.setAbsListView(mListView);
        mListView.setAdapter( mAdapter );
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // т.к. headerview считается первой позицией, то
                position--;

                Intent i = new Intent(getActivity(), RepositoryActivity.class);

                Repository repo = (Repository)  mAdapter.getItem(position);

                Log.i(TAG, position+" "+mAdapter.getItem(position).toString());
                i.putExtra(RepositoryActivity.EXTRA_REPO, repo.name);
                i.putExtra(RepositoryActivity.EXTRA_OWNER, repo.owner.login );
                // т.к. данные о репозитории не кэшируются, а повторный запрос делать не стоит, отправляем нужные данные репозитория в интенте
                i.putExtra(RepositoryActivity.EXTRA_RI_ONWER_PIC, repo.owner.avatar_url);
                i.putExtra(RepositoryActivity.EXTRA_RI_DESCR, repo.description);
                i.putExtra(RepositoryActivity.EXTRA_RI_CREATED, Helper.formatDate( repo.created_at, Helper.FORMAT_DATETIME_SL));
                i.putExtra(RepositoryActivity.EXTRA_RI_PUSHED, Helper.formatDate(repo.pushed_at, Helper.FORMAT_DATETIME_SL));

                startActivity(i);
            }
        });

        // отдельная вьюха, которая будет выполнять роль футера для индикации загрузки или сообщения о пустом списке
        // решено не использовать setEmptyView, чтобы сохранить видимым HeaderView списка и не назначать его повторно в EmptyView
        mListStatusView = inflater.inflate(R.layout.list_message_view, mListView, false);
        mListStatusView.findViewById(R.id.tvRepeatButton).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListStatusView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                mListStatusView.findViewById(R.id.tvRepeatButton).setVisibility(View.GONE);
                retryRequest();
            }
        });

        // при изменении конфигурации, если список репозиториев не пуст, блокируется перезагрузка списка
        if(!mIsRetained || mRepos.size()==0) {
            loadFirstRepos();
        }

        mListView.setOnScrollListener( new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!mIsUpdating && !mEndOfTheList && !mIsRetained && mRepos.size()>0) {
                    if( firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount>0) {
                        Log.d(TAG,"ONSCROLL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        Log.d(TAG, "First " + firstVisibleItem + "; visible " + visibleItemCount + "; total " + totalItemCount);
                        loadMoreRepos();
                    }
                }
            }
        });

        return layout;
    }

    public void retryRequest() {
        if(mSwipeRefreshLayout.isRefreshing() || mRepos.size()==0) {
            loadFirstRepos();
        } else {
            loadMoreRepos();
        }
    }

    public void showRefreshFooter() {
        mListStatusView.findViewById(R.id.tvRepeatButton).setVisibility(View.VISIBLE);
        mListStatusView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        mListStatusView.findViewById(R.id.tvEmptyMessage).setVisibility(View.GONE);
        if(mListView.getFooterViewsCount()==0) {
            mListView.addFooterView(mListStatusView, null, false);
            mListView.setFooterDividersEnabled(false);
        }

    }

    private void loadFirstRepos() {
        Log.i(TAG,"first request");
        mPage = 1;
        mListStatusView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        mListStatusView.findViewById(R.id.tvEmptyMessage).setVisibility(View.GONE);
        if(mListView.getFooterViewsCount()==0) {
            mListView.addFooterView(mListStatusView, null, false);
            mListView.setFooterDividersEnabled(false);
        }

        mEndOfTheList = false;
        mPage = 1;


        GitHubAPI.getInstance().getRepositoriesList( PER_PAGE, mPage,  new RequestCallback<List<Repository>>() {
            @Override
            public void onSuccess(List<Repository> repositoryList) {
                mListView.removeFooterView(mListStatusView);
                mRepos.clear();
                mRepos.addAll(repositoryList);
                mAdapter.notifyDataSetChanged();

                cancelRefreshing();
                mIsUpdating = false;
                if(!GitHubAPI.getInstance().hasLastResponseNextPage()) {
                    mEndOfTheList = true;
                }

                if(mRepos.isEmpty()) {
                    mListStatusView.findViewById(R.id.tvRepeatButton).setVisibility(View.GONE);
                    mListStatusView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    mListStatusView.findViewById(R.id.tvEmptyMessage).setVisibility(View.VISIBLE);
                    ((TextView)mListStatusView.findViewById(R.id.tvEmptyMessage)).setText(R.string.no_repositories);
                    if(mListView.getFooterViewsCount()==0) {
                        mListView.addFooterView(mListStatusView, null, false);
                        mListView.setFooterDividersEnabled(false);
                    }
                }
            }

            @Override
            public void onFailure(int error_code) {
                defaultRequestFailureAction(error_code);
            }
        });


    }


    private void loadMoreRepos() {
        Log.i(TAG," -- Loading more!");
        mIsUpdating = true;
        mPage++;
        GitHubAPI.getInstance().getRepositoriesList( PER_PAGE, mPage, new RequestCallback<List<Repository>>() {
            @Override
            public void onSuccess(List<Repository> commitList) {
                if(mListView.getFooterViewsCount()>0) mListView.removeFooterView(mListStatusView);

                mRepos.addAll(commitList);
                mAdapter.notifyDataSetChanged();
                mIsUpdating = false;
                if(!GitHubAPI.getInstance().hasLastResponseNextPage()) {
                    mEndOfTheList = true;
                }
            }

            @Override
            public void onFailure(int error_code) {
                mPage--;
                failureHandler(error_code);
            }
        } );
    }

    private void failureHandler(int error_code) {
        if(error_code==GitHubAPI.ERR_CODE_CONFLICT) {
            mListStatusView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            mListStatusView.findViewById(R.id.tvEmptyMessage).setVisibility(View.VISIBLE);
            ((TextView)(mListStatusView.findViewById(R.id.tvEmptyMessage))).setText(getString(R.string.no_commits));
        } else {
            defaultRequestFailureAction(error_code);
        }
    }

    @Override
    public void onRefresh() {
        loadFirstRepos();
    }

    public void cancelRefreshing() {
        if(mSwipeRefreshLayout!=null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void processRequestFailure(int error_code) {
        ((AlertCaller)getActivity()).showAlertDialog(error_code, true, true, null, null);
    }

    public class RepositoryListAdapter extends ArrayAdapter<Repository> {

        public RepositoryListAdapter(Context context, List<Repository> objects) {
            super(context, R.layout.list_item_repository, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_repository, parent, false);

                ViewHolder holder = new ViewHolder();

                holder.ivOwnerPic = (ImageView) convertView.findViewById(R.id.ivRepoOwnerPic);

                holder.tvRepoOwner = ((TextView)convertView.findViewById(R.id.tvRepoOwner));
                holder.tvRepoName = ((TextView)convertView.findViewById(R.id.tvRepoName));
                holder.tvRepoDescription = ((TextView)convertView.findViewById(R.id.tvRepoDescription));
                if(convertView.findViewById(R.id.tvRepoForksWatchers)!=null) {
                    holder.tvRepoForksWatchers =  ((TextView)convertView.findViewById(R.id.tvRepoForksWatchers));
                } else {
                    holder.tvRepoForks = ((TextView)convertView.findViewById(R.id.tvRepoForks));
                    holder.tvRepoWatchers = ((TextView)convertView.findViewById(R.id.tvRepoWatchers));
                }
                convertView.setTag(holder);
            }


            ViewHolder viewHolder = (ViewHolder) convertView.getTag();

            Picasso.with(getActivity()).cancelRequest(viewHolder.ivOwnerPic);
            Picasso.with(getActivity()).load(getItem(position).owner.avatar_url)
                    .resize(getActivity().getResources().getDimensionPixelSize(R.dimen.repo_list_avatar),getActivity().getResources().getDimensionPixelSize(R.dimen.repo_list_avatar))
                    .into(viewHolder.ivOwnerPic);

            viewHolder.tvRepoOwner.setText(getItem(position).owner.login);
            viewHolder.tvRepoName.setText(getItem(position).name);
            viewHolder.tvRepoDescription.setText(getItem(position).description);
            if(viewHolder.tvRepoForksWatchers!=null) {
                viewHolder.tvRepoForksWatchers.setText(getItem(position).forks_count+"\n"+getItem(position).watchers_count);
            } else {
                viewHolder.tvRepoForks.setText(getItem(position).forks_count+"");
                viewHolder.tvRepoWatchers.setText(getItem(position).watchers_count+"");
            }


            return convertView;
        }

        private class ViewHolder {
            public ImageView ivOwnerPic;
            public TextView tvRepoName;
            public TextView tvRepoOwner;
            public TextView tvRepoDescription;
            public TextView tvRepoForksWatchers;
            public TextView tvRepoForks;
            public TextView tvRepoWatchers;
        }
    }


}

