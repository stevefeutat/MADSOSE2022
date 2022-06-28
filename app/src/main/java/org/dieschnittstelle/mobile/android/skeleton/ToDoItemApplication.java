package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import org.dieschnittstelle.mobile.android.skeleton.model.CachedToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RetrofitRemoteToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RoomLocalToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SyncedToDoItemCRUDOperations;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ToDoItemApplication extends Application {
    private IToDoItemCRUDOperations crudOperations;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (checkConnectivity().get()) {
                IToDoItemCRUDOperations operations = new SyncedToDoItemCRUDOperations(
                        new RoomLocalToDoItemCRUDOperations(this),
                        new RetrofitRemoteToDoItemCRUDOperations());
                Toast.makeText(this, "Using Synced data access ...", Toast.LENGTH_SHORT).show();
                this.crudOperations = new CachedToDoItemCRUDOperations(operations);
            } else {
                this.crudOperations = new CachedToDoItemCRUDOperations(new RoomLocalToDoItemCRUDOperations(this));
                Toast.makeText(this, "Using local data access ...", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            throw new RuntimeException("Got exception trying to get future boolean for connectivity");
        }
        //new RetrofitRemoteToDoItemCRUDOperations();
        // new RoomLocalToDoItemCRUDOperations(this);
        // SimpleToDoItemCRUDOperations.getInstance();
    }

    public IToDoItemCRUDOperations getCrudOperations() {
        return this.crudOperations;
    }

    public Future<Boolean> checkConnectivity() {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Thread(() -> {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://10.0.2.2:8080/api/todos").openConnection();
                httpURLConnection.setReadTimeout(1000);
                httpURLConnection.setConnectTimeout(1000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                httpURLConnection.getInputStream();
                result.complete(true);
            } catch (Exception e) {
                Log.e("IToDoItemCRUDOperations", "Got exception trying to check connectivity: " + e, e);
                result.complete(false);
            }
        }).start();

        return result;
    }

}
