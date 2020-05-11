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
import org.vlebedzeu.players.api.InitializationException;
import org.vlebedzeu.players.api.builders.ApplicationBuilder;

import static org.vlebedzeu.players.api.ConfigConstants.*;

/**
 * Main application class
 */
public class Application  {
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /** CLI options */
    private static final Options options = new Options();

    static {
        // Initializing expected CLI options
        options.addOption(
                Option.builder(OPT_CHANNEL)
                        .longOpt(OPT_CHANNEL_LONG)
                        .desc(OPT_CHANNEL_DESC)
                        .argName(OPT_CHANNEL_ARG)
                        .hasArg()
                        .required(false)
                        .build()
        );
        options.addOption(
                Option.builder(OPT_PLAYERS)
                        .longOpt(OPT_PLAYERS_LONG)
                        .desc(OPT_PLAYERS_DESC)
                        .argName(OPT_PLAYERS_ARG)
                        .hasArg()
                        .required(true)
                        .build()
        );
        options.addOption(
                Option.builder(OPT_MESSAGE)
                        .longOpt(OPT_MESSAGE_LONG)
                        .desc(OPT_MESSAGE_DESC)
                        .argName(OPT_MESSAGE_ARG)
                        .hasArg()
                        .required(false)
                        .build()
        );
        options.addOption(
                Option.builder(OPT_TIMEOUT)
                        .longOpt(OPT_TIMEOUT_LONG)
                        .desc(OPT_TIMEOUT_DESC)
                        .argName(OPT_TIMEOUT_ARG)
                        .hasArg()
                        .required(false)
                        .build()
        );
        options.addOption(
                Option.builder(OPT_SUBSCRIBERS)
                        .longOpt(OPT_SUBSCRIBERS_LONG)
                        .desc(OPT_SUBSCRIBERS_DESC)
                        .argName(OPT_SUBSCRIBERS_ARG)
                        .hasArg()
                        .required(false)
                        .build()
        );
        options.addOption(
                Option.builder(OPT_HELP)
                        .longOpt(OPT_HELP_LONG)
                        .desc(OPT_HELP_DESC)
                        .required(false)
                        .build()
        );
    }

    /**
     * Main application method
     * @param args Application arguments
     */
    public static void main(String... args) {
        logger.info("Running Players application...");
        if (parseParameters(args)) {
            logger.info(StringUtils.repeat("=", 100));
        }
    }

    /**
     * Parces application parameters and starts the application
     * @param args Application argumants
     * @return True if application is run successfully
     */
    private static boolean parseParameters(String... args) {
        CommandLineParser parser = new DefaultParser();
        try {
            final CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                printHelp();
            }

            logger.info("channel : {}", line.getOptionValue(OPT_CHANNEL));
            logger.info("players : {}", line.getOptionValue(OPT_PLAYERS));
            logger.info("message : {}", line.getOptionValue(OPT_MESSAGE));

            ApplicationBuilder builder = new ApplicationBuilder(line).build();
            builder.start();
        }
        catch (ParseException | InitializationException e) {
            logger.error("Can not start application: {}", e.getMessage());
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
        formatter.printHelp(150, "java -jar trage/players-jar-with-dependencies.jar", header, options, separator, true);
    }
}
