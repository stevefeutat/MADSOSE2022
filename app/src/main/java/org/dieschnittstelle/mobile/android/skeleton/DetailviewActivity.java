package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityListitemDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.DetailviewViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;

public class DetailviewActivity extends AppCompatActivity implements DetailviewViewModel {
    public static String ARG_ITEM = "item";
    private FloatingActionButton saveChangeOnItemButton;
    private TodoItem item;
    private ActivityListitemDetailviewBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_listitem_detailview);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_listitem_detailview);
        saveChangeOnItemButton = findViewById(R.id.fab);

        this.item = (TodoItem) getIntent().getSerializableExtra(ARG_ITEM);
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
        returnIntent.putExtra(ARG_ITEM, this.item);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
