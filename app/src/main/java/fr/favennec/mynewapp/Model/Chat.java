package fr.favennec.mynewapp.Model;

public class Chat {


    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;
    private String chatid;
    private String hour;

    public Chat(String sender, String receiver, String message, Boolean isseen, String chatid, String hour) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.chatid = chatid;
        this.hour = hour;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
