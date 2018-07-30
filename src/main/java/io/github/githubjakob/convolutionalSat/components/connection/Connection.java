package io.github.githubjakob.convolutionalSat.components.connection;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.components.Component;
import io.github.githubjakob.convolutionalSat.components.InputPin;
import io.github.githubjakob.convolutionalSat.components.OutputPin;
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
