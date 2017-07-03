package com.marcondes.app.alltasty.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marcondes.app.alltasty.R;
import com.marcondes.app.alltasty.dao.Recipe;
import com.marcondes.app.alltasty.db.RecipeApp;
import com.marcondes.app.alltasty.html.RecipeHttp;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListRecipesFragment extends Fragment
        implements RecipeAdapter.toClickedOnRecipeListener {

    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipe;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    Recipe[] mRecipes;
    RecipesDownloadTask mTask;

    Bus mBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

       mBus = ((RecipeApp)getActivity().getApplication()).getBus();
        mBus.register(this);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_recipes, container, false);
        ButterKnife.bind(this, v);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTask = new RecipesDownloadTask();
                mTask.execute();
            }
        });
        mRecyclerView.setTag("web");
        mRecyclerView.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return v;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mRecipes == null) {
            if (mTask == null) {

                mTask = new RecipesDownloadTask();
                mTask.execute();
            } else if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
                showProgress();
            }
        } else {
            updateList();
        }
    }
    @Override
    public void toClickedOnRecipe(View v, int position, Recipe recipe) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        Pair.create(v.findViewById(R.id.imgImage), "capa"),
                        Pair.create(v.findViewById(R.id.txtLabel), "titulo")
                );
        Intent it = new Intent(getActivity(), DetailActivity.class);
        it.putExtra(DetailActivity.EXTRA_RECIPE, recipe);
        ActivityCompat.startActivity(getActivity(), it, options.toBundle());
    }

    private void updateList() {
        RecipeAdapter adapter = new RecipeAdapter(getActivity(), mRecipes);
        adapter.setToClickedOnRecipeListener(this);
        mRecyclerView.setAdapter(adapter);
    }
    private void showProgress(){
        mSwipe.post(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(true);
            }
        });
    }
    class RecipesDownloadTask extends AsyncTask<Void, Void, Recipe[]>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
        @Override
        protected Recipe[] doInBackground(Void... params) {
            return RecipeHttp.loadRecipes("");
        }
        @Override
        protected void onPostExecute(Recipe[] recipes) {
            super.onPostExecute(recipes);
            mSwipe.setRefreshing(false);
            if (recipes != null) {
                mRecipes= recipes;
                updateList();
            }
        }
    }

    @Subscribe
    public void getPost(String query){
        RecipeHttp.loadRecipes(query);
        updateList();
        Toast.makeText(this.getContext(),query, Toast.LENGTH_LONG).show();
    }
}
