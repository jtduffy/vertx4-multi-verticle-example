package com.nr.vertx.message;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public class PgRepository {
    SqlClient sqlClient;

    public PgRepository(SqlClient client) {
        this.sqlClient = client;
    }

    public Future<RowSet<Row>> createTable() {
        return sqlClient.query("create table test(id int primary key, name varchar(255))").execute();
    }

    public Future<RowSet<Row>> fetchAllRows() {
        return sqlClient.query("select * from test").execute();
    }

    public Future<RowSet<Row>> saveNewRow(Long id, String newName) {
        return sqlClient.preparedQuery("INSERT INTO test (id, name) VALUES ($1, $2)").execute(Tuple.of(id, newName));
    }
}
