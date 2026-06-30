package io.github.tofpu.vertexlink.grpc.server;

import com.google.protobuf.Empty;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import io.github.tofpu.vertexlink.config.ConfigService;
import io.github.tofpu.vertexlink.config.value.ConfigValueConverter;
import io.github.tofpu.vertexlink.protos.ConfigurationRequest;
import io.github.tofpu.vertexlink.protos.ValueType;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeNodeServiceGrpc extends io.github.tofpu.vertexlink.protos.EdgeNodeServiceGrpc.EdgeNodeServiceImplBase {
    private final ConfigService configService;
    private static final Logger log = LoggerFactory.getLogger(EdgeNodeServiceGrpc.class);

    public EdgeNodeServiceGrpc(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void ping(Empty request, StreamObserver<Empty> responseObserver) {
        log.info("Received ping request");
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void updateConfiguration(ConfigurationRequest request, StreamObserver<Empty> responseObserver) {
        String path = request.getPath();
        ValueType valueType = request.getValueType();
        Object value = ConfigValueConverter.converter().convertBytesToObject(valueType, request.getValue().toByteArray());

        Config config = configService.config();
        config = config.withValue(path, ConfigValueFactory.fromAnyRef(value));

        configService.updateConfig(config);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
