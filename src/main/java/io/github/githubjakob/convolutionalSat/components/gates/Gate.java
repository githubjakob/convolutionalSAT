package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.Component;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public interface Gate extends Component {

    OutputPin getOutputPin();
    List<InputPin> getInputPins();
    List<Clause> convertToCnf(BitStream bitStream, int maxMicroticks);
    Module getModule();
    void setModule(Module module);
    boolean evaluate(int tick);

}
