### vertx4-multi-verticle-example

This project demonstrates a simple Vertx 4 (specifically v4.5.9) project with three verticles deployed within the
same Vertx instance.

- `SampleTickVerticle` - Uses the Vertx `setPeriodic` call to create a timer that simple outputs to stdout every 10 seconds
- `HttpServerVerticle` - Creates a Vertx HTTP server with two routes:
    - `GET /` - Responds with a `Hello from non-clustered vertx app` message
    - `POST /send/:message` - Extracts the string from the `:message` path variable and delivers it to the `MessageReceiverVerticle` via the Vertx event bus
    - `GET /db` - Fetch all records from test PG database
    - `POST /db/:name` - Add a new row to test DB
- `MessageReceiverVerticle` - Receives messages from the `HttpServerVerticle` via the event bus, and posts a reply back to the message sender
- `VerticleMain` - Contains the `main` method of the application and starts up the other three verticles.

See the `main` method in the `VerticleMain` class to configure the PgConnectOptions and the `PgRepository` class for the DDL to create the test table.


