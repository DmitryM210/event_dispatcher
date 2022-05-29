package application.events;

public class PrintMessageEvent implements Event {

    public final void printMessage(String text) {
        System.out.println(getAttribute() + " " + text);
    }

    public String getAttribute() {
        return "";
    }

}