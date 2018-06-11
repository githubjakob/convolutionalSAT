package components;

import com.google.errorprone.annotations.Var;
import logic.Clause;
import logic.Clauses;
import logic.TimeDependentVariable;
import logic.Variable;

import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Input implements Gate {

    private final OutputPin outputPin;

    public Input() {
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "GlobalInput";
    }

    public OutputPin getOutputPin() {
        return outputPin;
    }

    public List<InputPin> getInputPins() {
        return null;
    }

    @Override
    public Clauses convertToCnfAtTick(int tick) {
        TimeDependentVariable outputTrue = new TimeDependentVariable(tick, true, this);
        TimeDependentVariable outputFalse = new TimeDependentVariable(tick, false, this);

        TimeDependentVariable outputPinTrue = new TimeDependentVariable(tick, true, outputPin);
        TimeDependentVariable outputPinFalse = new TimeDependentVariable(tick, false, outputPin);

        Clause clause1 = new Clause(outputTrue, outputPinFalse);
        Clause clause2 = new Clause(outputFalse, outputPinTrue);

        return new Clauses(tick, clause1, clause2);
    }

}
