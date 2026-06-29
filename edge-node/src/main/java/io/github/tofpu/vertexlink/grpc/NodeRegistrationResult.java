package io.github.tofpu.vertexlink.grpc;

public enum NodeRegistrationResult {
    SUCCESS(true),
    FAILURE(false);

    private final boolean success;

    NodeRegistrationResult(boolean success) {
        this.success = success;
    }

    public boolean success() {
        return success;
    }
}
