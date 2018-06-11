package components;

/**
 * Created by jakob on 07.06.18.
 */
public class OutputPin implements Pin {

    static int idCounter = 0;

    int id;

    private final Component component;

    public OutputPin(Component component) {
        this.id = idCounter++;
        this.component = component;
    }

    @Override
    public String toString() {
        return "Out" + id;
    }
}
