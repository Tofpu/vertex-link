package io.github.tofpu.vertexlink.grpc.server;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeNodeServiceGrpc extends io.github.tofpu.vertexlink.protos.EdgeNodeServiceGrpc.EdgeNodeServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(EdgeNodeServiceGrpc.class);

    @Override
    public void ping(Empty request, StreamObserver<Empty> responseObserver) {
        log.info("Received ping request");
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
