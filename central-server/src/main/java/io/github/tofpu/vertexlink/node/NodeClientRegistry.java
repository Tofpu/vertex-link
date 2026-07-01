package io.github.tofpu.vertexlink.node;

import io.github.tofpu.vertexlink.grpc.client.EdgeNodeClient;
import io.github.tofpu.vertexlink.node.exception.ClientAlreadyExistsException;
import io.github.tofpu.vertexlink.telemetry.NodeId;

import java.util.HashMap;
import java.util.Map;

public class NodeClientRegistry {
    private final Map<NodeId, EdgeNodeClient> clientMap = new HashMap<>();

    public void registerClient(final NodeId nodeId, final EdgeNodeClient client) {
        if (clientMap.containsKey(nodeId)) {
            throw new ClientAlreadyExistsException();
        }
        clientMap.put(nodeId, client);
    }

    public EdgeNodeClient getClient(final NodeId nodeId) {
        return clientMap.get(nodeId);
    }
}
