package io.github.tofpu.vertexlink.util.grpc;

import io.grpc.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SimpleServer<T extends BindableService> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SimpleServer.class);
    private final int port;
    private final io.grpc.Server server;

    /** Create a RouteGuide server listening on {@code port}. */
    public SimpleServer(int port, T service) {
        this(Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()), port, service);
    }

    /** Create a RouteGuide server using serverBuilder as a base */
    public SimpleServer(ServerBuilder<?> serverBuilder, int port, T service) {
        this.port = port;
        this.server = serverBuilder.addService(service)
                .build();
    }

    public void start() throws IOException {
        server.start();
        log.info("Server started, listening on {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                SimpleServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    /** Stop serving requests and shutdown resources. */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
