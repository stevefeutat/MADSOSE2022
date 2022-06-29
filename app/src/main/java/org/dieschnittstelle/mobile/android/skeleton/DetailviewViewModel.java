package org.dieschnittstelle.mobile.android.skeleton;

import android.view.View;

import org.dieschnittstelle.mobile.android.skeleton.model.TodoItem;

public interface DetailviewViewModel {
    public TodoItem getItem();

    public void onSaveItem();

    public String getErrorStatus();

    public boolean onNameInputChanged();

    public boolean checkFieldInputComplete(View view, int actionId, boolean hasFocus, boolean isCalledFromOnFocusChange);
}
