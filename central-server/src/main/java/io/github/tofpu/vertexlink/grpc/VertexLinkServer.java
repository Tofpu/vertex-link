package io.github.tofpu.vertexlink.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VertexLinkServer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VertexLinkServer.class);
    private final int port;
    private final Server server;

    /** Create a RouteGuide server listening on {@code port}. */
    public VertexLinkServer(int port, VertexLinkService<?> bindableService) {
        this(Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()), port, bindableService);
    }

    /** Create a RouteGuide server using serverBuilder as a base */
    public VertexLinkServer(ServerBuilder<?> serverBuilder, int port, VertexLinkService<?> bindableService) {
        this.port = port;
        this.server = serverBuilder.addService(bindableService)
                .build();
    }

    public void start() throws IOException {
        server.start();
//        logger.info("Server started, listening on " + port);
        log.info("Server started, listening on {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                VertexLinkServer.this.stop();
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
