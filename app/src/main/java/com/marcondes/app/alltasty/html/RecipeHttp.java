package com.marcondes.app.alltasty.html;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.marcondes.app.alltasty.dao.Recipe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.util.concurrent.TimeUnit;

public class RecipeHttp {
    public static Recipe[] loadRecipes(String query) {
        query = "rice+meat";
        String url = "https://api.edamam.com/search?q=" +
                query + "&app_id=89cfc9fd&app_key=bb79e8f482e2ab097f4fcb509b148d4f&from=0&to=60";
        Recipe[] recipes;
        try {
            HttpURLConnection conexao = connect(url);
            int resposta = conexao.getResponseCode();
            if (resposta == HttpURLConnection.HTTP_OK) {
                InputStream is = conexao.getInputStream();
                JSONObject json = new JSONObject(bytesParaString(is));
                return listToVectorRecipe(readJsonRecipes(json));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Recipe[] listToVectorRecipe(List<Recipe> list) {
        if (!list.isEmpty()) {
            Recipe[] array = new Recipe[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            return array;
        }
        return null;
    }


    private static HttpURLConnection connect(String urlArquivo) throws IOException {
        final int SEGUNDOS = 1000;
        URL url = new URL(urlArquivo);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setReadTimeout(10 * SEGUNDOS);
        conexao.setConnectTimeout(15 * SEGUNDOS);
        conexao.setRequestMethod("GET");
        conexao.setDoInput(true);
        conexao.setDoOutput(false);
        conexao.connect();
        return conexao;
    }

    private static String bytesParaString(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        // O bufferzao vai armazenar todos os bytes lidos
        ByteArrayOutputStream bufferzao = new ByteArrayOutputStream();
        // precisamos saber quantos bytes foram lidos
        int bytesLidos;
        // Vamos lendo de 1KB por vez...
        while ((bytesLidos = is.read(buffer)) != -1) {
            // copiando a quantidade de bytes lidos do buffer para o bufferzão
            bufferzao.write(buffer, 0, bytesLidos);
        }
        return new String(bufferzao.toByteArray(), "UTF-8");
    }

    public static List<Recipe> readJsonRecipes(JSONObject json) throws JSONException {
        JSONArray jsonHits = json.getJSONArray("hits");
        List<Recipe> recipes = new ArrayList<Recipe>();
        for (int i = 0; i < jsonHits.length(); i++) {
            JSONObject Object = jsonHits.getJSONObject(i).getJSONObject("recipe");
            String label = Object.getString("label");
            String image = Object.getString("image");
            String recipeUrl = Object.getString("url");
            JSONArray jsonIngredients = Object.getJSONArray("ingredients");
            String[] ingredients = new String[jsonIngredients.length()];
            for (int k = 0; k < jsonIngredients.length(); k++) {
                ingredients[k] = jsonIngredients.getJSONObject(k).getString("text");
            }
            Recipe recipe = new Recipe(label, image, recipeUrl, ingredients);
            recipes.add(recipe);
        }
        return recipes;
    }
}

