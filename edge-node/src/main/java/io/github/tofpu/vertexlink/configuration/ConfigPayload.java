package io.github.tofpu.vertexlink.configuration;

import java.util.Objects;

public class ConfigPayload {
    private boolean valueState;

    public boolean valueState() {
        return valueState;
    }

    public void valueState(boolean valueState) {
        this.valueState = valueState;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConfigPayload that = (ConfigPayload) o;
        return valueState == that.valueState;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valueState);
    }
}
