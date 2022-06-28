package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedToDoItemCRUDOperations implements IToDoItemCRUDOperations {
    private final Map<Long, TodoItem> itemMap = new HashMap<>();
    private IToDoItemCRUDOperations realCRUDOperations;

    public CachedToDoItemCRUDOperations(IToDoItemCRUDOperations realCRUDOperations) {
        this.realCRUDOperations = realCRUDOperations;
    }

    @Override
    public TodoItem createToDoItem(TodoItem item) {
        TodoItem created = realCRUDOperations.createToDoItem(item);
        itemMap.put(created.getId(), created);
        return created;
    }

    @Override
    public List<TodoItem> readAllToDoItem() {
        if (itemMap.size() == 0) {
            realCRUDOperations.readAllToDoItem().forEach((item) -> {
                itemMap.put(item.getId(), item);
            });
        }
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public TodoItem readToDoItem(long id) {
        if (itemMap.containsKey(id)) {
            TodoItem item = realCRUDOperations.readToDoItem(id);
            if (item != null) {
                itemMap.put(item.getId(), item);
            }
            return item;
        }
        return itemMap.get(id);
    }

    @Override
    public TodoItem updateToDoItem(TodoItem itemToBeUpdated) {
        TodoItem item = realCRUDOperations.updateToDoItem(itemToBeUpdated);
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public boolean deleteToDoItem(long id) {
        if (realCRUDOperations.deleteToDoItem(id)) {
            itemMap.remove(id);
            return true;
        } else {
            return false;
        }
    }
}
