package io.github.githubjakob.convolutionalSat.logic;

import io.github.githubjakob.convolutionalSat.components.Component;

/**
 * Created by jakob on 07.06.18.
 */
public class ConnectionVariable extends Variable {

    public ConnectionVariable(boolean weight, Component component) {
        super(weight, component);
    }

    @Override
    public String toString() {
        if (this.weight) {
            return this.component.toString();
        } else {
            return "~" + this.component.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof ConnectionVariable))return false;
        ConnectionVariable other = (ConnectionVariable) obj;
        return (other.component.equals(this.component));
    }

    @Override
    public int hashCode() {
        return component.hashCode();
    }
}
