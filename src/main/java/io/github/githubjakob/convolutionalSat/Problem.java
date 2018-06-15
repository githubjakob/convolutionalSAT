package io.github.githubjakob.convolutionalSat;


import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clauses;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Decoder;
import io.github.githubjakob.convolutionalSat.modules.Encoder;

import java.util.*;

/**
 * Created by jakob on 31.05.18.
 */
public class Problem {

    Encoder encoder;

    Decoder decoder;

    Channel channel;

    public int[] inputBitStream;

    public Problem(Encoder encoder, Decoder decoder, int[] inputBitStream) {
        this.inputBitStream = inputBitStream;
        this.encoder = encoder;
        this.decoder = decoder;
        encoder.setInputBitStream(inputBitStream);
        decoder.setOutputBitStream(inputBitStream);
        this.channel = new Channel(encoder, decoder);

    }

    public List<Clauses> convertProblemToCnf() {

        List<Clauses> cnf = new ArrayList<>();

        List<Clauses> channel = this.channel.convertModuleToCnf();
        cnf.addAll(channel);

        List<Clauses> encoder = this.encoder.convertCircuitToCnf();
        cnf.addAll(encoder);

        List<Clauses> decoder = this.decoder.convertModuleToCnf();
        cnf.addAll(decoder);

        return cnf;

    }

    public List<Gate> getGates() {
        List<Gate> gates = new ArrayList<>();
        gates.addAll(encoder.getGates());
        gates.addAll(decoder.getGates());
        return gates;
    }
}
