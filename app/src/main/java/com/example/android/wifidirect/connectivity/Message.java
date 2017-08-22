package com.example.android.wifidirect.connectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

/**
 * Created by tj on 1/31/16.
 */
public class Message {
    private final String sender;
    private final MessageType messageType;
    private final String name;

    private final String data;

    public Message(Message otherMessage){
        this.sender = otherMessage.sender;
        this.messageType = otherMessage.messageType;
        this.name = otherMessage.name;
        this.data = otherMessage.data;
    }

    public Message(MessageBuilder builder){
        this.sender = builder.sender;
        this.messageType = builder.messageType;
        this.name = builder.name;
        this.data = builder.data;
    }

    public Message(String jsonString) throws JSONException{
        JSONObject json = new JSONObject(jsonString);
        this.sender = json.get("sender").toString();
        this.messageType = MessageType.valueOf(json.get("messageType").toString());
        this.name = json.get("name").toString();
        this.data = json.get("stringData").toString();
    }

    public JSONObject toJson() {
        try{
            JSONObject json = new JSONObject();
            json.put("sender", sender.toString());
            json.put("messageType", messageType.toString());
            json.put("name", name);
            json.put("stringData", data);
            System.out.println(json);
            return json;
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
        return null;

    }

    @Override
    public String toString(){
        // TODO implement better version
        return "{sender: " + sender + ", " + "messageType: " + messageType + ", " +
                "name: " + name + "}";
    }

    public static class MessageBuilder {
        private final String sender;
        private final MessageType messageType;
        private final String name;

        private String data = "";

        public MessageBuilder(String sender, MessageType messageType, String name){
            this.sender = sender;
            this.messageType = messageType;
            this.name = name;
        }

        public MessageBuilder data(String data){
            this.data = data;
            return this;
        }

        public Message build(){
            return new Message(this);
        }
    }
}
