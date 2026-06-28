# Vertex Link

A local-first synchronization engine framework written in Java in the context of an __imaginary industrial telemetry system__.

> ⚠️**Transparency disclaimer**: This project is based on an interview challenge project given to me by Gemini. The technologies used throughout this project were dictated by Gemini (aside from `MVStore` by H2 and other trival dependencies). Furthermore, during the development of this project, Gemini were only used to ask clarifying questions which is meant to mimick a real interview. They were not used to generate code (aside from generating default logback.xml file). 

## Technology used:
- **Communication**: `RPC (gRPC)` were used to facilitate the communication from the edge-node(s) to the central-server.
- **Serialization**: `Protobuf 3` were used to serialize data objects during usage of RPC and Redis.
- **Durability**:
  - Edge node: `MVStore` were used to store telemetries in disk in an event that it cannot communicate to the central server. Once a connection is restablished, the telemetries stored in disk are sent over and removed from disk.
  - Central server: `Redis` were used to asynchronizely store incoming telemetries objects from the edge node to a stack. Additionally, a dedicated thread is used to poll telemetries stored in Redis and set the latest telemetry for each given node.
