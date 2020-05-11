package org.vlebedzeu.players.api;

import org.apache.commons.cli.Option;

/**
 * Configuration constants
 */
public class ConfigConstants {
    public static final String OPT_CHANNEL = "ch";
    public static final String OPT_CHANNEL_LONG = "channel";
    public static final String OPT_CHANNEL_DESC =
            "Type of channel (default or socket). Default will be used if omitted";
    public static final String OPT_CHANNEL_ARG = "Channel";

    public static final String OPT_PLAYERS = "p";
    public static final String OPT_PLAYERS_LONG = "players";
    public static final String OPT_PLAYERS_DESC = "Comma separated list of Players. First of them will be initiator.";
    public static final String OPT_PLAYERS_ARG = "Players";

    public static final String OPT_MESSAGE = "msg";
    public static final String OPT_MESSAGE_LONG = "message";
    public static final String OPT_MESSAGE_DESC = "Comma separated list of messages.";
    public static final String OPT_MESSAGE_ARG = "Messages";

    public static final String OPT_TIMEOUT = "to";
    public static final String OPT_TIMEOUT_LONG = "timeout";
    public static final String OPT_TIMEOUT_DESC = "Start timeout for socket mode in seconds. 180 by default.";
    public static final String OPT_TIMEOUT_ARG = "Timeout (sec)";

    public static final String OPT_SUBSCRIBERS = "sub";
    public static final String OPT_SUBSCRIBERS_LONG = "subscribers";
    public static final String OPT_SUBSCRIBERS_DESC = "Count of subscribers to start message exchange in socket mode.";
    public static final String OPT_SUBSCRIBERS_ARG = "Subscribers";

    public static final String OPT_HELP = "h";
    public static final String OPT_HELP_LONG = "help";
    public static final String OPT_HELP_DESC = "Prints help message.";
}
