package io.github.githubjakob.convolutionalSat.logic;

import io.github.githubjakob.convolutionalSat.components.Component;

/**
 * Created by jakob on 08.06.18.
 */
public class TimeDependentVariable extends Variable {

    int tick;

    public int getTick() {
        return tick;
    }

    public TimeDependentVariable(int tick, boolean weight, Component component) {
        super(weight, component);

        this.tick = tick;
    }

    @Override
    public String toString() {
        if (this.weight) {
            return this.component.toString() + "/" + tick;
        } else {
            return "~" + this.component.toString() + "/" + tick;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof TimeDependentVariable))return false;
        TimeDependentVariable other = (TimeDependentVariable) obj;
        return (other.tick ==this.tick
                && other.component.equals(this.component));
    }

    public boolean getWeight() {
        return this.weight;
    }
}
