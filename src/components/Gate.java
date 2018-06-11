package components;

import logic.Clauses;

import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public interface Gate extends Component {

    OutputPin getOutputPin();
    List<InputPin> getInputPins();
    Clauses convertToCnfAtTick(int tick);
}
