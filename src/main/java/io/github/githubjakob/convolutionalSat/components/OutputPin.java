package io.github.githubjakob.convolutionalSat.components;

/**
 * Created by jakob on 07.06.18.
 */
public class OutputPin implements Pin {

    static int idCounter = 0;

    Integer id;

    public Gate getGate() {
        return gate;
    }

    private final Gate gate;

    public OutputPin(Gate gate) {
        this.id = idCounter++;
        this.gate = gate;
    }

    @Override
    public String toString() {
        return "Out" + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof OutputPin))return false;
        OutputPin other = (OutputPin) obj;
        return (this.id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
