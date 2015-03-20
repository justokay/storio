package com.pushtorefresh.storio.db.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class PreparedGetCursor extends PreparedGet<Cursor> {

    PreparedGetCursor(@NonNull StorIODb storIODb, @NonNull Query query, @NonNull GetResolver getResolver) {
        super(storIODb, query, getResolver);
    }

    PreparedGetCursor(@NonNull StorIODb storIODb, @NonNull RawQuery rawQuery, @NonNull GetResolver getResolver) {
        super(storIODb, rawQuery, getResolver);
    }

    @NonNull public Cursor executeAsBlocking() {
        if (query != null) {
            return getResolver.performGet(storIODb, query);
        } else if (rawQuery != null) {
            return getResolver.performGet(storIODb, rawQuery);
        } else {
            throw new IllegalStateException("Please specify query");
        }
    }

    @NonNull @Override public Observable<Cursor> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override public void call(Subscriber<? super Cursor> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }

    @NonNull @Override public Observable<Cursor> createObservableStream() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservableStream()");

        final Set<String> tables;

        if (query != null) {
            tables = new HashSet<>(1);
            tables.add(query.table);
        } else if (rawQuery != null) {
            tables = rawQuery.tables;
        } else {
            throw new IllegalStateException("Please specify query");
        }

        if (tables != null && !tables.isEmpty()) {
            return storIODb
                    .observeChangesInTables(tables)
                    .map(new Func1<Changes, Cursor>() { // each change triggers executeAsBlocking
                        @Override public Cursor call(Changes changes) {
                            return executeAsBlocking();
                        }
                    })
                    .startWith(executeAsBlocking()); // start stream with first query result
        } else {
            return createObservable();
        }
    }

    public static class Builder {

        @NonNull private final StorIODb storIODb;

        private Query query;
        private RawQuery rawQuery;
        private GetResolver getResolver;

        Builder(@NonNull StorIODb storIODb) {
            this.storIODb = storIODb;
        }

        /**
         * Specifies {@link Query} for Get Operation
         *
         * @param query query
         * @return builder
         */
        @NonNull public Builder withQuery(@NonNull Query query) {
            this.query = query;
            return this;
        }

        /**
         * Specifies {@link RawQuery} for Get Operation, you can use it for "joins" and same constructions which are not allowed in {@link Query}
         *
         * @param rawQuery query
         * @return builder
         */
        @NonNull public Builder withQuery(@NonNull RawQuery rawQuery) {
            this.rawQuery = rawQuery;
            return this;
        }

        /**
         * Optional: Specifies {@link GetResolver} for Get Operation which allows you to customize behavior of Get Operation
         *
         * @param getResolver get resolver
         * @return builder
         */
        @NonNull public Builder withGetResolver(@NonNull GetResolver getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Prepares Get Operation
         * @return {@link PreparedGetCursor} instance
         */
        @NonNull public PreparedOperationWithReactiveStream<Cursor> prepare() {
            if (getResolver == null) {
                getResolver = DefaultGetResolver.INSTANCE;
            }

            if (query != null) {
                return new PreparedGetCursor(storIODb, query, getResolver);
            } else if (rawQuery != null) {
                return new PreparedGetCursor(storIODb, rawQuery, getResolver);
            } else {
                throw new IllegalStateException("Please specify query");
            }
        }
    }
}
