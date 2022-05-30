package application;

import domain.Event;
import application.events.PrintErrorEvent;
import application.events.PrintInfoEvent;
import application.events.PrintMessageEvent;
import domain.dispatcher.*;

public class Main {

    private final static Dispatcher dispatcher;

    static {
        dispatcher = new DispatcherBuilder()
                .registerAllEvents()
                .withHandler(Event.class, (event) -> System.out.println("some event was raised!"))
                .withHandler(PrintMessageEvent.class, (event) -> event.printMessage("some message print!"))
                .withHandler(PrintErrorEvent.class, (event) -> event.printMessage("some error .."))
                .build();
    }

    public static void main(String[] args) {
        var count = 4;
        for (var index = 0; index < count; index++) {
            if (index == count / 2)
                dispatcher.dispatch(new PrintErrorEvent());
            else
                dispatcher.dispatch(new PrintInfoEvent());
            System.out.println();
        }
    }

}