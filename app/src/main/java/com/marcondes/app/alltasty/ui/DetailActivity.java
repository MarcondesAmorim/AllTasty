package com.marcondes.app.alltasty.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcondes.app.alltasty.R;
import com.marcondes.app.alltasty.dao.Recipe;
import com.marcondes.app.alltasty.db.RecipeApp;
import com.marcondes.app.alltasty.db.RecipeDb;
import com.marcondes.app.alltasty.db.RecipeEvento;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

        public static final String EXTRA_RECIPE = "recipe";

    @Bind(R.id.fabFavorite)
    FloatingActionButton mFabFavorite;
    @Bind(R.id.imgImage)
    ImageView mImgImage;
    @Bind(R.id.txtLabel)
    TextView mTxtLabel;


    @Nullable
    @Bind(R.id.coordinator)
    CoordinatorLayout mCoordinator;
    @Nullable
    @Bind(R.id.appBar)
    AppBarLayout mAppBar;
    @Nullable
    @Bind(R.id.collapseToolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    Target mPicassoTarget;
    RecipeDb mRecipeDb;



    @Bind(R.id.txtIngrents)
    TextView mTxtIngredients;
    Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Recipe recipe = (Recipe)getIntent().getSerializableExtra(EXTRA_RECIPE);
        fillField(recipe);

        configurarBarraDeTitulo(recipe.label);

        carregarCapa(recipe);

        configurarAnimacaoEntrada();

        mRecipeDb = new RecipeDb(this);
        configureFab(recipe);


        mBus = ((RecipeApp)getApplicationContext()).getBus();
        mBus.register(this);


    }


    private void carregarCapa(Recipe recipe) {
        if (mPicassoTarget == null){
            mPicassoTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mImgImage.setImageBitmap(bitmap);
                    iniciarAnimacaoDeEntrada(mCoordinator);
                    definirCores(bitmap);
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    iniciarAnimacaoDeEntrada(mCoordinator);
                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
        }
        Picasso.with(this)
                .load(recipe.image)
                .into(mPicassoTarget);
    }

     private void fillField(Recipe recipe) {
        mTxtLabel.setText(recipe.label);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recipe.ingredients.length; i++){
            if (sb.length() != 0) sb.append('\n');
//            sb.append(i+1).append(". ").append(recipe.ingredients[i]);
            sb.append(recipe.ingredients[i]);
        }
        mTxtIngredients.setText(sb.toString());
    }

    private void configurarBarraDeTitulo(String titulo) {
        setSupportActionBar(mToolbar);
        if (mAppBar != null) {
            if (mAppBar.getLayoutParams() instanceof CoordinatorLayout.LayoutParams ) {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                lp.height = getResources().getDisplayMetrics().widthPixels;
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mCollapsingToolbarLayout != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            mCollapsingToolbarLayout.setTitle(titulo);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void configurarAnimacaoEntrada() {
        ViewCompat.setTransitionName(mImgImage, "image");
        ViewCompat.setTransitionName(mTxtLabel, "label");
        ActivityCompat.postponeEnterTransition(this);
    }

    private void iniciarAnimacaoDeEntrada(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        ActivityCompat.startPostponedEnterTransition(DetailActivity.this);
                        return true;
                    }
                });
    }

    private void definirCores(Bitmap bitmap){
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(Color.BLACK);
                int darkVibrantColor = palette.getDarkVibrantColor(Color.BLACK);
                int darkMutedColor = palette.getDarkMutedColor(Color.BLACK);
                int lightMutedColor = palette.getLightMutedColor(Color.WHITE);

                mTxtLabel.setTextColor(vibrantColor);
                if (mAppBar != null) {
                    mAppBar.setBackgroundColor(vibrantColor);
                } else {
                    mToolbar.setBackgroundColor(Color.TRANSPARENT);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(darkMutedColor);
                }
                if (mCollapsingToolbarLayout != null) {
                    mCollapsingToolbarLayout.setContentScrimColor(darkVibrantColor);
                }
                mCoordinator.setBackgroundColor(lightMutedColor);
                iniciarAnimacaoDeEntrada(mCoordinator);
            }
        });
    }

    private void configureFab(final Recipe recipe) {
        boolean favorito = mRecipeDb.favorite(recipe);
        mFabFavorite.setImageDrawable(getFabIcone(favorito));
        mFabFavorite.setBackgroundTintList(getFabBackground(favorito));
        mFabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean favorito = mRecipeDb.favorite(recipe);
                if (favorito) {
                    mRecipeDb.delete(recipe);
                } else {
                    mRecipeDb.insert(recipe);
                }
                mFabFavorite.setImageDrawable(getFabIcone(!favorito));
                mFabFavorite.setBackgroundTintList(getFabBackground(!favorito));
                animar(!favorito);
                ((RecipeApp) getApplication()).getBus().post(new RecipeEvento(recipe));
            }
        });
    }
    private Drawable getFabIcone(boolean favorito){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return ResourcesCompat.getDrawable(
                    getResources(),
                    favorito ? R.drawable.ic_cancel_anim : R.drawable.ic_check_anim,
                    getTheme());
        } else {
            return getResources().getDrawable(
                    favorito ? R.drawable.ic_cancel : R.drawable.ic_check);
        }
    }
    private ColorStateList getFabBackground(boolean favorito) {
        return getResources().getColorStateList(favorito ?
                R.color.bg_fab_cancel : R.color.bg_fab_favorito);
    }
    @Override
    public void onBackPressed() {
        mFabFavorite.animate().scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                DetailActivity.super.onBackPressed();
            }
        }).start();
    }
    private void animar(boolean favorito){
        mFabFavorite.setBackgroundTintList(getFabBackground(favorito));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) getFabIcone(!favorito);
            mFabFavorite.setImageDrawable(avd);
            avd.start();
        } else {
            mFabFavorite.setImageDrawable(getFabIcone(favorito));
        }
    }
}

