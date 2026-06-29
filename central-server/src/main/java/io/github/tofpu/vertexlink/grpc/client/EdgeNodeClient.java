package io.github.tofpu.vertexlink.grpc.client;

import com.google.protobuf.Empty;
import io.github.tofpu.vertexlink.protos.VertexLinkNodeServiceGrpc;
import io.github.tofpu.vertexlink.util.grpc.AbstractClient;
import io.grpc.StatusRuntimeException;

import static io.github.tofpu.vertexlink.protos.VertexLinkNodeServiceGrpc.VertexLinkNodeServiceBlockingStub;
import static io.github.tofpu.vertexlink.protos.VertexLinkNodeServiceGrpc.VertexLinkNodeServiceStub;

public class EdgeNodeClient extends AbstractClient<
        VertexLinkNodeServiceBlockingStub,
        VertexLinkNodeServiceStub> {
    public EdgeNodeClient(String host, int port) {
        super(
                host,
                port,
                VertexLinkNodeServiceGrpc::newBlockingStub,
                VertexLinkNodeServiceGrpc::newStub
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
