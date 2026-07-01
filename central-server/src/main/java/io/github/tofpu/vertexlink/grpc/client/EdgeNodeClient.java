package io.github.tofpu.vertexlink.grpc.client;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.typesafe.config.Config;
import io.github.tofpu.vertexlink.config.type.ConfigValueTypeResolver;
import io.github.tofpu.vertexlink.config.value.ConfigValueConverter;
import io.github.tofpu.vertexlink.protos.ConfigurationRequest;
import io.github.tofpu.vertexlink.protos.EdgeNodeServiceGrpc;
import io.github.tofpu.vertexlink.protos.ValueType;
import io.github.tofpu.vertexlink.telemetry.NodeId;
import io.github.tofpu.vertexlink.util.ConversionUtil;
import io.github.tofpu.vertexlink.util.grpc.AbstractClient;
import io.grpc.StatusRuntimeException;

import static io.github.tofpu.vertexlink.protos.EdgeNodeServiceGrpc.EdgeNodeServiceBlockingStub;
import static io.github.tofpu.vertexlink.protos.EdgeNodeServiceGrpc.EdgeNodeServiceStub;

public class EdgeNodeClient extends AbstractClient<
        EdgeNodeServiceBlockingStub,
        EdgeNodeServiceStub> {
    private final NodeId nodeId;
    private Config config;

    public EdgeNodeClient(NodeId nodeId, String host, int port, Config config) {
        super(
                host,
                port,
                EdgeNodeServiceGrpc::newBlockingStub,
                EdgeNodeServiceGrpc::newStub
        );
        this.nodeId = nodeId;
        this.config = config;
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

    public void updateConfiguration(String path, Object value) {
        ValueType valueType = ConfigValueTypeResolver.resolver().resolveFrom(value);
        byte[] valueBytes = ConfigValueConverter.converter().convertObjectToBytes(valueType, value);
        ConfigurationRequest request = ConfigurationRequest.newBuilder()
                .setNodeId(ByteString.copyFrom(ConversionUtil.convertUUIDtoBytes(nodeId.uuid())))
                .setPath(path)
                .setValueType(valueType)
                .setValue(ByteString.copyFrom(valueBytes))
                .build();
        try {
            //noinspection ResultOfMethodCallIgnored
            blockingStub.updateConfiguration(request);
        } catch (StatusRuntimeException e) {
            super.handleException(e);
        }
    }

    public void updateConfig(Config newConfig) {
        this.config = newConfig;
    }

    public Config config() {
        return config;
    }
}
