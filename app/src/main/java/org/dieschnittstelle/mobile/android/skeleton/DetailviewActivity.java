package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityListitemDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RetrofitRemoteToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;

public class DetailviewActivity extends AppCompatActivity implements DetailviewViewModel {
    public static String ARG_ITEM_ID = "itemId";
    public static int STATUS_CREATED = 42;
    public static int STATUS_UPDATED = -42;

    private TodoItem item;
    private ActivityListitemDetailviewBinding binding;
    private IToDoItemCRUDOperations crudOperations;
    private MADAsyncOperationRunner operationRunner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_listitem_detailview);
        this.crudOperations = ((ToDoItemApplication) getApplication()).getCrudOperations();
        //new RetrofitRemoteToDoItemCRUDOperations();//new RoomLocalToDoItemCRUDOperations(this.getApplicationContext());//SimpleToDoItemCRUDOperations.getInstance();

        this.operationRunner = new MADAsyncOperationRunner(this, null);

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        if (itemId != -1) {
            operationRunner.run(() -> this.crudOperations.readToDoItem(itemId),
                    item -> {
                        this.item = item;
                        this.binding.setViewModel(this);
                    }
            );
        }
        if (this.item == null) {
            this.item = new TodoItem();
        }
        this.binding.setViewModel(this);
    }

    public TodoItem getItem() {
        return this.item;
    }

    public void onSaveItem() {
        Intent returnIntent = new Intent();
        int resultCode = item.getId() > 0 ? STATUS_UPDATED : STATUS_CREATED;
        operationRunner.run(() ->
                        item.getId() > 0
                                ? crudOperations.updateToDoItem(item)
                                : crudOperations.createToDoItem(item),
                item -> {
                    this.item = item;
                    returnIntent.putExtra(ARG_ITEM_ID, this.item.getId());

                    setResult(resultCode, returnIntent);
                    finish();
                });


    }
}
