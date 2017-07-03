package com.marcondes.app.alltasty.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcondes.app.alltasty.R;
import com.marcondes.app.alltasty.dao.Recipe;
import com.marcondes.app.alltasty.db.RecipeApp;
import com.marcondes.app.alltasty.db.RecipeDb;
import com.marcondes.app.alltasty.db.RecipeEvento;

import java.util.ArrayList;
import java.util.List;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListRecipesFavoritesFragment extends Fragment
        implements RecipeAdapter.toClickedOnRecipeListener {

    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipe;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    RecipeDb mRecipeDb;
    List<Recipe> mRecipes;
    Bus mBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBus = ((RecipeApp)getActivity().getApplication()).getBus();
        mBus.register(this);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mBus.unregister(this);
        mBus = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_recipes, container, false);
        ButterKnife.bind(this, v);
        mSwipe.setEnabled(false);
        mRecyclerView.setTag("fav");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecipeDb = new RecipeDb(getActivity());
        if (mRecipes == null) {
            mRecipes = new ArrayList<Recipe>();
        }
        updateList();
    }

    private void updateList() {
        Recipe[] array = new Recipe[mRecipes.size()];
        mRecipes.toArray(array);
        RecipeAdapter adapter = new RecipeAdapter(getActivity(), array);
        adapter.setToClickedOnRecipeListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void toClickedOnRecipe(View v, int position, Recipe recipe) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                Pair.create(v.findViewById(R.id.imgImage), "image"),
                Pair.create(v.findViewById(R.id.txtLabel), "label")
        );
        Intent it = new Intent(getActivity(), DetailActivity.class);
        it.putExtra(DetailActivity.EXTRA_RECIPE, recipe);
        ActivityCompat.startActivity(getActivity(), it, options.toBundle());
    }
    @Subscribe
    public void atualizarLista(RecipeEvento event) {
        mRecipes = mRecipeDb.getRecipes();
        updateList();
    }

}
