package com.nr.vertx.message;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public class PgRepository {
    // Schema for the test table:
    // -- DROP TABLE IF EXISTS public.test;
    //
    // CREATE TABLE IF NOT EXISTS public.test
    // (
    //    name character varying(255) COLLATE pg_catalog."default",
    //    id bigint NOT NULL DEFAULT nextval('test_id_seq'::regclass),
    //    CONSTRAINT test_pkey PRIMARY KEY (id)
    // )
    //
    // TABLESPACE pg_default;

    SqlClient sqlClient;
    public PgRepository(SqlClient client) {
        this.sqlClient = client;
    }

    public Future<RowSet<Row>> fetchAllRows() {
        return sqlClient.query("select * from test").execute();
    }

    public Future<Long> saveNewRow(String newName) {
        return sqlClient.preparedQuery("INSERT INTO test (name) VALUES ($1) RETURNING (id)").execute(Tuple.of(newName))
                .map(rs -> rs.iterator().next().getLong("id"));
    }
}
