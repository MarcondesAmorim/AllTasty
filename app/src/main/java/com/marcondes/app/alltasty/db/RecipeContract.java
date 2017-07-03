package com.marcondes.app.alltasty.db;

import android.provider.BaseColumns;

public interface RecipeContract extends BaseColumns {
    String TABLE_RECIPE = "recipes";
    String COL_RECIPE_ID = "recipe_id";
    String COL_LABEL = "label";
    String COL_IMAGE = "image";
    String COL_RECIPE = "recipe";

    String TABLE_INGREDIENTS = "ingredients";
    String COL_INGREDIENTS_ID = "ingredients_id";
    String COL_INGREDIENTS = "ingredients";
}

