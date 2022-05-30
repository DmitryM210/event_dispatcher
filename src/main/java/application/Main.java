package application;

/*
* Суть задачи в следующем: представим, что у нас есть интерфейс Event.
* Для объектов, расширяющих этот интерфейс есть обработчики EventHandler<? extends Event>.
* EventHandler может быть привязан к какому-то конкретному типу, расширяющему Event,
* причём этот тип не обязан быть последним в иерархии классов
* (иерархия: B наследует A. EventHandler<B> обрабатывает только B, EventHandler<A> обрабатывает A и B).
* Допустим, что в некотором классе Dispatcher у нас собраны в список все имеющиеся
* обработчики (их n штук). У Dispatcher есть метод dispatch(Event event)
*
* Задача:
* Реализовать Dispatcher и метод dispatch таким образом, чтобы он за O(1),
* край за O(log n) находил нам EventHandler, соответствующий полученному Event.
*  КРАЙНЕ ЖЕЛАТЕЛЬНО иметь низкие затраты по памяти
* (например, не иметь Map<Event, List<EventHandler>> для всех возможных
* отображений из событий в те обработчики, которые их могут обрабатывать,
* иначе задача всякий смысл теряет)
*
* Дополнительно:
* Можно использовать рефлексию, считать, что у нас есть возможность сканировать
* дерево классов проекта и т.д.
*
* Пояснения ..
* Важная алгоритмия по этой задаче заключается в том, что на подготовку
* мы можем тратить достаточно продолжительное время.
* То есть подготовка подразумивает старт раоты диспетчера, а уже обрабатывать
* запросы мы должны за конкретные ограничения по времени (алгомитрические).
* Вся сложность в большей степени заключается в том, что в условиях иирархии
* нам нужно хорошо подобрать, быстро найти нужный нам EventHandler.
* */

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