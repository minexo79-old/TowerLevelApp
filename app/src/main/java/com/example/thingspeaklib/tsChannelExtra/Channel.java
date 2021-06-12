package com.example.thingspeaklib.tsChannelExtra;

/**
 * ThingSpeak tsChannelExtra.Channel List : tsChannelExtra.Channel Object
 * Author: Xin You Tsai (minexo79)
 * Date: 2021.6.5
 */
public class Channel {
    // thingspeak channel id
    public int Id;
    // thingspeak channel api key **only used for private channel **
    public String Api;
    // max tower depth
    public int MaxDepth;

    /** Initialize **/
    public Channel(int id, String api, int maxDepth) {
        this.Id = id;
        this.Api = api;
        this.MaxDepth = maxDepth;
    }
}
