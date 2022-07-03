package org.dieschnittstelle.mobile.android.skeleton.model;


public interface IUserItemOperations {
    //user auth
    public boolean authenticateUser(UserItem user);

    //prepare
    public boolean prepare(UserItem user);

}
