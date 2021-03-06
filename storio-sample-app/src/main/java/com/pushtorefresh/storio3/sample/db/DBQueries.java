package com.pushtorefresh.storio3.sample.db;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.sqlite.queries.Query;

import static com.pushtorefresh.storio3.sample.db.tables.TweetsTable.TABLE;

public final class DBQueries {

    // Yep, with StorIO you can safely store queries as objects and reuse them, they are immutable
    @NonNull
    public static final Query QUERY_ALL =
            Query.builder()
                    .table(TABLE)
                    .build();
}
