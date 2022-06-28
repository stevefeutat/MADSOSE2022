package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

public class SyncedToDoItemCRUDOperations implements IToDoItemCRUDOperations {
    private IToDoItemCRUDOperations localOperations;
    private IToDoItemCRUDOperations remoteOperations;

    public SyncedToDoItemCRUDOperations(IToDoItemCRUDOperations localOperations, IToDoItemCRUDOperations remoteOperations) {
        this.localOperations = localOperations;
        this.remoteOperations = remoteOperations;
    }

    @Override
    public TodoItem createToDoItem(TodoItem item) {
        TodoItem created = localOperations.createToDoItem(item);
        remoteOperations.createToDoItem(item);
        return created;
    }

    @Override
    public List<TodoItem> readAllToDoItem() {
        return localOperations.readAllToDoItem();
    }

    @Override
    public TodoItem readToDoItem(long id) {
        return localOperations.readToDoItem(id);
    }

    @Override
    public TodoItem updateToDoItem(TodoItem itemToBeUpdated) {
        TodoItem updatedToDoItem = localOperations.updateToDoItem(itemToBeUpdated);
        remoteOperations.updateToDoItem(updatedToDoItem);
        return updatedToDoItem;
    }

    @Override
    public boolean deleteToDoItem(long id) {
        if (localOperations.deleteToDoItem(id)) {
            return remoteOperations.deleteToDoItem(id);
        } else {
            return false;
        }

    }
}
