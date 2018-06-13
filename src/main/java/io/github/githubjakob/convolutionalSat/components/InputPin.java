package io.github.githubjakob.convolutionalSat.components;

/**
 * Created by jakob on 07.06.18.
 */
public class InputPin implements Pin {

    static int idCounter = 0;

    int id;

    private final Component component;

    public InputPin(Component component) {
        this.id = idCounter++;
        this.component = component;
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
        return (other.id==this.id);
    }
}
