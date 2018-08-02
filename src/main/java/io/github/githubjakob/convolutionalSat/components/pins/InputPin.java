package io.github.githubjakob.convolutionalSat.components.pins;

import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jakob on 07.06.18.
 */
public class InputPin implements Pin {

    static int idCounter = 0;

    Integer id;

    private final Gate gate;

    @Getter
    @Setter
    private Connection connection = null;

    public InputPin(Gate gate) {
        this.id = idCounter++;
        this.gate = gate;
    }

    @Override
    public String toString() {
        return "In" + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof InputPin))return false;
        InputPin other = (InputPin) obj;
        return (other.id.equals(this.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Gate getGate() {
        return gate;
    }

    @Override
    public String getType() {
        return "input-pin";
    }

    @Override
    public Module getModule() {
        return this.gate.getModule();
    }
}
