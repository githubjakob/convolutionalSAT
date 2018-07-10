package io.github.githubjakob.convolutionalSat.logic;

import io.github.githubjakob.convolutionalSat.components.Component;

/**
 * Created by jakob on 08.06.18.
 */
public class BiggerOrEqualVariable extends ConnectionVariable {

    private final int unaryPosition;

    public BiggerOrEqualVariable(int unaryPosition, boolean weight, Component component) {
        super(weight, component);
        this.unaryPosition = unaryPosition;
    }

    @Override
    public String toString() {
        if (this.weight) {
            return ">[" + unaryPosition + "]" + this.component.toString();
        } else {
            return "<[" + unaryPosition + "]" + this.component.toString();
        }
    }

    @Override
    public int hashCode() {
        return unaryPosition * super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof BiggerOrEqualVariable))return false;
        BiggerOrEqualVariable other = (BiggerOrEqualVariable) obj;
        return (this.unaryPosition == other.unaryPosition
                && other.component.equals(this.component));
    }

    public boolean getWeight() {
        return this.weight;
    }
}
