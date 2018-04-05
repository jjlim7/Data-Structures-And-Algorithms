import java.io.Serializable;

public class EventCollection implements Serializable {
    
    private LinkList eventsList;

    public EventCollection() {
        eventsList = new LinkList();
    }
    
    public ListNode getHeadNode() {
        return eventsList.getHeadNode();
    }
    
    public int getNoOfEvents() {
        return eventsList.getNoOfElement();
    }

    public Event getEvent(int index) {
        return (Event) eventsList.get(index);
    }
    
    public LinkList getEventsList() {
        return this.eventsList;
    }
    
    public void setEventsList(LinkList eventsList) {
        this.eventsList = eventsList;
    }
    
    public void addEvents(Event event) {
        eventsList.addLast(event);
    }
    
    public void removeEvent(Event event) {
        eventsList.removeByObject(event);
    }
    
    public boolean checkIfEventExists(String eventTitle) {
        for (int i = 0; i < eventsList.getNoOfElement(); i++) {
            Event e = (Event) eventsList.get(i);
            if (e.getEventTitle().equals(eventTitle))
                return true;
        }
        return false;
    }
    
    public boolean search(ListNode head, String search) {
        
        String eventTitle = ((Event)head.getData()).getEventTitle();
        
        if (head.getNext() == null)
            return false;
        
        if (eventTitle.toLowerCase().contains(search))
            return true;
        
        return search(head.getNext(), search);
    }
}
