package io.github.githubjakob.convolutionalSat.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Decoder;
import io.github.githubjakob.convolutionalSat.modules.Encoder;
import org.bouncycastle.ocsp.Req;

/**
 * Created by jakob on 02.08.18.
 */
public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Encoder.class).in(Singleton.class);
        bind(Decoder.class).in(Singleton.class);
        bind(Channel.class).in(Singleton.class);
        bind(Requirements.class).in(Singleton.class);
    }
}
