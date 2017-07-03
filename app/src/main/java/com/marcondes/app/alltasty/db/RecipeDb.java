package com.marcondes.app.alltasty.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.marcondes.app.alltasty.dao.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeDb {
    private RecipeDbHelper mDbHelper;

    public RecipeDb(Context context) {
        this.mDbHelper = new RecipeDbHelper(context.getApplicationContext());
    }

    public void insert(Recipe recipe){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(RecipeContract.COL_LABEL, recipe.label);
            cv.put(RecipeContract.COL_IMAGE, recipe.image);
            cv.put(RecipeContract.COL_RECIPE, recipe.recipe);

            db.beginTransaction();
            long idRecipe = db.insert(RecipeContract.TABLE_RECIPE, null, cv);

            if (idRecipe != -1) {
                for (String ingredients : recipe.ingredients) {
                    cv.clear();
                    cv.put(RecipeContract.COL_RECIPE_ID, idRecipe);
                    cv.put(RecipeContract.COL_INGREDIENTS, ingredients);
                    db.insert(RecipeContract.TABLE_INGREDIENTS, null, cv);
                }
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public boolean favorite(Recipe recipe){
        boolean exist;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT "+ RecipeContract._ID +
                        " FROM "+ RecipeContract.TABLE_RECIPE +
                        " WHERE "+ RecipeContract.COL_LABEL +" = ?",
                new String[]{ recipe.label });
        exist = c.getCount() > 0;
        db.close();

        return exist;
    }

    public void delete(Recipe recipe){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(RecipeContract.TABLE_RECIPE,
                RecipeContract.COL_LABEL +" = ?",
                new String[]{ recipe.label });
        db.close();
    }

    public List<Recipe> getRecipes(){
        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursorRecipes = db.rawQuery(
                "SELECT * FROM "+ RecipeContract.TABLE_RECIPE +" ORDER BY "+ RecipeContract.COL_LABEL, null);

        while (cursorRecipes.moveToNext()){
            Recipe recipe = new Recipe();
            recipe.label = cursorRecipes.getString(cursorRecipes.getColumnIndex(RecipeContract.COL_LABEL));
            recipe.image = cursorRecipes.getString(cursorRecipes.getColumnIndex(RecipeContract.COL_IMAGE));
            recipe.recipe = cursorRecipes.getString(cursorRecipes.getColumnIndex(RecipeContract.COL_RECIPE));

            long id = cursorRecipes.getLong(cursorRecipes.getColumnIndex(RecipeContract._ID));


            Cursor cursorIngredients = db.rawQuery(
                    "SELECT * FROM " + RecipeContract.TABLE_INGREDIENTS +
                            " WHERE "+ RecipeContract.COL_RECIPE_ID +" = ?",
                    new String[]{ String.valueOf(id)});
            String[] ingredients = new String[cursorIngredients.getCount()];
            int i = 0;
            while (cursorIngredients.moveToNext()){
                ingredients[i] = cursorIngredients.getString(cursorIngredients.getColumnIndex(RecipeContract.COL_INGREDIENTS));
                i++;
            }
            recipe.ingredients = ingredients;
            cursorIngredients.close();
            recipes.add(recipe);
        }
        cursorRecipes.close();
        db.close();
        return recipes;
    }
}

