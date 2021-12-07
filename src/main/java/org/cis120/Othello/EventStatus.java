package org.cis120.Othello;

import sun.jvm.hotspot.utilities.Observable;

//event status enum allows broadcasting of important events to RunOthello so that JOption panes can be displayed in the frame
enum EventStatusEnum
{
    ERROR, WIN_BLACK, WIN_WHITE, TIE, PASS_BLACK, PASS_WHITE
}
public class EventStatus extends Observable {
    private EventStatusEnum event;
    public EventStatus ()
    {
        event = null;
    }

    public EventStatusEnum getEvent()
    {
        return event;
    }

    public void setEvent(EventStatusEnum param)
    {
        event = param;
        notifyObservers(param);
    }

    public void setEventNull()
    {
        event = null;
        notifyObservers(null);
    }

    public void addObserver()
    {

    }

    public void notifyObservers(EventStatusEnum param)
    {

    }
}
