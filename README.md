# Vertex Link

A local-first synchronization engine written in Java in the context of an __imaginary industrial telemetry system__.

> ⚠️**Transparency disclaimer**: This project is based on an interview challenge provided by Gemini. The technologies used throughout the project were dictated by Gemini (aside from `MVStore` by H2 and other trival dependencies). Furthermore, during the development of this project, Gemini were only used to ask clarifying questions which is meant to mimick a real interview. They were not used to generate code (aside from generating default logback.xml file). They also also used for interface name suggestions (`Ingest` suffix, etc). 

## Capabilities:
- The registration of edge nodes to the central server on boot.
  - The registration request is validated prior to acceptance by sending a ping request via gRPC to the given edge node to confirm the validity of the provided address (host:port).
- The transmission of incoming telemetry from edge nodes to the central server.
  - If the connection from the edge node to the central server were severed, the telemetry are written to disk until the connection is reinstated.

## Technology used:
- `RPC (gRPC)`: facilitates communication between edge nodes and central server.
- `Protobuf 3`: introduces compact representation of domain data objects to minimize network usage.
- `MVStore`: stores the ingested telemetry into disk during connection outage on the edge server.
- `Redis`: stores upcoming telemetry data in a stack until they are processed by the central server
