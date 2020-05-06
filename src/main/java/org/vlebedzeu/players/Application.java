package org.vlebedzeu.players;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.builders.ApplicationBuilder;

/**
 *
 */
public class Application  {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /** CLI options */
    private static Options options = new Options();

    static {
        options.addOption(
                Option.builder("ch")
                        .longOpt("channel")
                        .desc("Type of channel.")
                        .argName("Channel")
                        .hasArg()
                        .required(false)
                        .build()
        );
        options.addOption(
                Option.builder("p")
                        .longOpt("players")
                        .desc("Comma separated list of Players. First of them will be initiator.")
                        .argName("Players")
                        .hasArg()
                        .required(true)
                        .build()
        );
        options.addOption(
                Option.builder("msg")
                        .longOpt("message")
                        .desc("Comma separated list of messages.")
                        .argName("Messages")
                        .hasArg()
                        .required(true)
                        .build()
        );
        options.addOption(
                Option.builder("h")
                        .longOpt("help")
                        .desc("Prints help message")
                        .required(false)
                        .build()
        );
    }

    public static void main(String... args) {
        logger.info("Running Players application...");
        if (parseParameters(args)) {
            logger.info("==============");
        }
        logger.info("Done.");
    }

    private static boolean parseParameters(String... args) {
        CommandLineParser parser = new DefaultParser();
        try {
            final CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                printHelp();
            }

            final String channelOpt = line.getOptionValue("ch");
            final String playersOpt = line.getOptionValue("p");
            final String messageOpt = line.getOptionValue("msg");

            logger.info("channel : {}", channelOpt);
            logger.info("players : {}", playersOpt);
            logger.info("message : {}", messageOpt);

            ApplicationBuilder builder = new ApplicationBuilder(channelOpt, playersOpt, messageOpt).build();
            builder.start();
        }
        catch (ParseException e) {
            logger.error(e.getMessage());
            printHelp();
            return false;
        }
        return true;
    }

    /**
     * Prints CLI application help, including possible options
     */
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        final String separator = StringUtils.repeat("=", 100);
        final String header = separator + "\n" + "Players application options\n" + separator + "\n";
        formatter.printHelp(150, "java -jar bidder.jar", header, options, separator, true);
    }
}
