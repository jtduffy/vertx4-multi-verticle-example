package com.nr.vertx.message;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.testcontainers.containers.PostgreSQLContainer;

public class VerticleMain {
    public static void main(String [] args) {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>();
        postgres.start();

        Vertx vertx = Vertx.vertx();
        PgConnectOptions options = new PgConnectOptions()
                .setPort(postgres.getMappedPort(5432))
                .setHost(postgres.getHost())
                .setDatabase(postgres.getDatabaseName())
                .setUser(postgres.getUsername())
                .setPassword(postgres.getPassword());

        SqlClient client = Pool.pool(vertx, options, new PoolOptions().setMaxSize(4));
//                .client()
//                .with(new PoolOptions().setMaxSize(5))
//                .connectingTo(options)
//                .using(vertx)
//                .build();

        PgRepository pgRepository = new PgRepository(client);
        pgRepository.createTable().onSuccess(result -> {
            System.out.println("Table 'test' created");
            vertx.deployVerticle(new MessageReceiverVerticle());
            vertx.deployVerticle(new SampleTickVerticle());
            vertx.deployVerticle(new HttpServerVerticle(pgRepository));
        });
    }
}
