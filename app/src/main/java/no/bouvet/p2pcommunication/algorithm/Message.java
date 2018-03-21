package no.bouvet.p2pcommunication.algorithm;

/**
 * Created by micha on 3/20/2018.
 */

public class Message {

    public final String sender;
    public final MessageType messageType;
    public final DataType dataType;
    public final String name;

    public final String stringData;
    public final BitmapData bitmapData;

    public Message(Message otherMessage){
        this.sender = otherMessage.sender;
        this.messageType = otherMessage.messageType;
        this.dataType = otherMessage.dataType;
        this.name = otherMessage.name;
        this.stringData = otherMessage.stringData;
        this.bitmapData = otherMessage.bitmapData;
    }

    public Message(MessageBuilder builder){
        this.sender = builder.sender;
        this.messageType = builder.messageType;
        this.dataType = builder.dataType;
        this.name = builder.name;
        this.stringData = builder.stringData;
        this.bitmapData = builder.bitmapData;
    }


//    public JSONObject toJson() {
//        try{
//            JSONObject json = new JSONObject();
//            json.put("sender", sender.toString());
//            json.put("messageType", messageType.toString());
//            json.put("dataType", dataType.toString());
//            json.put("name", name);
//            json.put("stringData", stringData);
//            System.out.println(json);
//            return json;
//        }
//        catch(JSONException ex){
//            ex.printStackTrace();
//        }
//        return null;
//
//    }
//
//    @Override
//    public String toString(){
//        // TODO implement better version
//        return "{sender: " + sender + ", " + "messageType: " + messageType + ", " +
//                "dataType: " + dataType + ", " + "name: " + name + "}";
//    }

    public static class MessageBuilder {
        private final String sender;
        private final MessageType messageType;
        private final String name;

        private DataType dataType;
        private String stringData = "";
        private BitmapData bitmapData;


        public MessageBuilder(String sender, MessageType messageType, String name){
            this.sender = sender;
            this.messageType = messageType;
            this.name = name;
        }

        public MessageBuilder dataType(DataType dataType){
            this.dataType = dataType;
            return this;
        }

        public MessageBuilder stringData(String data){
            this.stringData = data;
            return this;
        }

        public MessageBuilder bitmapData(BitmapData bitmapData){
            this.bitmapData = bitmapData;
            return this;
        }

        public Message build(){
            return new Message(this);
        }
    }
}
