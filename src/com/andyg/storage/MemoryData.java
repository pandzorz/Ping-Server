package com.andyg.storage;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Andy on 4/06/2016.
 */
public class MemoryData {

    private String devName;
    private TreeSet<Long> pings = new TreeSet<>();
    private String dbId;
    private boolean inDatabase;
    private Document doc;

    // Entry Field Identifications
    private final String DEV_ID = "dev_Id";
    private final String PINGS = "pings";

    /**
     * Constructor for a memory data item.
     *
     * @param name of the device
     * @param pings for the device
     * @param dbStatus whether the memory item is stored in the database
     */
    public MemoryData(String name, ArrayList<Long> pings, boolean dbStatus)
    {
        devName = name;
        this.pings.addAll(pings);
        inDatabase = dbStatus;
        createDocument(pings);
    }


    /**
     * Create a Document object with the provided pings.
     *
     * @param pings to be stored
     */
    private void createDocument(ArrayList<Long> pings)
    {
        ObjectId id = new ObjectId();
        Document tempDoc = new Document(DEV_ID, devName);
        tempDoc.append(PINGS, pings);
        tempDoc.put("_id", id);
        doc = tempDoc;
    }


    /**
     * Update the document with additional pings.
     */
    private void updateDocument()
    {
        doc.put(PINGS, pings);
    }


    /**
     * Add more pings for this device.
     *
     * @param pingData pings to be added to this device
     */
    public void addData(ArrayList<Long> pingData)
    {
        pings.addAll(pingData);
        updateDocument();
    }


    /**
     * Get the document object for this device.
     *
     * @return Document object of this device
     */
    public Document getDocumentItem()
    {
        return doc;
    }


    /**
     * Get the pings stored for this device.
     *
     * @return list of pings
     */
    public ArrayList<Long> getPings() {
        ArrayList<Long> tempPings = new ArrayList<>();
        for (Long ping : pings)
        {
            tempPings.add(ping);
        }
        return tempPings;
    }


    /**
     * Get the device name.
     *
     * @return device name
     */
    public String getName()
    {
        return devName;
    }


    /**
     * Get the status of the data storage in the database.
     *
     * @return if the data is stored in the database or not
     */
    public boolean isInDatabase()
    {
        return inDatabase;
    }


    /**
     * Set the status of being stored in the database.
     *
     * @param status of being stored in the database
     */
    public void setInDatabase(boolean status)
    {
        inDatabase = status;
    }


    /**
     * Get the ID of the device stored in the database.
     *
     * @return database ID
     */
    public String getDbId()
    {
        return dbId;
    }


    /**
     * Set what the id is of the data item stored in the database.
     *
     * @param id of the device stored in the database
     */
    public void setDbId(String id)
    {
        dbId = id;
    }


    /**
     * Get a nicely constructed string representation of this device and data.
     *
     * @return A nicely constructed string representation of this device data
     */
    @Override
    public String toString()
    {
        StringBuilder wholeString = new StringBuilder();
        wholeString.append(devName);
        wholeString.append(" ");
        wholeString.append(dbId);
        for (Long ping : pings)
        {
            wholeString.append(" ");
            wholeString.append(ping);
        }
        return wholeString.toString();
    }
}
