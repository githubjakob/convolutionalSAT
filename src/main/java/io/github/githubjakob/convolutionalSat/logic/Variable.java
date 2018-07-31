package io.github.githubjakob.convolutionalSat.logic;

import io.github.githubjakob.convolutionalSat.components.Component;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jakob on 07.06.18.
 */
public abstract class Variable {

    @Getter
    @Setter
    boolean weight;

    @Getter
    @Setter
    Component component;

    @Getter
    @Setter
    int literal;

    public Variable(boolean weight, Component component) {
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
}
