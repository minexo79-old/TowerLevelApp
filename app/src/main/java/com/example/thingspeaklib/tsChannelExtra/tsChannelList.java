package com.example.thingspeaklib.tsChannelExtra;

import java.util.ArrayList;
import java.util.List;

/**
 * ThingSpeak tsChannelExtra.Channel List
 * Author: Xin You Tsai (minexo79)
 * Date: 2021.6.5
 */

public class tsChannelList {

    /** Use List **/
    private List<Channel> _channels;

    /** Initialize **/
    public tsChannelList() {
        this._channels = new ArrayList<Channel>();
    }

    /** Add Thingspeak Channel Object to List and return true, otherwise return false. **/
    public boolean AddChannel(int id, String api, int maxDepth) {
        // check channel has exist
        for (Channel channel : _channels) {
            if(channel.Id == id)
                return false;
        }
        // if not exist, add channel into list
        Channel newChannel = new Channel(id, api, maxDepth);
        _channels.add(newChannel);
        return true;
    }

    /** Remove Thingspeak channel Parameter from the List and return true, otherwise return false. **/
    public boolean RemoveChannel(int id) {
        // create null obejct
        Channel remChannel = null;
        for (Channel channel : _channels) {
            // if channel has exist
            if (channel.Id == id) {
                remChannel = channel;
                _channels.remove(remChannel);
                return true;
            }
        }
        return false;
    }

    /** Return a channel list **/
    public List<Channel> GetChannelList() {
        return this._channels;
    }

    /** return specific channel object if it has in list, otherwise return null object **/
    public Channel GetChannel(int id) {
        for (Channel channel : _channels) {
            if (channel.Id == id)
                return channel;
        }
        return null;
    }
}
