package com.example.miapputn2026.api;

import okhttp3.*;

import com.example.miapputn2026.models.Pizza;
import com.example.miapputn2026.models.Ingredient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ApiClient {

    // URL base de la API
    private static final String BASE_URL = "https://api-pizzeria-2ed4.onrender.com/";

    // cliente HTTP
    private static final OkHttpClient client = new OkHttpClient();

    // convertir JSON ↔ objetos
    private static final Gson gson = new Gson();

    // tipo JSON para POST y PUT
    private static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");


    // ================= PIZZAS =================

    // obtener todas las pizzas
    public static List<Pizza> getPizzas() throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Pizzas")
                .build();

        return ejecutarLista(request, Pizza.class);
    }

    // obtener pizza por id
    public static Pizza getPizza(int id) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Pizzas/" + id)
                .build();

        return ejecutarObjeto(request, Pizza.class);
    }

    // crear pizza
    public static Pizza createPizza(Pizza pizza) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Pizzas")
                .post(RequestBody.create(gson.toJson(pizza), JSON))
                .build();

        return ejecutarObjeto(request, Pizza.class);
    }

    // actualizar pizza
    public static Pizza updatePizza(int id, Pizza pizza) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Pizzas/" + id)
                .put(RequestBody.create(gson.toJson(pizza), JSON))
                .build();

        ejecutar(request);
        return pizza;
    }

    // eliminar pizza
    public static void deletePizza(int id) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Pizzas/" + id)
                .delete()
                .build();

        ejecutar(request);
    }


    // ================= INGREDIENTES =================

    // obtener todos
    public static List<Ingredient> getIngredients() throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Ingredients")
                .build();

        return ejecutarLista(request, Ingredient.class);
    }

    // obtener por id
    public static Ingredient getIngredient(int id) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Ingredients/" + id)
                .build();

        return ejecutarObjeto(request, Ingredient.class);
    }

    // crear
    public static Ingredient createIngredient(Ingredient ingredient) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Ingredients")
                .post(RequestBody.create(gson.toJson(ingredient), JSON))
                .build();

        return ejecutarObjeto(request, Ingredient.class);
    }

    // actualizar
    public static Ingredient updateIngredient(int id, Ingredient ingredient) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Ingredients/" + id)
                .put(RequestBody.create(gson.toJson(ingredient), JSON))
                .build();

        ejecutar(request);
        return ingredient;
    }

    // eliminar
    public static void deleteIngredient(int id) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/Ingredients/" + id)
                .delete()
                .build();

        ejecutar(request);
    }


    // ================= RELACION =================

    // agregar ingrediente a pizza
    public static void addIngredientToPizza(int pizId, int ingId) throws Exception {
        String jsonBody = "{ \"pizId\": " + pizId + ", \"ingId\": " + ingId + " }";

        Request request = new Request.Builder()
                .url(BASE_URL + "api/PizzaIngredients")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        ejecutar(request);
    }

    // obtener ingredientes de una pizza
    public static String getIngredientsByPizza(int pizId) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/PizzaIngredients/pizza/" + pizId)
                .build();

        return ejecutarTexto(request);
    }


    // ================= METODOS GENERALES =================

    // ejecutar y devolver lista
    private static <T> List<T> ejecutarLista(Request request, Class<T> clase) throws Exception {
        Response response = client.newCall(request).execute();
        String json = response.body() != null ? response.body().string() : "[]";

        if (!response.isSuccessful()) {
            throw new Exception("ERROR " + response.code() + ": " + json);
        }

        return gson.fromJson(json, TypeToken.getParameterized(List.class, clase).getType());
    }

    // ejecutar y devolver objeto
    private static <T> T ejecutarObjeto(Request request, Class<T> clase) throws Exception {
        Response response = client.newCall(request).execute();
        String json = response.body() != null ? response.body().string() : "{}";

        if (!response.isSuccessful()) {
            throw new Exception("ERROR " + response.code() + ": " + json);
        }

        return gson.fromJson(json, clase);
    }

    // ejecutar sin retorno
    private static void ejecutar(Request request) throws Exception {
        Response response = client.newCall(request).execute();
        String json = response.body() != null ? response.body().string() : "";

        if (!response.isSuccessful()) {
            throw new Exception("ERROR " + response.code() + ": " + json);
        }
    }

    // ejecutar y devolver texto (para JSON crudo)
    private static String ejecutarTexto(Request request) throws Exception {
        Response response = client.newCall(request).execute();
        String json = response.body() != null ? response.body().string() : "";

        if (!response.isSuccessful()) {
            throw new Exception("ERROR " + response.code() + ": " + json);
        }

        return json;
    }
}