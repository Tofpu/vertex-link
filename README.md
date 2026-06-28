# Vertex Link

A local-first synchronization engine framework written in Java in the context of an imaginary industrial telemetry system.

## Technology used:
- **Communication**: `RPC (gRPC)` is used to facilitate the communication from the edge-node(s) to the central-server.
- **Serialization**: `Protobuf 3` is used to serialize data objects in the context of RPC and Redis.
- **Durability**:
  - Edge node: `MVStore` is used to store telemetries in disk in an event that it cannot communicate to the central server.
  - Central server: `Redis` is immediately used to store telemetries in a stack after receiving it from the edge node. In another thread, it is used to poll the given telemtries and set the latest telemetry for each given node.
