package domain.dispatcher;

import domain.Event;
import domain.EventHandler;

import java.util.HashMap;

public class Dispatcher {

    private final HashMap<Class<? extends Event>, EventHandler<? extends Event>> handlers;

    protected Dispatcher(HashMap<Class<? extends Event>, EventHandler<? extends Event>> handlers) {
        this.handlers = handlers;
    }

    public <TEvent extends Event> void dispatch(TEvent event) {
        EventHandler<TEvent> handler = (EventHandler<TEvent>) handlers.get(event.getClass());
        if (handler != null)
            handler.invoke(event);
    }

}