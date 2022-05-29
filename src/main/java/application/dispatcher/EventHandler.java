package application.dispatcher;

import application.events.Event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EventHandler<TEvent extends Event> {

    private final Set<Consumer<TEvent>> actions;

    public EventHandler(Consumer<TEvent>... actions) {
        this.actions = new HashSet<Consumer<TEvent>>();
        Collections.addAll(this.actions, actions);
    }

    public static <TOtherEvent extends Event> EventHandler<TOtherEvent> createFor(Class<TOtherEvent> event) {
        return new EventHandler<>();
    }

    public void appendAction(Consumer<TEvent> action) {
        this.actions.add(action);
    }

    public EventHandler<TEvent> union(EventHandler<TEvent> other) {
        var newEventHandler = new EventHandler<TEvent>();
        newEventHandler.actions.addAll(this.actions);
        newEventHandler.actions.addAll(other.actions);
        return newEventHandler;
    }

    public void invoke(TEvent event) {
        for (var action : this.actions) {
            action.accept(event);
        }
    }

}