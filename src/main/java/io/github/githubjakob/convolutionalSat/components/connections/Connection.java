package io.github.githubjakob.convolutionalSat.components.connections;

import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.Component;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.List;

/**
 * Created by jakob on 30.07.18.
 */
public interface Connection extends Component {
    List<Clause> convertMicroticksRequirement(int numberOfGates);
    List<Clause> convertToCnfAtTick(BitStream bitStream, int numberOfGates);
    InputPin getTo();
    OutputPin getFrom();

}
