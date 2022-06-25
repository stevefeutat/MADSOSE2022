package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityOverviewListitemViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {
    public static final String LOGGER = "OverviewActivity";
    private ListView listView;
    private ArrayAdapter<TodoItem> listviewAdapter;
    private final List<TodoItem> listviewItems = new ArrayList<>();
    private FloatingActionButton addNewItemButton;
    private ActivityResultLauncher<Intent> detailviewForNewItemActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
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

        Arrays.asList("Leonie", "Steve").stream().map(name -> new TodoItem(name)).forEach(item ->
                this.addListItemView(item)
        );
    }

    @NonNull
    private ArrayAdapter<TodoItem> initialiseListviewAdapter() {
        return new ArrayAdapter<>(this, R.layout.activity_overview_listitem_view, listviewItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingListitemView, @NonNull ViewGroup parent) {
                //1.take the data to be shown
                TodoItem item = super.getItem(position);
                ActivityOverviewListitemViewBinding itemBinding= DataBindingUtil.inflate(getLayoutInflater(),R.layout.activity_overview_listitem_view,null,false);
                itemBinding.setItem(item);
                return itemBinding.getRoot();

            }
        };
    }

    private void initialiseActivityResultLaunchers() {

        detailviewForNewItemActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    Log.i(LOGGER, "ResultCode: " + result.getResultCode());
                    Log.i(LOGGER, "Data: " + result.getData());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        TodoItem item = (TodoItem) (result.getData() != null ? result.getData().getSerializableExtra(DetailviewActivity.ARG_ITEM) : null);
                        addListItemView(item);
                    }

                });
    }

    private void addListItemView(TodoItem item) {
//        TextView listItemView = (TextView) getLayoutInflater().inflate(R.layout.activity_overview_listitem_view, null);
//        listItemView.setText(item);
//        listView.addView(listItemView);
//        listItemView.setOnClickListener(v -> onListItemSelected(((TextView) v).getText().toString()));
        listviewAdapter.add(item);
    }

    private void onListItemSelected(TodoItem item) {
        Intent detailViewIntent = new Intent(this, DetailviewActivity.class);
        detailViewIntent.putExtra(DetailviewActivity.ARG_ITEM, item);
        startActivity(detailViewIntent);
    }

    private void onAddNewItem() {
        Intent detailViewIntentForAddNewItem = new Intent(this, DetailviewActivity.class);
//        startActivity(detailViewIntentForAddNewItem);
        detailviewForNewItemActivityLauncher.launch(detailViewIntentForAddNewItem);
    }

    private void showMessage(String msg) {
        Snackbar.make(listView, msg, Snackbar.LENGTH_INDEFINITE).show();
    }
}
