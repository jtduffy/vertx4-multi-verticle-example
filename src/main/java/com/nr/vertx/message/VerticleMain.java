package com.nr.vertx.message;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class VerticleMain {
    public static void main(String [] args) {
        Vertx vertx = Vertx.vertx();
        PgConnectOptions options = new PgConnectOptions()
                .setPort(5432)
                .setHost("localhost")
                .setDatabase("jduffy")
                .setUser("jduffy")
                .setPassword("");

        SqlClient client = PgBuilder
                .client()
                .with(new PoolOptions().setMaxSize(5))
                .connectingTo(options)
                .using(vertx)
                .build();

        PgRepository pgRepository = new PgRepository(client);

        vertx.deployVerticle(new MessageReceiverVerticle());
        vertx.deployVerticle(new SampleTickVerticle());
        vertx.deployVerticle(new HttpServerVerticle(pgRepository));
    }
}
