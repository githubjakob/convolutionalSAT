package io.github.githubjakob.convolutionalSat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.graph.Graph;
import io.github.githubjakob.convolutionalSat.gui.MainGui;
import io.github.githubjakob.convolutionalSat.guice.GuiceModule;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Decoder;
import io.github.githubjakob.convolutionalSat.modules.Encoder;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.ui.view.Viewer;

import java.io.ObjectInputStream;

import static org.apache.commons.cli.PatternOptionBuilder.NUMBER_VALUE;

public class Main {

    static Injector guice;

    static Logger logger;

    static boolean showGui = false;

    public static void main(String[] args) {

        CommandLineParser commandLineParser = new DefaultParser();
        Options options = new Options();


        options.addOption(Option.builder("a").longOpt("encoderAnds").hasArg().desc("Number of Ands for the Encdoder").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("n").longOpt("encoderNots").hasArg().desc("Number of Nots for the Encdoder").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("r").longOpt("endocderRegisters").hasArg().desc("Number of Registers for the Encoder").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("b").longOpt("decoderAnds").hasArg().desc("Number of Ands for the Decoder").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("m").longOpt("decoderNots").hasArg().desc("Number of Nots for the Decoder").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("q").longOpt("decoderRegisters").hasArg().desc("Number of Registers for the Decoder").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("d").longOpt("delay").hasArg().desc("Ticks of Delay").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("c").longOpt("channel").hasArg().desc("Number of Channels").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("f").longOpt("frameLength").hasArg().desc("Length of Frame in Bits").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("i").longOpt("iterions").hasArg().desc("Number of Iterations").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("e").longOpt("error").hasArg().desc("Number of Flipped Bits").type(NUMBER_VALUE).build());
        options.addOption(Option.builder("h").longOpt("help").desc("Shows the help").build());
        options.addOption(Option.builder("g").longOpt("gui").desc("Shows the Gui").build());

        try {
            CommandLine commandLine = commandLineParser.parse(options, args);

            if (commandLine.hasOption("h")) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("ConvolutionalSat", options);
            }

            if (!commandLine.hasOption("a") || !commandLine.hasOption("n") || !commandLine.hasOption("r")
             || !commandLine.hasOption("b") || !commandLine.hasOption("m") || !commandLine.hasOption("q")
             || !commandLine.hasOption("f") || !commandLine.hasOption("d") || !commandLine.hasOption("c")
             || !commandLine.hasOption("i") || !commandLine.hasOption("e")) {
                System.out.println("Invalid args, see --help.");
                System.exit(-1);
            }

            showGui = commandLine.hasOption("gui");
            disableWarning();
            runApp(((Long) commandLine.getParsedOptionValue("a")).intValue(),
                    ((Long) commandLine.getParsedOptionValue("n")).intValue(),
                    ((Long) commandLine.getParsedOptionValue("r")).intValue(),
                    0,
                    ((Long) commandLine.getParsedOptionValue("b")).intValue(),
                    ((Long) commandLine.getParsedOptionValue("m")).intValue(),
                    ((Long) commandLine.getParsedOptionValue("q")).intValue(),
                    0,
                    ((Long) commandLine.getParsedOptionValue("f")).intValue(),
                    ((Long) commandLine.getParsedOptionValue("d")).intValue(),
                    ((Long) commandLine.getParsedOptionValue("c")).intValue(),
                    ((Long) commandLine.getParsedOptionValue("i")).intValue(),
                    ((Long) commandLine.getParsedOptionValue("e")).intValue());

        } catch (ParseException e) {
            System.out.println("Invalid args, see --help.");
        }

    }

    public static void disableWarning() {
        System.err.close();
        System.setErr(System.out);
    }

    public static void runApp(int enAnd, int enNot, int enReg, int enXor,
                              int decAnd, int decNot, int decReg, int decXor,
                              int frameLength, int delay, int numberOfChannels, int maxNumberOfIterations, int numberOfFlippedBits) {
        logger = LogManager.getLogger();
        guice = Guice.createInjector(new GuiceModule());
        findConvolutionalCode(enAnd, enNot, enReg, enXor, decAnd, decNot, decReg, decXor,
        frameLength, delay, numberOfChannels, maxNumberOfIterations, numberOfFlippedBits);
    }

    public static void findConvolutionalCode(int enAnd, int enNot, int enReg, int enXor,
                                        int decAnd, int decNot, int decReg, int decXor,
                                        int frameLength, int delay, int numberOfChannels, int maxNumberOfIterations, int numberOfFlippedBits) {

        Requirements requirements = guice.getInstance(Requirements.class);
        requirements.setFrameLength(frameLength);
        requirements.setDelay(delay);
        requirements.setNumberOfChannels(numberOfChannels);
        requirements.setMaxNumberOfIterations(maxNumberOfIterations);
        requirements.setNumberOfFlippedBits(numberOfFlippedBits);
        requirements.setEnAnd(enAnd);
        requirements.setEnNot(enNot);
        requirements.setEnReg(enReg);
        requirements.setEnXor(enXor);
        requirements.setDecAnd(decAnd);
        requirements.setDecNot(decNot);
        requirements.setDecReg(decReg);
        requirements.setDecXor(decXor);

        logger.info("Finding convolutional code with:");
        logger.info(requirements.toString());
        findConvolutionalCode(requirements);
    }

    public static void findConvolutionalCode(Requirements requirements) {

        Encoder encoder = guice.getInstance(Encoder.class);
        Decoder decoder = guice.getInstance(Decoder.class);
        Channel channel = guice.getInstance(Channel.class);
        Problem problem = guice.getInstance(Problem.class);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit latestCircuit = null;

        MainGui mainGui = showGui ? new MainGui() : null;

        int iteration = 0;
        while (iteration < requirements.getMaxNumberOfIterations()) {
            logger.info("Iteration: {} ", iteration);

            BitStream failingOrRandomBitStream = problem.addFailingForOrRandom(latestCircuit);

            if (failingOrRandomBitStream == null) {
                break; // keine failingBitstreams gefunden -> fertig
            }

            latestCircuit = booleanExpression.solve();

            if (latestCircuit == null) {
                break; // keine Lösung mit letztem failing Bitstream gefunden -> fertig
            }

            Graph solution = new Graph(latestCircuit);
            if (showGui) {
                mainGui.addPanel(solution);
            }

            iteration++;
        }
    }
}
