package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityOverviewListitemViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RetrofitRemoteToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RoomLocalToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SimpleToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;

import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {
    public static final String LOGGER = "OverviewActivity";
    private ListView listView;
    private ArrayAdapter<TodoItem> listviewAdapter;
    private final List<TodoItem> listviewItems = new ArrayList<>();
    private FloatingActionButton addNewItemButton;
    private IToDoItemCRUDOperations crudOperations;
    private ActivityResultLauncher<Intent> detailviewActivityLauncher;
    private ProgressBar progressBar;
    private MADAsyncOperationRunner operationRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        operationRunner = new MADAsyncOperationRunner(this, progressBar);
        listviewAdapter = initialiseListviewAdapter();
        listView.setAdapter(listviewAdapter);
        listView.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {
                    TodoItem selectedItem = listviewAdapter.getItem(position);
                    onListItemSelected(selectedItem);
                }
        );
        addNewItemButton = findViewById(R.id.fab);
        initialiseActivityResultLaunchers();

        addNewItemButton.setOnClickListener(v -> {
            onAddNewItem();
        });
        crudOperations = new RetrofitRemoteToDoItemCRUDOperations();//new RoomLocalToDoItemCRUDOperations(this.getApplicationContext());//SimpleToDoItemCRUDOperations.getInstance();
        operationRunner.run(
                () -> crudOperations.readAllToDoItem(),
                items -> {
                    items.forEach(item -> this.addListItemView(item));
                });

    }

    @NonNull
    private ArrayAdapter<TodoItem> initialiseListviewAdapter() {
        return new ArrayAdapter<>(this, R.layout.activity_overview_listitem_view, listviewItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingListitemView, @NonNull ViewGroup parent) {
                //data we want to show
                TodoItem item = super.getItem(position);
                //the data binding object to show the data
                ActivityOverviewListitemViewBinding itemBinding = (existingListitemView != null
                        ? (ActivityOverviewListitemViewBinding) existingListitemView.getTag()
                        : DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_overview_listitem_view, null, false));

                itemBinding.setItem(item);
                //the view in which the data is shown
                View itemView = itemBinding.getRoot();
                itemView.setTag(itemBinding);
                return itemView;

            }
        };
    }

    private void initialiseActivityResultLaunchers() {

        detailviewActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    Log.i(LOGGER, "ResultCode: " + result.getResultCode());
                    Log.i(LOGGER, "Data: " + result.getData());
                    if (result.getResultCode() == DetailviewActivity.STATUS_CREATED
                            || result.getResultCode() == DetailviewActivity.STATUS_UPDATED) {
                        long itemId = result.getData() != null ?
                                result.getData().getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1)
                                : 0;
                        this.operationRunner.run(() -> crudOperations.readToDoItem(itemId),
                                item -> {
                                    if (result.getResultCode() == DetailviewActivity.STATUS_CREATED) {
                                        onToDoItemCreated(item);
                                    } else if (result.getResultCode() == DetailviewActivity.STATUS_UPDATED) {
                                        onToDoItemUpdated(item);
                                    }
                                });
                    }

                });
    }

    private void onToDoItemCreated(TodoItem item) {
        this.addListItemView(item);
    }

    private void onToDoItemUpdated(TodoItem item) {
        TodoItem itemToBeUpdated = this.listviewAdapter.getItem(this.listviewAdapter.getPosition(item));
        itemToBeUpdated.setName(item.getName());
        itemToBeUpdated.setDescription(item.getDescription());
        itemToBeUpdated.setChecked(item.isChecked());
        this.listviewAdapter.notifyDataSetChanged();
    }

    private void addListItemView(TodoItem item) {
        listviewAdapter.add(item);
        listView.setSelection(listviewAdapter.getPosition(item));
    }

    private void onListItemSelected(TodoItem item) {
        Intent detailViewIntent = new Intent(this, DetailviewActivity.class);
        detailViewIntent.putExtra(DetailviewActivity.ARG_ITEM_ID, item.getId());
        detailviewActivityLauncher.launch(detailViewIntent);
    }

    private void onAddNewItem() {
        Intent detailViewIntentForAddNewItem = new Intent(this, DetailviewActivity.class);
        detailviewActivityLauncher.launch(detailViewIntentForAddNewItem);
    }

    private void showMessage(String msg) {
        Snackbar.make(listView, msg, Snackbar.LENGTH_INDEFINITE).show();
    }
}
