package com.andyg;

import com.andyg.server.PingSever;
import com.andyg.server.ServerSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    // Option Parsing
    private static ServerSettings serverSettings = new ServerSettings();
    // Ping Server
    private static PingSever server = new PingSever();

    public static void main(String[] args)
    {
        if (!serverSettings.parseArgs(args))
        {
            return;
        }

        serverSettings.setOptions();

        try
        {
            server.bindServer(serverSettings.hostname, serverSettings.portNumber);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            logger.error("Cannot create server.");
            return;
        }
        logger.info("Hostname: " + serverSettings.hostname + " Port Number: " + serverSettings.portNumber +
                " Thread Count: " + serverSettings.threadCount);

        try
        {
            server.loadData(serverSettings.dbHostname, serverSettings.dbPort);
        } catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Cannot connect to database.");
            return;
        }
        server.start(serverSettings.threadCount);
    }
}