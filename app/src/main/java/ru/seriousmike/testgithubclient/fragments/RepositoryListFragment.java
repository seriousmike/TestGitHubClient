package ru.seriousmike.testgithubclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.activities.RepositoryActivity;
import ru.seriousmike.testgithubclient.activities.TestActivity;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.Repository;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;

/**
 * Created by SeriousM on 14.01.2015.
 */
public class RepositoryListFragment extends AlerterInterfaceFragment {

    public RepositoryListFragment() {}

    private ListView mListView;
    private ArrayList<Repository> mRepos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_repository_list, parent, false);
        ((TextView)layout.findViewById(R.id.tvUserName)).setText(GitHubAPI.getInstance(getActivity().getApplicationContext()).getCurrentUser().name);


        //TODO добавить в листхэдер всякое о пользователе
        mRepos = new ArrayList<Repository>();
        mListView = (ListView) layout.findViewById(R.id.listRepositories);
        mListView.setAdapter( new RepositoryListAdapter(getActivity(), mRepos) );
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), RepositoryActivity.class);
                i.putExtra(RepositoryActivity.EXTRA_REPO, ((RepositoryListAdapter)mListView.getAdapter()).getItem(position).name );
                i.putExtra(RepositoryActivity.EXTRA_OWNER, ((RepositoryListAdapter)mListView.getAdapter()).getItem(position).owner.login );
                startActivity(i);
            }
        });

        GitHubAPI.getInstance().getRepositoriesList( new RequestCallback<List<Repository>>() {
            @Override
            public void onSuccess(List<Repository> repositoryList) {
                mRepos.addAll(repositoryList);
                ((RepositoryListAdapter)mListView.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error_code) {
                defaultRequestFailureAction(error_code);
            }
        });

        return layout;
    }




    public class RepositoryListAdapter extends ArrayAdapter<Repository> {

        public RepositoryListAdapter(Context context, List<Repository> objects) {
            super(context, R.layout.list_item_repository, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_repository, parent, false);
            }

            ImageView ownerPic = (ImageView) convertView.findViewById(R.id.ivRepoOwnerPic);
            Picasso.with(getActivity()).cancelRequest(ownerPic);
            Picasso.with(getActivity()).load(getItem(position).owner.avatar_url)
                    .resize(getActivity().getResources().getDimensionPixelSize(R.dimen.repo_list_avatar),getActivity().getResources().getDimensionPixelSize(R.dimen.repo_list_avatar))
                    .into(ownerPic);

            ((TextView)convertView.findViewById(R.id.tvRepoOwner)).setText(getItem(position).owner.login);
            ((TextView)convertView.findViewById(R.id.tvRepoName)).setText(getItem(position).name);
            ((TextView)convertView.findViewById(R.id.tvRepoDescription)).setText(getItem(position).description);
            if(convertView.findViewById(R.id.tvRepoForksWatchers)!=null) {
                ((TextView)convertView.findViewById(R.id.tvRepoForksWatchers)).setText(getItem(position).forks_count+"\n"+getItem(position).watchers_count);
            } else {
                ((TextView)convertView.findViewById(R.id.tvRepoForks)).setText(getItem(position).forks_count+"");
                ((TextView)convertView.findViewById(R.id.tvRepoWatchers)).setText(getItem(position).watchers_count+"");
            }


            return convertView;
        }
    }


}
