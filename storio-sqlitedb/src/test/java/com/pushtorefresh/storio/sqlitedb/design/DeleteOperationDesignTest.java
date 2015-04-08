package com.pushtorefresh.storio.sqlitedb.design;

import com.pushtorefresh.storio.sqlitedb.operation.delete.DeleteResults;
import com.pushtorefresh.storio.sqlitedb.operation.delete.DeleteResult;
import com.pushtorefresh.storio.sqlitedb.query.DeleteQuery;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class DeleteOperationDesignTest extends OperationDesignTest {

    @Test public void deleteObjectBlocking() {
        User user = newUser();

        DeleteResult deleteResult = storIOSQLiteDb()
                .delete()
                .object(user)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void deleteObjectObservable() {
        User user = newUser();

        Observable<DeleteResult> deleteResultObservable = storIOSQLiteDb()
                .delete()
                .object(user)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .createObservable();
    }

    @Test public void deleteCollectionOfObjectsBlocking() {
        List<User> users = new ArrayList<User>();

        DeleteResults<User> deleteResult = storIOSQLiteDb()
                .delete()
                .objects(users)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void deleteCollectionOfObjectsObservable() {
        List<User> users = new ArrayList<User>();

        Observable<DeleteResults<User>> deleteResultObservable = storIOSQLiteDb()
                .delete()
                .objects(users)
                .withMapFunc(User.MAP_TO_DELETE_QUERY)
                .prepare()
                .createObservable();
    }

    @Test public void deleteByQueryBlocking() {
        DeleteResult deleteResult = storIOSQLiteDb()
                .delete()
                .byQuery(new DeleteQuery.Builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void deleteByQueryObservable() {
        Observable<DeleteResult> deleteResultObservable = storIOSQLiteDb()
                .delete()
                .byQuery(new DeleteQuery.Builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }
}