package application;

import application.events.Event;
import application.events.PrintErrorEvent;
import application.events.PrintInfoEvent;
import application.events.PrintMessageEvent;
import application.dispatcher.*;

import java.util.function.Consumer;

public class Main {

    static {
        registerEvent(Event.class, (event) -> System.out.println("some event was raised!"));
        registerEvent(PrintMessageEvent.class, (event) -> event.printMessage("some message print!"));
        registerEvent(PrintErrorEvent.class, (event) -> event.printMessage("some error .."));
    }

    public static void main(String[] args) {
        var count = 4;
        for (var index = 0; index < count; index++) {
            if (index == count / 2)
                Dispatcher.dispatch(new PrintErrorEvent());
            else
                Dispatcher.dispatch(new PrintInfoEvent());
            System.out.println();
        }
    }

    private static <TEvent extends Event> void registerEvent(Class<TEvent> event, Consumer<TEvent> action) {
        Dispatcher.addHandler(event, new EventHandler<>(action));
    }

}