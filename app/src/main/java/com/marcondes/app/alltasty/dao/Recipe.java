package com.marcondes.app.alltasty.dao;


import java.io.Serializable;

public class Recipe implements Serializable{
    public String label;
    public String image;
    public String recipe;
    public String[] ingredients;

    public Recipe(String label, String image, String recipe, String[] ingredients){
       this.label = label;
        this.image = image;
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    public Recipe(){};
}