package com.nr.vertx.message;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;

import java.util.Random;

public class HttpServerVerticle extends AbstractVerticle {
    private PgRepository pgRepository;
    private Random rnd = new Random();

    public HttpServerVerticle(PgRepository pgRepository) {
        this.pgRepository = pgRepository;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        final Router router = Router.router(vertx);
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.end("Hello from non-clustered vertx app");
        });
        router.post("/send/:message").handler(this::sendMessage);
        router.get("/db").handler(this::fetchFromPostgres);
        router.post("/db/:name").handler(this::saveNewRow);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(config()
                        .getInteger("http.server.port", 8080), result -> {
                    if (result.succeeded()) {
                        System.out.println("HttpServerVerticle running on 8080");
                        System.out.println("use GET / to get a welcome message");
                        System.out.println("use POST /send/:message to send a message");
                        System.out.println("use GET /db to fetch data from Postgres DB; see PgRepository.java for schema " +
                                "details; see VerticleMain.java to change connection options");
                        System.out.println("use POST /db/:name to save a new row in the test table");
                        startPromise.complete();
                    } else {
                        System.out.println("Could not start a HTTP server " +  result.cause());
                        startPromise.fail(result.cause());
                    }
                });
    }

    private void sendMessage(RoutingContext routingContext) {
        final EventBus eventBus = vertx.eventBus();
        final String message = routingContext.request().getParam("message");
        eventBus.request("inbox", message, reply -> {
            if (reply.succeeded()) {
                System.out.println("Received reply: " + reply.result().body());
            } else {
                System.out.println("No reply    " + reply.cause());
            }
        });
        routingContext.response().end("Sent msg: " + message);
    }

    private void fetchFromPostgres(RoutingContext routingContext) {
        pgRepository.fetchAllRows().onComplete(ar -> {
            if (ar.succeeded()) {
                StringBuilder payload = new StringBuilder();
                for (Row r : ar.result()) {
                    payload.append(r.getLong("id")).append(" ").append(r.getString("name")).append("\n");
                }
                routingContext.response().end(payload.toString());
            } else {
                routingContext.response().end("Failed to fetch data from Postgres");
            }
        });
    }

    private void saveNewRow(RoutingContext routingContext) {
        Long id = rnd.nextLong();
        pgRepository.saveNewRow(id, routingContext.request().getParam("name")).onComplete(ar -> {
            if (ar.succeeded()) {
                routingContext.response().end("Saved new row with id: " + id + "\n");
            } else {
                routingContext.response().end("Failed to save data to Postgres");
            }
        });
    }
}
