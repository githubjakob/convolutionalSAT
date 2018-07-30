package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.List;

/**
 * Created by jakob on 30.07.18.
 */
public interface Connection extends Component {
    List<Clause> convertToCnf(int numberOfGates);
    List<Clause> convertToCnfAtTick(BitStream bitStream, int numberOfGates);
    InputPin getTo();
    OutputPin getFrom();

}
