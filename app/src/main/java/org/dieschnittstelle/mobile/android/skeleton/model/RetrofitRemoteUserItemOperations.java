package org.dieschnittstelle.mobile.android.skeleton.model;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.PUT;

public class RetrofitRemoteUserItemOperations implements IUserItemOperations {
    public static final String WEBAPP_API_BASEURL_LOCALHOST_FROM_ANDROIDSTUDIO_EMULATOR = "http://10.0.2.2:8080/api/";

    public interface UserWebAPI {
        @PUT("users/auth")
        public boolean authenticateUser(UserItem user);

        @PUT("users/prepare")
        public boolean prepare(UserItem user);

    }

    private UserWebAPI webAPI;

    public RetrofitRemoteUserItemOperations() {
        Retrofit apiBase = new Retrofit.Builder().baseUrl(WEBAPP_API_BASEURL_LOCALHOST_FROM_ANDROIDSTUDIO_EMULATOR)
                .addConverterFactory(GsonConverterFactory.create()).build();
        webAPI = apiBase.create(RetrofitRemoteUserItemOperations.UserWebAPI.class);
    }

    @Override
    public boolean authenticateUser(UserItem user) {
        try {
            return webAPI.authenticateUser(user);
        }catch (Exception exception){
            throw new RuntimeException("got Exception: " + exception, exception);
        }

    }

    @Override
    public boolean prepare(UserItem user) {
        try {
            return webAPI.prepare(user);
        }catch (Exception exception){
            throw new RuntimeException("got Exception: " + exception, exception);
        }
    }
}
