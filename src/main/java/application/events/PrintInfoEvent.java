package application.events;

public class PrintInfoEvent extends PrintMessageEvent {

    public String getAttribute() {
        return "[INF]";
    }

}