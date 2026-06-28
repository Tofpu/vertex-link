package io.github.tofpu.vertexlink.util.grpc;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.function.Function;

public abstract class AbstractClient<AbstractBlockingStub, AbstractAsyncStub> {
    private static final Logger log = LoggerFactory.getLogger(AbstractClient.class);
    protected final ManagedChannel channel;
    protected final AbstractBlockingStub blockingStub;
    protected final AbstractAsyncStub asyncStub;

    public AbstractClient(
            String host,
            int port,
            Function<Channel, AbstractBlockingStub> channelBlockingStubFunction,
            Function<Channel, AbstractAsyncStub> channelAsyncStubFunction
    ) {
        this(
                ManagedChannelBuilder.forAddress(host, port).usePlaintext(),
                channelBlockingStubFunction,
                channelAsyncStubFunction
        );
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public AbstractClient(
            ManagedChannelBuilder<?> channelBuilder,
            Function<Channel, AbstractBlockingStub> channelBlockingStubFunction,
            Function<Channel, AbstractAsyncStub> channelAsyncStubFunction
    ) {
        this.channel = channelBuilder.build();
        this.blockingStub = channelBlockingStubFunction.apply(this.channel);
        this.asyncStub = channelAsyncStubFunction.apply(this.channel);
    }

    protected void handleException(StatusRuntimeException e) {
        if (isConnectionException(e)) {
            log.error("Failed to upload telemetry: Cannot reach the server (Network/Connection issue).");
        } else {
            log.info("Failed to upload telemetry. RPC failed: {}", e.getStatus());
        }
    }

    private boolean isConnectionException(StatusRuntimeException e) {
        // Connection drops and timeouts almost always fall under UNAVAILABLE
        if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof ConnectException) {
                    return true;
                }
                cause = cause.getCause(); // Move down the causal chain
            }
        }
        return false;
    }
}
