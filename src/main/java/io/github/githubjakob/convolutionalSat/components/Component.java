package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.modules.Module;

/**
 * Created by jakob on 07.06.18.
 */
public interface Component {
    String getType();
    Module getModule();
}
