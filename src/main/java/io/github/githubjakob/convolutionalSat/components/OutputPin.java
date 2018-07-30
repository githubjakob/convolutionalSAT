package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.components.connection.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class OutputPin implements Pin {

    static int idCounter = 0;

    Integer id;

    public Gate getGate() {
        return gate;
    }

    @Getter
    @Setter
    private List<Connection> connections = new ArrayList<>();

    @Getter
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

    @Override
    public String getType() {
        return "output-pin";
    }

    @Override
    public Module getModule() {
        return this.gate.getModule();
    }
}
