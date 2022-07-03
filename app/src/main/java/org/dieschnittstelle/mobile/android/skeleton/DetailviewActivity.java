package org.dieschnittstelle.mobile.android.skeleton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityListitemDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailviewActivity extends AppCompatActivity implements DetailviewViewModel {
    public static String ARG_ITEM_ID = "itemId";
    public static int STATUS_CREATED = 42;
    public static int STATUS_UPDATED = -42;
    public static int STATUS_DELETED = -34;
    private DatePickerDialog picker;
    public String dateText;
    String myFormat;
    SimpleDateFormat sdf;

    private TodoItem item;
    private ActivityListitemDetailviewBinding binding;
    private IToDoItemCRUDOperations crudOperations;
    private MADAsyncOperationRunner operationRunner;
    private String errorStatus;
    private ActivityResultLauncher<Intent> selectContactLauncher;
    private Uri latestSelectedContactUri;
    private static int REQUEST_CONTACT_PERMISSIONS_REQUEST_CODE = 1;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dateText = sdf.format(myCalendar.getTime());
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_listitem_detailview);
        this.selectContactLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        onContactSelected(result.getData());
                    }
                }
        );
        this.crudOperations = ((ToDoItemApplication) getApplication()).getCrudOperations();
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

    @Override
    public String getErrorStatus() {
        return errorStatus;
    }

    @Override
    public boolean onNameInputChanged() {
        if (this.errorStatus != null) {
            this.errorStatus = null;
            this.binding.setViewModel(this);
        }
        return true;
    }

    @Override
    public boolean checkFieldInputComplete(View view, int actionId, boolean hasFocus, boolean isCalledFromOnFocusChange) {

//        Log.i("LOGGER", "checkFielInputComplete");
        if (isCalledFromOnFocusChange ?
                !hasFocus
                : actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            if (item.getName().length() < 5) {
                errorStatus = "name too short";
                this.binding.setViewModel(this);
//                Log.i("LOGGER", errorStatus);
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent returnIntent = new Intent();
        if (item.getItemId() == R.id.selectContact) {
            selectContact();
            return true;
        } else if (item.getItemId() == R.id.deleteItem) {
            this.operationRunner.run(() -> crudOperations.deleteToDoItem(this.item.getId()),
                    (deleted) -> {
                        Log.i("LOGGER", "deleteToDoItem" + deleted);
                        returnIntent.putExtra(ARG_ITEM_ID, this.item.getId());

                        setResult(STATUS_DELETED, returnIntent);
                        this.binding.setViewModel(this);
                        finish();
                    });
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACT_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (latestSelectedContactUri != null) {
                    showContactDetails(latestSelectedContactUri);
                } else {
                    Toast.makeText(this, "Cannot continue:no contact", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Contact cannot be accessed", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void selectContact() {
        Log.i("LOGGER", "selectContact");
        Intent selectedContactIntent =
                new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        this.selectContactLauncher.launch(selectedContactIntent);
    }

    public void onContactSelected(Intent result) {

        Log.i("LOGGER", "onContactSelected" + result);
        showContactDetails(result.getData());
    }

    public void showContactDetails(Uri contactUri) {
        int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            latestSelectedContactUri = contactUri;
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT_PERMISSIONS_REQUEST_CODE);
            return;

        }
        Cursor cursor = getContentResolver()
                .query(contactUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            Log.i("LOGGER", "moveToFirst " + cursor);
            int contactNamePosition = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String contactName = cursor.getString(contactNamePosition);
            Log.i("LOGGER", "contactName " + contactName);
            int internalIdPosition = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            long internalId = cursor.getLong(internalIdPosition);
            Log.i("LOGGER", "internalId " + internalId);
            showContactDetailsForInternalId(internalId);
        }
    }

    public void showContactDetailsForInternalId(long internalId) {
        Cursor cursor = getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI, null,
                        "_id=?", new String[]{String.valueOf(internalId)}, null);
        if (cursor.moveToFirst()) {
            int contactDisplayNamePosition = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String displayName = cursor.getString(contactDisplayNamePosition);
            Log.i("LOGGER", "displayName " + displayName);

        } else {
            Toast.makeText(this, "No contact found with internalId", Toast.LENGTH_LONG).show();
            return;
        }
        cursor =getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,"_id=?", new String[]{String.valueOf(internalId)}, null);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String number= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            @SuppressLint("Range") int numberType= cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
        }
    }

    public void selectDate() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date date = myCalendar.getTime();
                        dateText=sdf.format(date);
                        item.setExpiryDate(sdf.format(date));

                    }
                }, year, month, day);
        picker.show();
        this.binding.setViewModel(this);

    }
}
