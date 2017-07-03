package com.marcondes.app.alltasty.ui;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.marcondes.app.alltasty.R;
import com.marcondes.app.alltasty.dao.Recipe;
import com.marcondes.app.alltasty.db.RecipeApp;
import com.marcondes.app.alltasty.html.RecipeHttp;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.v7.appcompat.R.styleable.View;

public class RecipeActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private List<String> items;
    public Bus mBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mViewPager.setAdapter(new RecipePagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        mBus = ((RecipeApp)getApplicationContext()).getBus();
        mBus.register(this);

    }

    class RecipePagerAdapter extends FragmentPagerAdapter {

        public RecipePagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return position == 0 ?
                    new ListRecipesFragment() :
                    new ListRecipesFavoritesFragment();
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ?
                    getString(R.string.tab_all) :
                    getString(R.string.tab_favorites);
        }
    }

}


