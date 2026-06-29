package io.github.tofpu.vertexlink.node;

public enum NodeRegistrationResult {
    UNREACHABLE_ADDRESS(false),
    CLIENT_ALREADY_EXISTS(false),
    UNFAMILIAR_ERROR(false),
    SUCCESS(true);

    private final boolean success;

    NodeRegistrationResult(boolean success) {
        this.success = success;
    }

    public boolean success() {
        return success;
    }
}
