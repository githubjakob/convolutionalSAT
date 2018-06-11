package logic;

import com.google.errorprone.annotations.Var;
import components.Component;
import components.Connection;

import java.util.*;

/**
 * Created by jakob on 11.06.18.
 */
public class Model {

    private final Set<Connection> connections;

    private List<Variable> variables = new ArrayList<>();

    public Model(List<Variable> variables) {
        List<Variable> cloned = new ArrayList<>();
        for (Variable variable : variables) {
            boolean weight = variable.getWeight();
            Component component = variable.getComponent();
            cloned.add(new Variable(weight, component));
        }
        this.variables = cloned;
        this.connections = getConnections();

    }


    public Set<Connection> getConnections() {
        if (variables == null) {
            return Collections.emptySet();
        }

        Set<Connection> connections = new HashSet<>();

        for (Variable variable : variables) {
            Component component = variable.getComponent();

            if (variable.getWeight() && component instanceof Connection){
                connections.add((Connection) component);
            }
        }
        return connections;
    }


}
