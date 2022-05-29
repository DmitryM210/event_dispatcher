package application.dispatcher;

import application.events.Event;

import java.util.HashMap;
import java.util.List;

public class Dispatcher {

    private final static HashMap<Class<? extends Event>, List<Class<? extends Event>>> superclasses;
    private final static HashMap<Class<? extends Event>, EventHandler<? extends Event>> handlers;

    static {
        superclasses = new HashMap<>();
        handlers = new HashMap<>();
        EventRegistrar.registerAllEvents(superclasses, handlers);
    }

    public static <TEvent extends Event> void dispatch(TEvent event) {
        var eventSuperclasses = superclasses.get(event.getClass());
        for (var superclass : eventSuperclasses) {
            EventHandler<TEvent> handler = (EventHandler<TEvent>)handlers.get(superclass);
            if (handler != null)
                handler.invoke(event);
        }
    }

    public static <TEvent extends Event> void addHandler(Class<TEvent> event, EventHandler<TEvent> handler) {
        EventHandler<TEvent> foundHandler = (EventHandler<TEvent>)handlers.get(event);
        if (foundHandler != null)
            handlers.put(event, foundHandler.union(handler));
    }

}