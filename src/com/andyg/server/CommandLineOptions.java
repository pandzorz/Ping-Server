package com.andyg.server;

import org.apache.commons.cli.*;

/**
 * Created by Andy on 4/06/2016.
 */
public class CommandLineOptions {

    /**
     * Get an Options object based on the outlined options.
     *
     * @return an Options object containing the available options for the server
     */
    public static Options getOptions()
    {
        Options tempOptions = new Options();
        tempOptions.addOption("h", true, "Hostname the server should start on.");
        tempOptions.addOption("p", true, "A port number to start the server on.");
        tempOptions.addOption("t", true, "Set the number of threads the server should use.");

        tempOptions.addOption("dh", true, "Hostname of the MongoDB connection.");
        tempOptions.addOption("dp", true, "Port number of the MongoDB connection.");

        return tempOptions;
    }


    /**
     * Get the parsed results of arguments for the options provided.
     *
     * @param opts available to the server
     * @param args provided to the server
     * @return the parsed commands that were obtained from the args
     * @throws ParseException
     */
    public static CommandLine getCommands(Options opts, String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(opts, args);
        return cmd;
    }
}
