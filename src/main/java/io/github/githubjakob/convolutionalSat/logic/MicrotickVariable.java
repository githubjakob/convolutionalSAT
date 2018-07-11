package io.github.githubjakob.convolutionalSat.logic;

import io.github.githubjakob.convolutionalSat.components.Component;
import lombok.Getter;

/**
 * Created by jakob on 08.06.18.
 */
public class MicrotickVariable extends Variable {

    @Getter
    private final int microtick;

    public MicrotickVariable(int microtick, boolean weight, Component component) {
        super(weight, component);
        this.microtick = microtick;
    }

    public int getBitStreamId() {
        return bitStreamId;
    }

    @Override
    public String toString() {
        return "[" + microtick + "|" + weight + "]" + this.component.toString();
    }

    @Override
    public int hashCode() {
        return microtick + 33 * super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof MicrotickVariable))return false;
        MicrotickVariable other = (MicrotickVariable) obj;
        return (this.microtick == other.microtick
                && other.component.equals(this.component));
    }

    public boolean getWeight() {
        return this.weight;
    }
}
