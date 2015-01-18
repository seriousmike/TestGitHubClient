package ru.seriousmike.testgithubclient.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.Commit;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;

/**
 * Created by SeriousM on 17.01.2015.
 */
public class RepositoryFragment extends AlerterInterfaceFragment {

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
    private CommitsAdapter mAdapter;
    private List<Commit> mCommits;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        mInflater = inflater;
        View layout = inflater.inflate(R.layout.fragment_repository, parentView, false);

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
        mListView.addHeaderView(header);

        mCommits = new ArrayList<>();
        mAdapter = new CommitsAdapter(getActivity(), mCommits);
        mListView.setAdapter( mAdapter );
        mListView.setClickable(false);

        refreshCommits();
        return layout;
    }

    public void refreshCommits() {

        Log.i(TAG, "repository "+ getArguments().getString(REPO)+" by "+ getArguments().getString(OWNER));

        GitHubAPI.getInstance().getRepositoryCommits( getArguments().getString(OWNER) ,getArguments().getString(REPO), new RequestCallback<List<Commit>>() {
            @Override
            public void onSuccess(List<Commit> commitList) {
                mCommits.addAll(commitList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error_code) {
                if(error_code!=GitHubAPI.ERR_CODE_CONFLICT) {
                    defaultRequestFailureAction(error_code);
                } else {
                    //TODO emptyList
                }
            }
        });
    }



    private class CommitsAdapter extends ArrayAdapter<Commit> {

        public CommitsAdapter(Context context, List<Commit> commits) {
            super(context, R.layout.list_item_commit, commits);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_commit, parent, false);
            }

            ((TextView)convertView.findViewById(R.id.tvCommitHash)).setText(getItem(position).getName());
            ((TextView)convertView.findViewById(R.id.tvCommitMsg)).setText(getItem(position).commit.message);
            ((TextView)convertView.findViewById(R.id.tvCommitAuthor)).setText(getItem(position).commit.author.name);
            ((TextView)convertView.findViewById(R.id.tvCommitDate)).setText(getItem(position).getDate());

            return convertView;
        }
    }


}
