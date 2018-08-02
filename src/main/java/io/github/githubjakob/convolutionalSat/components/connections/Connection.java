package io.github.githubjakob.convolutionalSat.components.connections;

import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.Component;
import io.github.githubjakob.convolutionalSat.components.gates.Input;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.List;

/**
 * Created by jakob on 30.07.18.
 */
public interface Connection extends Component {
    List<Clause> convertToCnf();
    InputPin getTo();
    OutputPin getFrom();
    void setFrom(OutputPin from);
    void setTo(InputPin to);
}
