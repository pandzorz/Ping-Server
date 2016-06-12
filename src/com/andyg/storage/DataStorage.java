package com.andyg.storage;

import com.andyg.time.DateControl;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.joda.time.LocalDate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andy on 31/05/2016.
 */
public class DataStorage {

    private HashMap<String, MemoryData> memStorage = new HashMap<>();

    // Queue to add items to the database
    private ArrayList<MemoryData> dbStorageQueue = new ArrayList<>();
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    // Database name values
    private final String DATABASE_NAME = "mydb";
    private final String DEVICE_COLLECTION = "devices";

    // Entry Field Identifications
    private final String DEV_ID = "dev_Id";
    private final String PINGS = "pings";


    /**
     * A means for storing and manipulating data in a remote database and memory.
     *
     * @param hostname of database
     * @param port of database
     */
    public DataStorage(String hostname, int port) throws Exception
    {
            mongoClient = new MongoClient(hostname, port);
            database = mongoClient.getDatabase(DATABASE_NAME);
            collection = database.getCollection(DEVICE_COLLECTION);
            databaseToMem();
    }


    /**
     * Load the database into memory.
     */
    private void databaseToMem()
    {
        for (Document doc : collection.find())
        {
            ArrayList pings = (ArrayList) doc.get(PINGS);
            MemoryData tempData = new MemoryData((String) doc.get(DEV_ID), pings, true);
            tempData.setDbId(doc.get("_id").toString());
        }
    }


    /**
     *  Store an entry for the given device.
     *
     * @param device name
     * @param times of pings to be stored
     */
    public void storeItem(String device, ArrayList<Long> times)
    {
        MemoryData temp;
        if (memStorage.containsKey(device))
        {
            temp = memStorage.get(device);
            temp.addData(times);
        }
        else
        {
            temp = new MemoryData(device, times, false);
            memStorage.put(device, temp);
        }
        dbStorageQueue.add(temp);
        updateDatabase();
    }


    /**
     * Update the database with any memory items that are not stored.
     */
    private void updateDatabase()
    {
        for (MemoryData mem : (ArrayList<MemoryData>)dbStorageQueue.clone())
        {
            updateDbItem(mem);
            dbStorageQueue.remove(mem);
        }
    }


    /**
     * Update the database with the provided Memory Item.
     *
     * @param mem Memory structure for device and pings
     */
    private void updateDbItem(MemoryData mem) {
        if (mem.isInDatabase())
        {
            collection.updateOne(new Document(DEV_ID, mem.getName()),
                    new Document("$set", new Document(PINGS, mem.getPings())));
        }
        else
        {
            try
            {
                collection.insertOne(mem.getDocumentItem());
            }
            catch (Exception  e)
            {
                e.printStackTrace();
            }
            mem.setInDatabase(true);
        }
    }


    /**
     * Get all of the data.
     *
     * @return A JSON of all the available data
     */
    public JSONObject allData()
    {
        JSONObject data = new JSONObject();
        ArrayList<Long> tempTimes;

        for (String key : memStorage.keySet())
        {
            tempTimes = memStorage.get(key).getPings();
            JSONArray tempJArr = new JSONArray();
            tempJArr.addAll(tempTimes);
            data.put(key, tempJArr);
        }
        return data;
    }


    /**
     * Get all of the data in a range of dates.
     *
     * @param from From date
     * @param to To date
     * @return A JSON of the required data
     */
    public JSONObject allData(long from, long to)
    {
        JSONObject data = new JSONObject();
        ArrayList<Long> tempTimes;

        for (String key : memStorage.keySet())
        {
            tempTimes = getDates(key, from, to);
            JSONArray tempJArr = new JSONArray();
            tempJArr.addAll(tempTimes);
            data.put(key, tempJArr);
        }
        return data;
    }


    /**
     * Get All of the stored devices.
     *
     * @return A list of device names
     */
    public ArrayList<String> getDevices()
    {
        return new ArrayList<>(memStorage.keySet());
    }


    /**
     * Get the data on a given date.
     *
     * @param device name
     * @param unixDate required
     * @return A list of pings for the given date
     */
    public ArrayList<Long> getDate(String device, long unixDate)
    {
        ArrayList<Long> pings = new ArrayList<>();
        ArrayList<Long> tempPings = memStorage.get(device).getPings();
        LocalDate dateFromUnix = DateControl.toDate(unixDate).toLocalDate();

        for (Long ping : tempPings)
        {
            LocalDate tempDate = DateControl.toDate(ping).toLocalDate();
            if (dateFromUnix.compareTo(tempDate) == 0)
            {
                pings.add(ping);
            }
        }
        return pings;
    }


    /**
     * Get the data in a range of dates.
     *
     * @param device name
     * @param from From date
     * @param to To date
     * @return A list of pings in the range of dates
     */
    public ArrayList<Long> getDates(String device, long from, long to)
    {
        ArrayList<Long> pings = new ArrayList<>();
        ArrayList<Long> tempPings;
        if (memStorage.containsKey(device))
        {
             tempPings = memStorage.get(device).getPings();
        }
        else
        {
            return new ArrayList<>();
        }

        for (Long ping : tempPings)
        {
            if ((ping.compareTo(from) >= 0) && (ping.compareTo(to) < 0) )
            {
                pings.add(ping);
            }
        }
        return pings;
    }


    /**
     * Clears all of the data stored.
     */
    public void clearData()
    {
        collection.drop();
        memStorage.clear();
    }
}
