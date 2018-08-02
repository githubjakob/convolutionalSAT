package io.github.githubjakob.convolutionalSat.components.gates;

import com.google.inject.Inject;
import io.github.githubjakob.convolutionalSat.Requirements;

/**
 * Created by jakob on 24.07.18.
 */
public class GlobalOutput extends Output {

    @Inject
    public GlobalOutput(Requirements requirements) {
        super(requirements);
    }
}
