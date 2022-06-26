package org.dieschnittstelle.mobile.android.skeleton.model;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RetrofitRemoteToDoItemCRUDOperations implements IToDoItemCRUDOperations {
    public interface ToDoWebAPI {
        @POST("/api/todos")
        public Call<TodoItem> createToDo(@Body TodoItem item);

        @GET("/api/todos")
        public Call<List<TodoItem>> readAllTodos();

        @GET("/api/todos/{todoId}")
        public Call<TodoItem> readTodo(@Path("todoId") long id);

        @PUT("/api/todos/{todoId}")
        public Call<TodoItem> updateTodo(@Path("todoId") long id, @Body TodoItem item);

        @DELETE("/api/todos/{todoId}")
        public Call<Boolean> deleteTodo(@Path("todoId") long id);
    }

    private ToDoWebAPI webAPI;

    public RetrofitRemoteToDoItemCRUDOperations() {
        Retrofit apiBase = new Retrofit.Builder().baseUrl("http:10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create()).build();
        webAPI = apiBase.create(ToDoWebAPI.class);
    }

    @Override
    public TodoItem createToDoItem(TodoItem item) {
        try {
            return webAPI.createToDo(item).execute().body();
        } catch (Exception e) {
            throw new RuntimeException("got Exception: " + e, e);
        }
    }

    @Override
    public List<TodoItem> readAllToDoItem() {
        try {
            return webAPI.readAllTodos().execute().body();
        } catch (Exception e) {
            throw new RuntimeException("got Exception: " + e, e);
        }
    }

    @Override
    public TodoItem readToDoItem(long id) {
        try {
            return webAPI.readTodo(id).execute().body();
        } catch (Exception e) {
            throw new RuntimeException("got Exception: " + e, e);
        }
    }

    @Override
    public TodoItem updateToDoItem(TodoItem itemToBeUpdated) {
        try {
            return webAPI.updateTodo(itemToBeUpdated.getId(), itemToBeUpdated).execute().body();
        } catch (Exception e) {
            throw new RuntimeException("got Exception: " + e, e);
        }
    }

    @Override
    public boolean deleteToDoItem(long id) {
        try {
            return webAPI.deleteTodo(id).execute().body();
        } catch (Exception e) {
            throw new RuntimeException("got Exception: " + e, e);
        }
    }
}
