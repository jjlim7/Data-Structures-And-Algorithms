
import java.io.Serializable;

public class LinkList implements Serializable {

    private int noOfElement = 0;	// Number of elements in the list
    private ListNode headnode = null;	// Headnode of the LinkList

    public LinkList() {
    }

    public LinkList(Object data) {
        add(0, data);
    }

    public ListNode getHeadNode() {
        return headnode;
    }

    public boolean isEmpty() {
        return headnode == null;
    }

    public int getNoOfElement() {
        return noOfElement;
    }

    public ListNode find(int index) {
        //Index starts from 0
        ListNode currnode = headnode;
        for (int i = 0; i < index; i++) {
            currnode = currnode.getNext();
        }
        return currnode;
    }

    public ListNode findLast() {
        ListNode currnode = headnode;
        while (currnode.getNext() != null) {
            currnode = currnode.getNext();
        }
        return currnode; //currnode Next should be null
    }

    public Object get(int index) throws IndexOutOfBoundsException {
        if (index >= 0 && index < noOfElement) {
            ListNode currnode = find(index);
            Object data = currnode.getData();
            return data;
        } else {
            throw new IndexOutOfBoundsException("index out of bounds exception on get");
        }
    }

    public Object getLast() {
        ListNode currnode = findLast();
        Object data = currnode.getData();
        return data;
    }

    public void add(int index, Object data) {
        if (index >= 0 && index <= noOfElement) {
            if (index == 0) {
                ListNode newnode = new ListNode(data, headnode);
                headnode = newnode;
            } else {
                ListNode prevnode = find(index - 1);
                ListNode nextnode = new ListNode(data, prevnode.getNext());
                prevnode.setNext(nextnode);
            }
            noOfElement++;
        } else {
            throw new IndexOutOfBoundsException("index out of bounds exception on get");
        }
    }

    public void addLast(Object data) {
        add(noOfElement, data);
    }

    public void removeAll() {
        headnode = null; //Remove links from all nodes
        noOfElement = 0;
    }

    public void remove(int index) {
        if (index >= 0 && index < noOfElement) {
            if (index == 0) {
                headnode = headnode.getNext(); //Remove first node from list
            } else {
                ListNode prevnode = find(index - 1);  //Get Previous Node
                ListNode currnode = prevnode.getNext(); //Get Current Node
                prevnode.setNext(currnode.getNext());   //Remove link between previous and current node
            }
            noOfElement--;
        } else {
            throw new IndexOutOfBoundsException("index out of bounds exception on get");
        }
    }

    public void removeByObject(Object data) {
        for (int i = 0; i < noOfElement; i++) {
            if (get(i).equals(data)) {
                remove(i);
            }
        }
    }

    public ListNode sortedMerge(ListNode left, ListNode right) {
        ListNode result = null;

        //Base cases
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }

        Event event1 = (Event) left.getData();
        Event event2 = (Event) right.getData();
        
        //Get either left or right list and recur
        if (event1.getEventTitle().toUpperCase().compareTo(event2.getEventTitle().toUpperCase()) <= 0) {
            result = left;
            result.setNext(sortedMerge(left.getNext(), right));
        } else {
            result = right;
            result.setNext(sortedMerge(left, right.getNext()));
        }
        return result;
    }

    public ListNode mergeSort(ListNode head) {
        //Base - If head is null
        if (head == null || head.getNext() == null) {
            return head;
        }

        ListNode mid = getMiddle(head); //Get mid of list
        ListNode nextofmiddle = mid.getNext();

        //Set next of mid node to null
        mid.setNext(null);

        //Merge Sort both left and right list
        ListNode left = mergeSort(head);
        ListNode right = mergeSort(nextofmiddle);

        // Merge the left and right lists
        ListNode sortedlist = sortedMerge(left, right);
        this.headnode = sortedlist;
        return sortedlist;
    }

    //Get the middle of the linked list
    public ListNode getMiddle(ListNode head) {
        //Base case
        if (head == null) {
            return head;
        }
        ListNode fast = head.getNext();
        ListNode slow = head;

        //Move fast pointer by two and slow pointer by one
        //The slow pointer will point to the middle node
        while (fast.getNext() != null && fast.getNext().getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }
        return slow;
    }
}
