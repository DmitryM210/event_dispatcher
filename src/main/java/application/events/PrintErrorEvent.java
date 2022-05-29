package application.events;

public class PrintErrorEvent extends PrintMessageEvent {

    public String getAttribute() {
        return "[ERR]";
    }

}