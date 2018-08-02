package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import lombok.Setter;

import javax.inject.Inject;

/**
 * Created by jakob on 24.07.18.
 */
public class GlobalInput extends Input {

    @Setter
    private Boolean bitValue = null;

    @Inject
    public GlobalInput(Requirements requirements) {
        super(requirements);
    }

    @Override
    public boolean evaluate(BitStream bitStream, int tick) {
        if (bitValue == null) {
            throw new RuntimeException("Bit Wert des globalen Inputs nicht gesetzt.");
        }
        return bitValue;
    }
}
