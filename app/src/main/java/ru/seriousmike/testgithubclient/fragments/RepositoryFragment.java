package ru.seriousmike.testgithubclient.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.AbstractListFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.Commit;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;

/**
 * Created by SeriousM on 17.01.2015.
 */
public class RepositoryFragment extends AbstractListFragment {

    private static final String TAG = "sm_RepositoryFragment";

    private static final String REPO = "repo";
    private static final String OWNER = "owner";
    private static final String REPO_INFO = "repo_info";

    public static final String REPOINFO_CREATED = "created_at";
    public static final String REPOINFO_PUSHED = "pushed_at";
    public static final String REPOINFO_OWNER_NAME = "owner_name";
    public static final String REPOINFO_OWNER_PIC = "avatar";
    public static final String REPOINFO_DESCR = "description";

    private LayoutInflater mInflater;
    private ListView mListView;
    private SwingBottomInAnimationAdapter mAdapter;
    private ArrayList<Commit> mCommits;

    private static final int PER_PAGE = 50;
    private int mPage;
    private boolean mIsUpdating = false;
    private boolean mEndOfTheList = false;
    private boolean mIsRetained = false;


    public static RepositoryFragment getInstance(String owner, String repo, HashMap<String,String> repoInfo) {
        Bundle args = new Bundle();
        args.putString(OWNER, owner);
        args.putString(REPO, repo);
        args.putSerializable(REPO_INFO, repoInfo);
        RepositoryFragment fragment = new RepositoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
        return mCommits;
    }

    @Override
    protected ListView getListView() {
        return mListView;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        mInflater = inflater;
        View layout = inflater.inflate(R.layout.fragment_repository, parentView, false);

        initSwipeRefreshLayout((SwipeRefreshLayout) layout.findViewById(R.id.swipeRefresher));

        mListView = (ListView)layout.findViewById(R.id.listCommits);

        View header = mInflater.inflate(R.layout.list_header_commits, mListView, false);

        HashMap<String,String> repoInfo = (HashMap<String,String>)getArguments().getSerializable(REPO_INFO);

        if(repoInfo.get(REPOINFO_OWNER_PIC)!=null) {
            Picasso.with(getActivity()).load( repoInfo.get(REPOINFO_OWNER_PIC) )
                    .resize( getResources().getDimensionPixelSize(R.dimen.commit_list_header_avatar), getResources().getDimensionPixelSize(R.dimen.commit_list_header_avatar) )
                    .into( (ImageView) header.findViewById(R.id.ivRepoOwnerPic) );
        }

        ((TextView)header.findViewById(R.id.tvRepoName)).setText(getArguments().getString(REPO));
        if(repoInfo.get(REPOINFO_OWNER_NAME)!=null) {
            ((TextView)header.findViewById(R.id.tvRepoOwner)).setText( repoInfo.get(REPOINFO_OWNER_NAME)+" ("+getArguments().getString(OWNER)+")" );
        } else {
            ((TextView)header.findViewById(R.id.tvRepoOwner)).setText( getArguments().getString(OWNER) );
        }


        if(repoInfo.get(REPOINFO_CREATED)!=null) {
            ((TextView)header.findViewById(R.id.tvRepoDateCreated)).setText( repoInfo.get(REPOINFO_CREATED ));
        } else {
            ((TextView)header.findViewById(R.id.tvRepoDateCreated)).setText( getString(R.string.na ));
        }

        if(repoInfo.get(REPOINFO_PUSHED)!=null) {
            ((TextView)header.findViewById(R.id.tvRepoDatePushed)).setText( repoInfo.get(REPOINFO_PUSHED) );
        } else {
            ((TextView)header.findViewById(R.id.tvRepoDatePushed)).setText( getString(R.string.na) );
        }


        if(repoInfo.get(REPOINFO_DESCR)!=null && !repoInfo.get(REPOINFO_DESCR).equals("")) {
            ((TextView)header.findViewById(R.id.tvRepoDescription)).setText( getString(R.string.repo_description)+" "+repoInfo.get(REPOINFO_DESCR) );
        } else {
            header.findViewById(R.id.tvRepoDescription).setVisibility(View.GONE);
        }



        mListView.setHeaderDividersEnabled(true);
        mListView.addHeaderView(header, null, false);

        if(mCommits==null) mCommits = new ArrayList<>();
        mAdapter = new SwingBottomInAnimationAdapter(new CommitsAdapter(getActivity(), mCommits));
        mAdapter.setAbsListView(mListView);
        mListView.setAdapter( mAdapter );
        mListView.setClickable(false);


        initListFooterView(inflater);

        // предотвращает перезагрузку коммитов при изменении конфигурации, если список коммитов не был пуст
        if(!mIsRetained) {
            loadFirstItems();
        }



        mListView.setOnScrollListener( new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!mIsUpdating && !mEndOfTheList && !mIsRetained && mCommits.size()>0) {
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
        mIsUpdating = true;

        showFooterLoading();

        mEndOfTheList = false;
        mPage = 1;
        Log.i(TAG, "repository "+ getArguments().getString(REPO)+" by "+ getArguments().getString(OWNER));

        GitHubAPI.getInstance().getRepositoryCommits( getArguments().getString(OWNER) ,getArguments().getString(REPO), PER_PAGE, mPage, new RequestCallback<List<Commit>>() {
            @Override
            public void onSuccess(List<Commit> commitList) {
                detachFooterFromList();
                mCommits.clear();
                mCommits.addAll(commitList);
                mAdapter.notifyDataSetChanged();
                mIsUpdating = false;
                cancelRefreshing();
                if(!GitHubAPI.getInstance().hasLastResponseNextPage()) {
                    mEndOfTheList = true;
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
        mPage++;
        showFooterLoading();
        GitHubAPI.getInstance().getRepositoryCommits( getArguments().getString(OWNER) ,getArguments().getString(REPO), PER_PAGE, mPage, new RequestCallback<List<Commit>>() {
            @Override
            public void onSuccess(List<Commit> commitList) {
                detachFooterFromList();
                mCommits.addAll(commitList);
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
        });
    }



    private void failureHandler(int error_code) {
        if(error_code==GitHubAPI.ERR_CODE_CONFLICT) {
            showFooterMessage(getString(R.string.no_commits));
            cancelRefreshing();
        } else if(error_code == GitHubAPI.ERR_CODE_NOT_FOUND) {
            mCommits.clear();
            mAdapter.notifyDataSetChanged();
            showFooterMessage(getString(R.string.repo_unavailable));
            cancelRefreshing();
        } else {
            defaultRequestFailureAction(error_code);
        }
    }







    private class CommitsAdapter extends ArrayAdapter<Commit> {

        public CommitsAdapter(Context context, List<Commit> commits) {
            super(context, R.layout.list_item_commit, commits);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_commit, parent, false);

                ViewHolder holder = new ViewHolder();
                holder.tvName = ((TextView)convertView.findViewById(R.id.tvCommitHash));
                holder.tvMessage = ((TextView)convertView.findViewById(R.id.tvCommitMsg));
                holder.tvAuthor = ((TextView)convertView.findViewById(R.id.tvCommitAuthor));
                holder.tvDate = ((TextView)convertView.findViewById(R.id.tvCommitDate));

                convertView.setTag(holder);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.tvName.setText(getItem(position).getName());
            viewHolder.tvMessage.setText(getItem(position).commit.message);
            viewHolder.tvAuthor.setText(getItem(position).commit.author.name);
            viewHolder.tvDate.setText(getItem(position).getDate());

            return convertView;
        }

        private class ViewHolder {
            public TextView tvName;
            public TextView tvMessage;
            public TextView tvAuthor;
            public TextView tvDate;
        }
    }


}
