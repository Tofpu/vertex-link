package io.github.tofpu.vertexlink.node;

import io.github.tofpu.vertexlink.grpc.client.EdgeNodeClient;
import io.github.tofpu.vertexlink.grpc.client.PingResult;
import io.github.tofpu.vertexlink.node.exception.ClientAlreadyExistsException;
import io.github.tofpu.vertexlink.telemetry.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeConnectionHandler {
    private static final Logger log = LoggerFactory.getLogger(NodeConnectionHandler.class);
    private final NodeClientRegistry nodeClientRegistry;

    public NodeConnectionHandler(NodeClientRegistry nodeClientRegistry) {
        this.nodeClientRegistry = nodeClientRegistry;
    }

    public NodeRegistrationResult validateAndRegisterNode(
            NodeId nodeId,
            String host,
            int port
    ) {
        EdgeNodeClient nodeClient = new EdgeNodeClient(host, port);
        log.info("Pinging edge node {}:{}", host, port);
        PingResult result = nodeClient.ping();
        if (result == PingResult.SUCCESS) {
            log.info("Successfully pinged edge node {}:{}", host, port);
            try {
                nodeClientRegistry.registerClient(nodeId, nodeClient);
            } catch (ClientAlreadyExistsException e) {
                log.error("Client already exists: {}", nodeId, e);
                return NodeRegistrationResult.CLIENT_ALREADY_EXISTS;
            } catch (Exception e) {
                log.error("Error", e);
                return NodeRegistrationResult.UNFAMILIAR_ERROR;
            }
            log.info("Registered Node {} ({}:{})", nodeId, host, port);
            return NodeRegistrationResult.SUCCESS;
        } else {
            log.info("Could not connect to edge node {}:{}", host, port);
            return NodeRegistrationResult.UNREACHABLE_ADDRESS;
        }
    }
}
