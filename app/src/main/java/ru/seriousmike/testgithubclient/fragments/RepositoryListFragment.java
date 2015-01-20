package ru.seriousmike.testgithubclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.Repository;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;
import ru.seriousmike.testgithubclient.helpers.Helper;

/**
 * Created by SeriousM on 14.01.2015.
 */
public class RepositoryListFragment extends AlerterInterfaceFragment {

    private static final String TAG = "sm_RepositoryListFragment";

    private ListView mListView;
    private ArrayList<Repository> mRepos;
    private RepositoryListAdapter mAdapter;

    public RepositoryListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_repository_list, parent, false);

        mRepos = new ArrayList<>();
        mListView = (ListView) layout.findViewById(R.id.listRepositories);

        View header = inflater.inflate(R.layout.list_header_repositories, mListView, false);
        ((TextView)header.findViewById(R.id.tvUserName)).setText(getString(R.string.greetings)+"\n"+GitHubAPI.getInstance(getActivity().getApplicationContext()).getCurrentUser().name);

        header.findViewById(R.id.ibLogout).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AlertCaller)getActivity()).showAlertDialog(AlertDialogFragment.EVENT_LOGOUT, true, true, getString(R.string.logout), null);
            }
        } );

        mListView.addHeaderView(header);
        mListView.setHeaderDividersEnabled(true);

        mAdapter = new RepositoryListAdapter(getActivity(), mRepos);

        mListView.setAdapter( mAdapter );
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // т.к. headerview считается первой позицией, то
                position--;

                Intent i = new Intent(getActivity(), RepositoryActivity.class);

                Log.i(TAG, position+" "+mAdapter.getItem(position).toString());

                i.putExtra(RepositoryActivity.EXTRA_REPO, mAdapter.getItem(position).name);
                i.putExtra(RepositoryActivity.EXTRA_OWNER, mAdapter.getItem(position).owner.login );
                // т.к. данные о репозитории не кэшируются, а повторный запрос делать не стоит, отправляем нужные данные репозитория в интенте
                i.putExtra(RepositoryActivity.EXTRA_RI_ONWER_PIC, mAdapter.getItem(position).owner.avatar_url);
                i.putExtra(RepositoryActivity.EXTRA_RI_DESCR, mAdapter.getItem(position).description);
                i.putExtra(RepositoryActivity.EXTRA_RI_CREATED, Helper.formatDate( mAdapter.getItem(position).created_at, Helper.FORMAT_DATETIME_SL));
                i.putExtra(RepositoryActivity.EXTRA_RI_PUSHED, Helper.formatDate( mAdapter.getItem(position).pushed_at, Helper.FORMAT_DATETIME_SL));

                startActivity(i);
            }
        });

        GitHubAPI.getInstance().getRepositoriesList( new RequestCallback<List<Repository>>() {
            @Override
            public void onSuccess(List<Repository> repositoryList) {
                mRepos.clear();
                mRepos.addAll(repositoryList);
                mAdapter.notifyDataSetChanged();
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

