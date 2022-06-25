package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleToDoItemCRUDOperations implements IToDoItemCRUDOperations {
    private static SimpleToDoItemCRUDOperations instance;
    private long idCount = 0;
    private final Map<Long, TodoItem> itemMap = new HashMap<>();

    public static SimpleToDoItemCRUDOperations getInstance() {
        if (instance == null) {
            instance = new SimpleToDoItemCRUDOperations();
        }
        return instance;
    }

    private SimpleToDoItemCRUDOperations() {
        Arrays.asList(
                        "Kind zur Schule", "DHL Paket abholen", "Zum Zahnarzt", "Kind abholen", "Einkaufen"
                )
                .forEach(name -> this.createToDoItem(new TodoItem(name)));
    }

    @Override
    public TodoItem createToDoItem(TodoItem item) {
        item.setId(idCount++);
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public List<TodoItem> readAllToDoItem() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public TodoItem readToDoItem(long id) {
        return itemMap.get(id);
    }

    @Override
    public TodoItem updateToDoItem(TodoItem itemToBeUpdated) {
        return itemMap.put(itemToBeUpdated.getId(), itemToBeUpdated);
    }

    @Override
    public boolean deleteToDoItem(long id) {
        itemMap.remove(id);
        return true;
    }
}
