package org.dieschnittstelle.mobile.android.skeleton.model;

import android.content.Context;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;

public class RoomLocalToDoItemCRUDOperations implements IToDoItemCRUDOperations {
    @Dao
    public interface ToDoItemDao {
        @Query("select * from TodoItem")
        public List<TodoItem> readAll();

        @Query("select * from TodoItem where id==(:id)")
        public TodoItem readById(long id);

        @Insert
        public long create(TodoItem item);

        @Update
        public void update(TodoItem item);

        @Delete
        public  void delete(TodoItem item);
    }

    @Database(entities = {TodoItem.class}, version = 1)
    public static abstract class ToDoItemDatabase extends RoomDatabase {
        public abstract ToDoItemDao getDao();
    }

    private final ToDoItemDatabase db;

    public RoomLocalToDoItemCRUDOperations(Context context) {
        db = Room.databaseBuilder(context, ToDoItemDatabase.class, "todoitems").build();
        Log.i("RoomLocalToDoItemCRUDOperations", "db: " + db);
    }

    @Override
    public TodoItem createToDoItem(TodoItem item) {
        long id = db.getDao().create(item);
        item.setId(id);
        return item;
    }

    @Override
    public List<TodoItem> readAllToDoItem() {
        return db.getDao().readAll();
    }

    @Override
    public TodoItem readToDoItem(long id) {
        return db.getDao().readById(id);
    }

    @Override
    public TodoItem updateToDoItem(TodoItem itemToBeUpdated) {
        db.getDao().update(itemToBeUpdated);
        return itemToBeUpdated;
    }

    @Override
    public boolean deleteToDoItem(long id) {
        db.getDao().delete(readToDoItem(id));
        return true;
    }
}
