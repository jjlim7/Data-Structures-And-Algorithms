import java.io.Serializable;

/**
 *
 * @author LIM JING JIE
 */

public class ListNode implements Serializable {
    
    private ListNode next = null;
    private Object data = null;
    
    public ListNode(Object object) {
        this(object,null);
    }
    
    public ListNode(Object object, ListNode nextNode) {
        this.data = object;
        this.next = nextNode;
    }
    
    public void setData(Object newData) {
        data = newData;
    }

    public Object getData() {
        return data;
    }

    public void setNext(ListNode next) {
        this.next = next;
    }

    public ListNode getNext() {
        return next;
    }
}
