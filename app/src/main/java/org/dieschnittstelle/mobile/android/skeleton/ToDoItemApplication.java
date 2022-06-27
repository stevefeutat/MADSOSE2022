package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;

import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RetrofitRemoteToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RoomLocalToDoItemCRUDOperations;

public class ToDoItemApplication extends Application {
    private IToDoItemCRUDOperations crudOperations;

    @Override
    public void onCreate() {
        super.onCreate();
        //new RetrofitRemoteToDoItemCRUDOperations();
        // new RoomLocalToDoItemCRUDOperations(this);
        // SimpleToDoItemCRUDOperations.getInstance();
        this.crudOperations = new RetrofitRemoteToDoItemCRUDOperations();
    }

    public IToDoItemCRUDOperations getCrudOperations() {
        return this.crudOperations;
    }
}
