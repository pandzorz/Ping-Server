package com.andyg.server;

import com.andyg.storage.DataStorage;
import com.andyg.time.DateControl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Andy on 31/05/2016.
 */
public class PingSever {
    private static final Logger logger = LogManager.getLogger();

    // HTTP Response Strings
    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String JSON_DATA = "application/json; charset=%s";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    // HTTP Statuses
    private static final int STATUS_OK = 200;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private static final int NO_RESPONSE_LENGTH = -1;

    // HTTP Method Strings
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_POST;

    private static DataStorage data;
    private HttpServer server;

    public PingSever()
    {

    }

    /**
     * Attempt to bind the server to a socket of the provided hostname and port.
     *
     * @param hostname of the server
     * @param port of the server
     * @throws IOException
     */
    public void bindServer(String hostname, int port) throws IOException
    {
        server = HttpServer.create(new InetSocketAddress(hostname, port), 0);

        // Contexts available to use
        server.createContext("/", new PostData());
        server.createContext("/all", new AllData());
        server.createContext("/devices", new Devices());
        server.createContext("/clear_data", new ClearData());
    }

    /**
     * Initialise the data storage.
     *
     * @param hostname of the database
     * @param port of the database
     */
    public void loadData(String hostname, int port) throws Exception
    {
        data = new DataStorage(hostname, port);
    }

    /**
     * Start the sever with the provided thread count.
     * A thread count of 0 will result in a default executor.
     *
     * @param threadCount for the server
     */
    public void start(int threadCount)
    {
        Executor executor = threadCount != 0 ? Executors.newFixedThreadPool(threadCount) : null;
        server.setExecutor(executor);
        server.start();
        logger.info("Server started successfully.");
    }


    /**
     * Class for "/" context.
     * Used to get and store device data.
     */
    private static class PostData implements HttpHandler
    {
        @Override
        public void handle(HttpExchange t) throws IOException
        {
            String[] params = t.getRequestURI().toString().split("/");
            String device = params[1];
            ArrayList<Long> dates = addFromTo(params);

            switch (t.getRequestMethod())
            {
                case METHOD_POST:
                    data.storeItem(device, getPings(params));
                    sendOkay(t);
                    break;
                case METHOD_GET:
                    String response = getJsonArray(getTimes(params.length, device, dates)).toJSONString();
                    sendJson(t, response);
                    break;
                default:
                    sendError(t);
                    break;
            }
        }
    }


    /**
     * Class for "clear_data" context.
     * Used to clear all data stored.
     */
    private static class ClearData implements HttpHandler
    {
        @Override
        public void handle(HttpExchange t) throws IOException
        {
            switch (t.getRequestMethod())
            {
                case METHOD_POST:
                    data.clearData();
                    sendOkay(t);
                    break;
                default:
                    sendError(t);
                    break;
            }
        }
    }


    /**
     * Class for "/devices" context.
     * Used to get all of the stored devices.
     */
    private static class Devices implements HttpHandler
    {
        @Override
        public void handle(HttpExchange t) throws IOException
        {
            String response = getJsonArray(data.getDevices()).toJSONString();
            sendJson(t, response);
        }
    }


    /**
     * Class for "/all" context.
     * Used to get all of the data stored.
     */
    private static class AllData implements HttpHandler
    {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String[] params = t.getRequestURI().toString().split("/");
            String response = params.length > 1 ?
                    data.allData(DateControl.toUnix(params[2]), DateControl.toUnix(params[3])).toJSONString() :
                    data.allData().toJSONString();
            sendJson(t, response);
        }
    }


    // Helper Methods

    /**
     * Send a JSON string to the given request.
     *
     * @param t The received HTTP object
     * @param response The string of a JSON formatted response
     * @throws IOException
     */
    private static void sendJson(HttpExchange t, String response) throws IOException
    {
        t.getResponseHeaders().set(HEADER_CONTENT_TYPE, String.format(JSON_DATA, CHARSET));
        t.sendResponseHeaders(STATUS_OK, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    /**
     * Send Okay response back to request.
     *
     * @param t The received HTTP object
     * @throws IOException
     */
    private static void sendOkay(HttpExchange t) throws IOException
    {
        t.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
    }


    /**
     * Send a methods allowed error back to request.
     *
     * @param t The received HTTP object
     * @throws IOException
     */
    private static void sendError(HttpExchange t) throws IOException
    {
        t.getResponseHeaders().set(HEADER_ALLOW, ALLOWED_METHODS);
        t.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
    }


    /**
     * Turn a list of objects into JSONArray.
     *
     * @param items Objects to add to the JSON array
     * @param <T> Different types for array
     * @return JSONArray of the given list
     */
    private static <T> JSONArray getJsonArray(ArrayList<T> items)
    {
        JSONArray json = new JSONArray();
        for(Object item : items)
        {
            json.add(item);
        }
        return json;
    }


    /**
     * Get the from and to dates in unix form.
     *
     * @param params Parameters from get
     * @return A list of unix times from and to
     */
    private static ArrayList<Long> addFromTo(String[] params)
    {
        ArrayList<Long> dates = new ArrayList<>();
        for (int i = 2; i < params.length; i++)
        {
            dates.add(DateControl.toUnix(params[i]));
        }
        return dates;
    }


    /**
     * Get data for the given device on given date(s).
     *
     * @param opt 3 parameters is a single date, and 4 means a from and to date
     * @param device Device name to get
     * @param dates The date or from and to dates
     * @return
     */
    private static ArrayList<Long> getTimes(int opt, String device, ArrayList<Long> dates)
    {
        if (opt == 3)
        {
            return data.getDate(device, dates.get(0));
        }
        else if (opt == 4)
        {
            return data.getDates(device, dates.get(0), dates.get(1));
        }
        return new ArrayList<>();
    }


    /**
     * Extract the pings from the URI request.
     *
     * @param params Parameters from post
     * @return List of times as Longs
     */
    private static ArrayList<Long> getPings(String[] params)
    {
        ArrayList<Long> times = new ArrayList<>();
        for (int i = 2; i < params.length; i++)
        {
            times.add(Long.parseLong(params[i]));
        }
        return times;
    }
}
