package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

public interface IToDoItemCRUDOperations {
    //C:create
    public TodoItem createToDoItem(TodoItem item);

    //R:read
    public List<TodoItem> readAllToDoItem();

    public TodoItem readToDoItem(long id);

    //U:update
    public TodoItem updateToDoItem(TodoItem itemToBeUpdated);

    //D:delete
    public boolean deleteToDoItem(long id);
}
