package com.marcondes.app.alltasty.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcondes.app.alltasty.R;
import com.marcondes.app.alltasty.dao.Recipe;
import com.squareup.picasso.Picasso;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Context mContext;
    private Recipe[] mRecipes;
    private toClickedOnRecipeListener mListener;

    public RecipeAdapter(Context ctx, Recipe[] recipes) {
        mContext = ctx;
        mRecipes = recipes;
    }
    public void setToClickedOnRecipeListener(toClickedOnRecipeListener l) {
        mListener = l;
    }
    @Override
    public int getItemCount() {
        return mRecipes != null ? mRecipes.length : 0;
    }
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_recipe, parent, false);
        RecipeViewHolder vh = new RecipeViewHolder(v);
        v.setTag(vh);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    RecipeViewHolder vh = (RecipeViewHolder)view.getTag();
                    int position = vh.getAdapterPosition();
                    mListener.toClickedOnRecipe(view, position, mRecipes[position]);
                }
            }
        });
        return vh;
    }
    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipes[position];
        Picasso.with(mContext).load(recipe.image).into(holder.imgCapa);
        holder.txtLabel.setText(recipe.label);
    }

    public interface toClickedOnRecipeListener {
        void toClickedOnRecipe(View v, int position, Recipe recipe);
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imgImage)
        public ImageView imgCapa;
        @Bind(R.id.txtLabel)
        public TextView txtLabel;

        public RecipeViewHolder(View parent) {
            super(parent);
            ButterKnife.bind(this, parent);
            ViewCompat.setTransitionName(imgCapa, "image");
            ViewCompat.setTransitionName(txtLabel, "label");
        }
    }
}

