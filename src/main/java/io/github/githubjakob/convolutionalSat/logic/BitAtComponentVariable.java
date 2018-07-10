package io.github.githubjakob.convolutionalSat.logic;

import io.github.githubjakob.convolutionalSat.components.Component;

/**
 * Created by jakob on 08.06.18.
 */
public class BitAtComponentVariable extends ConnectionVariable {

    int tick;

    public BitAtComponentVariable(int tick, int bitstreamId, boolean weight, Component component) {
        super(weight, component);
        this.bitStreamId = bitstreamId;
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }

    public int getBitStreamId() {
        return bitStreamId;
    }

    @Override
    public String toString() {
        if (this.weight) {
            return this.component.toString() + "/" + bitStreamId + "/" + tick;
        } else {
            return "~" + this.component.toString() + "/" + bitStreamId + "/" + tick;
        }
    }

    @Override
    public int hashCode() {
        return bitStreamId * super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof BitAtComponentVariable))return false;
        BitAtComponentVariable other = (BitAtComponentVariable) obj;
        return (other.tick ==this.tick
                && this.bitStreamId == other.bitStreamId
                && other.component.equals(this.component));
    }

    public boolean getWeight() {
        return this.weight;
    }
}
