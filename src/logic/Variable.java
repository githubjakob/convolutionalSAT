package logic;

import com.google.common.collect.Multimap;
import com.google.errorprone.annotations.Var;
import components.Component;

/**
 * Created by jakob on 07.06.18.
 */
public class Variable {

    boolean weight;

    Component component;

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
        if (!(obj instanceof Variable))return false;
        Variable other = (Variable) obj;
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
