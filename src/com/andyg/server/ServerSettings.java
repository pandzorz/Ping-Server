package com.andyg.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Andy on 6/06/2016.
 */
public class ServerSettings {
    private static final Logger logger = LogManager.getLogger();

    // Default Server Values
    private static final String HOSTNAME_DEFAULT = "127.0.0.1";
    private static final int PORT_DEFAULT = 3000;
    private static final int THREAD_COUNT = 100;

    // Default Database Server Values
    private static final String DEFAULT_DATABASE_HOSTNAME = "127.0.0.1";
    private static final int DEFAULT_DATABASE_PORT = 27017;

    // Server and DB values
    public String hostname = HOSTNAME_DEFAULT;
    public int portNumber = PORT_DEFAULT;
    public int threadCount = THREAD_COUNT;
    public String dbHostname = DEFAULT_DATABASE_HOSTNAME;
    public int dbPort = DEFAULT_DATABASE_PORT;

    // Option Parsing
    private static Options opts = CommandLineOptions.getOptions();
    private static CommandLine cmds;


    /**
     * Parse the arguments provided to options available.
     *
     * @param args Arguments provided
     * @return If parsing was successful
     */
    public boolean parseArgs(String[] args) {
        // Parse the arguments to options
        try
        {
            cmds = CommandLineOptions.getCommands(opts, args);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     *  Set the options of the server.
     */
    public void setOptions()
    {
        // Hostname
        if (cmds.hasOption("h"))
        {
            hostname = cmds.getOptionValue("h");
        }

        // Port
        if (cmds.hasOption("p"))
        {
            try
            {
                portNumber = Integer.parseInt(cmds.getOptionValue("p"));
            }
            catch (Exception e)
            {
                logger.info("Not a valid Port number.");
                return;
            }
        }

        // Thread count
        if (cmds.hasOption("t"))
        {
            try
            {
                threadCount = Integer.parseInt(cmds.getOptionValue("t"));
            }
            catch (Exception e)
            {
                logger.info("Thread count is not a valid number.");
            }
        }

        // Database Hostname
        if (cmds.hasOption("dh"))
        {
            dbHostname = cmds.getOptionValue("dh");
        }

        // Database Port
        if (cmds.hasOption("dp"))
        {
            try
            {
                dbPort = Integer.parseInt(cmds.getOptionValue("dp"));
            }
            catch (Exception e)
            {
                logger.info("Not a valid database port number.");
            }
        }
    }
}
