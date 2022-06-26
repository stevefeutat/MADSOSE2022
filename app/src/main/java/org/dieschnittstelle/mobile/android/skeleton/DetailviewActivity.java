package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityListitemDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.DetailviewViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SimpleToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;

public class DetailviewActivity extends AppCompatActivity implements DetailviewViewModel {
    public static String ARG_ITEM_ID = "itemId";
    private TodoItem item;
    private ActivityListitemDetailviewBinding binding;
    private IToDoItemCRUDOperations crudOperations;
    private MADAsyncOperationRunner operationRunner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_listitem_detailview);
        this.crudOperations = SimpleToDoItemCRUDOperations.getInstance();

        this.operationRunner = new MADAsyncOperationRunner(this, null);

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        if (itemId != -1) {
            operationRunner.run(() -> this.crudOperations.readToDoItem(itemId),
                    item -> {
                        this.item = item;
                        this.binding.setViewModel(this);
                    }
            );
//            this.item = this.crudOperations.readToDoItem(itemId);
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
        this.item = (item.getId() > 0
                ? crudOperations.updateToDoItem(this.item)
                : crudOperations.createToDoItem(this.item));
        returnIntent.putExtra(ARG_ITEM_ID, this.item.getId());

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
