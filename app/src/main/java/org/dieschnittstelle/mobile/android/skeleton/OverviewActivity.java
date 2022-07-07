package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OverviewActivity extends AppCompatActivity {
    public static final String LOGGER = "OverviewActivity";
    public static final Comparator<TodoItem> NAME_COMPARATOR = Comparator.comparing(TodoItem::getName);
    public static final Comparator<TodoItem> CHECKED_COMPARATOR = Comparator.comparing(TodoItem::isChecked);
    public static final Comparator<TodoItem> CHECKED_AND_DATE_COMPARATOR = Comparator.comparing(TodoItem::isChecked).reversed().thenComparing(TodoItem::getExpiryDate);
    public static final Comparator<TodoItem> FAVORITE_COMPARATOR = Comparator.comparing(TodoItem::isFavorit);
    public static final Comparator<TodoItem> FAVORITE_AND_DATE_COMPARATOR = Comparator.comparing(TodoItem::isFavorit).reversed().thenComparing(TodoItem::getExpiryDate);
    public static final Comparator<TodoItem> DATE_AND_FAVORITE_COMPARATOR = Comparator.comparing(TodoItem::getExpiryDate).reversed().thenComparing(TodoItem::isFavorit);

    private ListView listView;
    private ArrayAdapter<TodoItem> listviewAdapter;
    private final List<TodoItem> listviewItems = new ArrayList<>();
    private FloatingActionButton addNewItemButton;
    private IToDoItemCRUDOperations crudOperations;
    private ActivityResultLauncher<Intent> detailviewActivityLauncher;
    private ProgressBar progressBar;
    private MADAsyncOperationRunner operationRunner;
    private Comparator<TodoItem> currentComparator = CHECKED_COMPARATOR;
    private final Calendar myCalendar = Calendar.getInstance();

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
        crudOperations = ((ToDoItemApplication) getApplication()).getCrudOperations();
        operationRunner.run(
                () -> crudOperations.readAllToDoItem(),
                items -> {
                    items.forEach(item -> this.addListItemView(item));
                    sortItemsByComparator();
                });

    }

    @NonNull
    private ArrayAdapter<TodoItem> initialiseListviewAdapter() {
        return new ArrayAdapter<>(this, R.layout.activity_overview_listitem_view, listviewItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingListItemView, @NonNull ViewGroup parent) {
                //data we want to show
                TodoItem item = super.getItem(position);
                //the data binding object to show the data
                ActivityOverviewListitemViewBinding itemBinding = (existingListItemView != null
                        ? (ActivityOverviewListitemViewBinding) existingListItemView.getTag()
                        : DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_overview_listitem_view, null, false));

                itemBinding.setItem(item);
                itemBinding.setController(OverviewActivity.this);
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
                            || result.getResultCode() == DetailviewActivity.STATUS_UPDATED
                            || result.getResultCode() == DetailviewActivity.STATUS_DELETED) {
                        long itemId = result.getData() != null ?
                                result.getData().getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1)
                                : 0;

                        this.operationRunner.run(() -> crudOperations.readToDoItem(itemId),
                                item -> {


                                    if (result.getResultCode() == DetailviewActivity.STATUS_CREATED) {
                                        onToDoItemCreated(item);
                                    } else if (result.getResultCode() == DetailviewActivity.STATUS_UPDATED) {
                                        onToDoItemUpdated(item);
                                    } else if (result.getResultCode() == DetailviewActivity.STATUS_DELETED) {
                                        onToDoItemdeleted(itemId);

                                    }
                                });


                    }

                });
    }

    private void onToDoItemdeleted(long itemId) {
        TodoItem itemtoBeDeleted = null;

        for (TodoItem item : listviewItems) {
            if (item.getId() == itemId) {
                itemtoBeDeleted = item;
            }
        }
        listviewItems.remove(itemtoBeDeleted);
        listviewAdapter.remove(itemtoBeDeleted);
        this.listviewAdapter.notifyDataSetChanged();
        sortItemsByComparator();
    }

    private void onToDoItemCreated(TodoItem item) {
        this.addListItemView(item);
        sortItemsByComparator();
    }

    private void onToDoItemUpdated(TodoItem item) {
        TodoItem itemToBeUpdated = this.listviewAdapter.getItem(this.listviewAdapter.getPosition(item));
        itemToBeUpdated.setName(item.getName());
        itemToBeUpdated.setDescription(item.getDescription());
        itemToBeUpdated.setChecked(item.isChecked());
        itemToBeUpdated.setChecked(item.isFavorit());
        itemToBeUpdated.setExpiryDate(item.getExpiryDate());
//        this.listviewAdapter.notifyDataSetChanged();
        sortItemsByComparator();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sortAlpha) {

            this.currentComparator = NAME_COMPARATOR;
            sortItemsByComparator();
            return true;
        } else if (item.getItemId() == R.id.deleteAllItemsLocally) {
            deleteAllItemsLocally();
            return true;
        } else if (item.getItemId() == R.id.sortfavorite) {
            this.currentComparator = FAVORITE_COMPARATOR;
            sortItemsByComparator();
            return true;
        } else if (item.getItemId() == R.id.sortByFavoriteDate) {
            this.currentComparator = FAVORITE_AND_DATE_COMPARATOR;
            sortItemsByComparator();
            return true;
        } else if (item.getItemId() == R.id.sortByDateFavorite) {
            this.currentComparator = DATE_AND_FAVORITE_COMPARATOR;
            sortItemsByComparator();
            return true;
         } else if (item.getItemId() == R.id.sortChecked) {
        this.currentComparator = CHECKED_COMPARATOR;
        sortItemsByComparator();
        return true;
    }  else if (item.getItemId() == R.id.reload) {
            operationRunner.run(
                    () -> crudOperations.readAllToDoItem(),
                    items -> {
                        items.forEach(itemLoaded -> this.addListItemView(itemLoaded));
                        sortItemsByComparator();
                    });
        return true;
    } else {
            return super.onOptionsItemSelected(item);
        }

    }

    public void sortItemsByComparator() {
        this.listviewItems.sort(this.currentComparator);
        this.listviewAdapter.notifyDataSetChanged();
    }

    public void deleteAllItemsLocally() {
        this.listviewItems.clear();
        this.listviewAdapter.clear();
        this.listviewAdapter.notifyDataSetChanged();
    }

    public void onCheckedChangedInListview(TodoItem item) {
        this.operationRunner.run(() -> crudOperations.updateToDoItem(item), updatedItem -> {
            this.sortItemsByComparator();
            showMessage("item.isChecked()=" + item.isChecked());
        });

    }
}
