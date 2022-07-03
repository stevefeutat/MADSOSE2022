package org.dieschnittstelle.mobile.android.skeleton;

import android.view.View;

import org.dieschnittstelle.mobile.android.skeleton.model.UserItem;

public interface LoginviewModel {
    public UserItem getUserItem();
    public boolean onEmailInputChanged();
    public boolean onPasswordInputChanged();
    public void onLoginUser(UserItem item);
    public String getErrorStatus();
    public boolean checkEmailFieldInputComplete(View view, int actionId, boolean hasFocus, boolean isCalledFromOnFocusChange);
    public boolean checkPasswordFieldInputComplete(View view, int actionId, boolean hasFocus, boolean isCalledFromOnFocusChange);
}
