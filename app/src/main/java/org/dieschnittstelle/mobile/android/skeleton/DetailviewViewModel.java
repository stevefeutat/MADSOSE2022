package org.dieschnittstelle.mobile.android.skeleton;

import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;

public interface DetailviewViewModel {
    public TodoItem getItem();

    public void onSaveItem();
}
