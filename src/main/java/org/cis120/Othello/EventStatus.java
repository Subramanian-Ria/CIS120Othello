package org.cis120.Othello;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

//event status enum allows broadcasting of important events to RunOthello so that JOption panes can be displayed in the frame
enum EventStatusEnum
{
    ERROR, WIN_BLACK, WIN_WHITE, TIE, PASS_BLACK, PASS_WHITE
}
public class EventStatus {

    protected PropertyChangeSupport propertyChangeSupport;
    private EventStatusEnum event;

    public EventStatus ()
    {
        propertyChangeSupport = new PropertyChangeSupport(this);
        event = null;
    }

    public void setEvent(EventStatusEnum eventParam)
    {
        EventStatusEnum oldEvent = this.event;
        this.event = eventParam;
        propertyChangeSupport.firePropertyChange("EventStatus", oldEvent, this.event);
    }

    public void setEventNull()
    {
        setEvent(null);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
