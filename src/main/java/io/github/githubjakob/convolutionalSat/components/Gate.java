package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public interface Gate extends Component {

    OutputPin getOutputPin();
    List<InputPin> getInputPins();
    List<Clause> convertToCnfAtTick(int tick);
    Enums.Group getGroup();

}
