package org.dieschnittstelle.mobile.android.skeleton.model;

import java.io.Serializable;
import java.util.Objects;

public class TodoItem implements Serializable {
    private String name;
    private String description;
    private boolean checked;
    private long id;

    public TodoItem() {

    }

    public TodoItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
        return id == todoItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
