# Vertex Link

A local-first synchronization engine framework written in Java in the context of an __imaginary industrial telemetry system__.

> ⚠️**Transparency disclaimer**: This project is based on an interview challenge provided by Gemini. The technologies used throughout the project were dictated by Gemini (aside from `MVStore` by H2 and other trival dependencies). Furthermore, during the development of this project, Gemini were only used to ask clarifying questions which is meant to mimick a real interview. They were not used to generate code (aside from generating default logback.xml file). They also also used for interface name suggestions (`Ingest` suffix, etc). 

## Capabilities:
- The registration of edge nodes to the central server on boot.
  - The registration request is validated prior to acceptance by sending a ping request via gRPC to the given edge node to confirm the validity of the address.
- The transmission of incoming telemetry from edge nodes to the central server.
  - If the connection from the edge node to the central server were severed, the telemetry are written to disk until the connection is reinstated.

## Technology used:
- **Communication**: `RPC (gRPC)` were used to facilitate the communication from the edge-node(s) to the central-server.
- **Serialization**: `Protobuf 3` were used to serialize data objects during usage of RPC and Redis.
- **Durability**:
  - Edge node: `MVStore` were used to store telemetries in disk in an event that it cannot communicate to the central server. Once a connection is restablished, the telemetries stored in disk are sent over and removed from disk.
  - Central server: `Redis` were used to asynchronizely store incoming telemetries objects from the edge node to a stack. Additionally, a dedicated thread is used to poll telemetries stored in Redis and set the latest telemetry for each given node.

