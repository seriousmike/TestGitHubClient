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
import ru.seriousmike.testgithubclient.core.AbstractListFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.Repository;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;
import ru.seriousmike.testgithubclient.helpers.Helper;

/**
 * Created by SeriousM on 14.01.2015.
 */
public class RepositoryListFragment extends AbstractListFragment {

    private static final String TAG = "sm_RepositoryListFragment";

    private ArrayList<Repository> mRepos;
    private SwingBottomInAnimationAdapter mAdapter;
    private ListView mListView;


    private static final int PER_PAGE = 3;
    private int mPage;
    private boolean mIsUpdating = false;
    private boolean mEndOfTheList = false;
    private boolean mIsRetained = false;


    public RepositoryListFragment() {}

    @Override
    protected void setFlagFragmentRetained(boolean isRetained) {
        mIsRetained = isRetained;
    }

    @Override
    protected void setIsFragmentDataUpdating(boolean isUpdating) {
        mIsUpdating = isUpdating;
    }

    @Override
    protected ArrayList getDataItems() {
        return mRepos;
    }

    @Override
    protected ListView getListView() {
        return mListView;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        Log.i(TAG,"onCreateView ... isDetached "+(isDetached()?" true ":" false "));

        View layout = inflater.inflate(R.layout.fragment_repository_list, parent, false);

        if(mRepos==null) mRepos = new ArrayList<>();
        mListView = (ListView) layout.findViewById(R.id.listRepositories);

        initSwipeRefreshLayout( (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefresher) );

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
                getActivity().overridePendingTransition(R.anim.activity_from_right_bottom_in, R.anim.activity_step_out);
            }
        });


        initListFooterView(inflater);

        // при изменении конфигурации блокируется перезагрузка списка
        if(!mIsRetained) {
            loadFirstItems();
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
                        loadMoreItems();
                    }
                }
            }
        });

        return layout;
    }


    @Override
    protected void loadFirstItems() {
        Log.i(TAG,"first request");
        mPage = 1;
        showFooterLoading();

        mEndOfTheList = false;
        mPage = 1;


        GitHubAPI.getInstance().getRepositoriesList( PER_PAGE, mPage,  new RequestCallback<List<Repository>>() {
            @Override
            public void onSuccess(List<Repository> repositoryList) {
                detachFooterFromList();
                mRepos.clear();
                mRepos.addAll(repositoryList);
                mAdapter.notifyDataSetChanged();
                mIsUpdating = false;
                cancelRefreshing();
                if(!GitHubAPI.getInstance().hasLastResponseNextPage()) {
                    mEndOfTheList = true;
                }

                if(mRepos.isEmpty()) {
                    showFooterMessage(getString(R.string.no_repositories));
                }
            }

            @Override
            public void onFailure(int error_code) {
                failureHandler(error_code);
            }
        });


    }

    @Override
    protected void loadMoreItems() {
        Log.i(TAG," -- Loading more!");
        mIsUpdating = true;
        showFooterLoading();
        mPage++;
        GitHubAPI.getInstance().getRepositoriesList( PER_PAGE, mPage, new RequestCallback<List<Repository>>() {
            @Override
            public void onSuccess(List<Repository> commitList) {
                detachFooterFromList();

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
            showFooterMessage(getString(R.string.no_repositories));
        } else {
            defaultRequestFailureAction(error_code);
        }
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

