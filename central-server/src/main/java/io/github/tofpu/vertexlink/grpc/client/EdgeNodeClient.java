package io.github.tofpu.vertexlink.grpc.client;

import com.google.protobuf.Empty;
import io.github.tofpu.vertexlink.protos.EdgeNodeServiceGrpc;
import io.github.tofpu.vertexlink.util.grpc.AbstractClient;
import io.grpc.StatusRuntimeException;

import static io.github.tofpu.vertexlink.protos.EdgeNodeServiceGrpc.EdgeNodeServiceBlockingStub;
import static io.github.tofpu.vertexlink.protos.EdgeNodeServiceGrpc.EdgeNodeServiceStub;

public class EdgeNodeClient extends AbstractClient<
        EdgeNodeServiceBlockingStub,
        EdgeNodeServiceStub> {
    public EdgeNodeClient(String host, int port) {
        super(
                host,
                port,
                EdgeNodeServiceGrpc::newBlockingStub,
                EdgeNodeServiceGrpc::newStub
        );
    }

    public PingResult ping() {
        try {
            //noinspection ResultOfMethodCallIgnored
            blockingStub.ping(Empty.newBuilder().build());
            return PingResult.SUCCESS;
        } catch (StatusRuntimeException e) {
            super.handleException(e);
            return PingResult.FAILURE;
        }
    }
}
