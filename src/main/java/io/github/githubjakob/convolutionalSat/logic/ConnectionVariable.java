package io.github.githubjakob.convolutionalSat.logic;

import io.github.githubjakob.convolutionalSat.components.Component;

/**
 * Created by jakob on 07.06.18.
 */
public class ConnectionVariable {

    boolean weight;

    Component component;

    int literal;

    int bitStreamId = -1;

    public ConnectionVariable(boolean weight, Component component) {
        this.weight = weight;
        this.component = component;
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

    public boolean getWeight() {
        return this.weight;
    }

    public void setWeight(boolean weight) {
        this.weight = weight;
    }

    public void setLiteral(int literal) {
        this.literal = literal;
    }

    public Component getComponent() {
        return component;
    }
}
