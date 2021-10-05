import tester.Tester;

//Represents a boolean-valued question over values of type T
interface IPred<T> {
  boolean apply(T t);
}

// checks if the string starts with c
class StartsWithC implements IPred<String> {
  public boolean apply(String string) {
    char firstletter = string.charAt(0);
    return firstletter == ('c');
  }
}

// checks if the string starts with e
class StartsWithE implements IPred<String> {
  public boolean apply(String string) {
    char firstletter = string.charAt(0);
    return firstletter == ('e');
  }
}

// checks if the string has more than 4 characters
class LongerThanFour implements IPred<String> {
  public boolean apply(String string) {
    return string.length() > 4;
  }
}

// represents a deque
class Deque<T> {
  Sentinel<T> header;

  // zero-arg constructor
  Deque() {
    this.header = new Sentinel<T>();
  }

  // convenience constructor
  Deque(Sentinel<T> sentinel) {
    this.header = sentinel;
  }

  // returns the size of this deque not including the sentinel
  int size() {
    return this.header.countNodes();
  }

  // Effect: adds a node to the front of the list
  void addAtHead(T t) {
    this.header.addNext(t);
  }

  // Effect: adds a node to the front of the list
  void addAtTail(T t) {
    this.header.addBefore(t);
  }

  // removes and returns the first node from the deque
  T removeFromHead() {
    return this.header.removeHead();
  }

  // removes and returns the last node from the deque
  T removeFromTail() {
    return this.header.removeTail();
  }

  // returns the first node in the deque which the pred returns true, or the header
  // if none return true
  ANode<T> find(IPred<T> pred) {
    return this.header.findHelp(pred, false);
  }

  // Effect: removes the given anode from the deque, if given
  // a sentinel it does nothing
  void removeNode(ANode<T> node) {
    node.removeNodeHelp();
  }

  // Effect: reverses a deque
  void reverse() {
    this.header.reverseHelp(false);
  }

}

// to represent a node in a deque
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  // default constructor
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  // zero-arg constructor
  ANode() {
    this.next = this;
    this.prev = this;
  }

  // Effect: updates a node's next item
  public void updateNext(ANode<T> other) {
    this.next = other;
  }

  // Effect: updates a node's prev item
  public void updatePrev(ANode<T> other) {
    this.prev = other;
  }

  ANode<T> getNext() {
    return this.next;
  }

  ANode<T> getPrev() {
    return this.prev;
  }

  // counts the number of nodes in a deque
  int countNodes() {
    return this.countNodesHelp(false);
  }

  // helper: keeps track of if the sentinel has been seen already
  abstract int countNodesHelp(Boolean seen);

  // Effect: add a node between this anode and the next anode
  void addNext(T t) {
    new Node<T>(t, this.next, this);
  }

  // add a node before this anode and the prev anode
  void addBefore(T t) {
    new Node<T>(t, this, this.prev);
  }


  // removes the next and returns the removed node's data
  // ensure that the list is not empty
  public T removeHead() {
    if (this.countNodes() == 0) {
      throw new RuntimeException("this deque is empty");
    } else {
      T t = this.next.getData();
      this.removeNext();
      return t;
    }
  }

  // removes the previous and returns the removed node's data
  // ensure that the list is not empty
  public T removeTail() {
    if (this.countNodes() == 0) {
      throw new RuntimeException("this deque is empty");
    } else {
      T t = this.prev.getData();
      this.removePrev();
      return t;
    }
  }

  // Effect: removes the next anode from the deque
  void removeNext() {
    this.next.removeNextHelp();
    this.updateNext(this.next.getNext());
  }

  // Effect: removes this from the next anode's previous
  void removeNextHelp() {
    this.next.updatePrev(this.prev);
  }

  // Effect: removes the prev node from the deque
  void removePrev() {
    this.prev.removePrevHelp();
    this.updatePrev(this.prev.getPrev());
  }

  // Effect: removes this from the prev anode's previous
  void removePrevHelp() {
    this.prev.updateNext(this.next);
  }

  // finds the first node for which the pred returns true, 
  // or the sentinel if none are true
  // keeps track of if the sentinel has already been seen
  abstract ANode<T> findHelp(IPred<T> pred, boolean seen);

  // removes the given anode from the deque, if given
  // a sentinel it does nothing
  void removeNodeHelp() {
    /* this method is empty because in the case of a sentinel, it does
     not need to do anything. It is overriden in the Node class */
  }


  abstract T getData();

  abstract void reverseHelp(boolean seen);

  void reverseThis() {
    ANode<T> temp = this.prev;
    this.next = this.prev;
    this.prev = temp;
  }
}

class Sentinel<T> extends ANode<T> {
  // zero-arg constructor, the next and prev fields are itself
  Sentinel() {
    super();
  }

  // counts the nodes in a deque, either adds the nodes or stops the recursion
  public int countNodesHelp(Boolean seen) {
    if (seen) {
      return 0;
    } else {
      return this.next.countNodesHelp(true);
    }
  }


  // finds the first node for which the pred returns true, 
  // or the sentinel if none are true
  // keeps track of if the sentinel has been seen
  public ANode<T> findHelp(IPred<T> pred, boolean seen) {
    if (seen) {
      return this;
    } else {
      return this.next.findHelp(pred, true);
    }
  }

  T getData() {
    return null;
  }

  @Override
  void reverseHelp(boolean seen) {
    if (!seen) {
      this.next.reverseHelp(true);
      this.reverseThis();
    }

  }
}

class Node<T> extends ANode<T> {
  T data;

  // 1-arg constructor, fills both nodes with null
  Node(T data) {
    super(null, null);
    this.data = data;
  }

  // convenience constructor, updates the other two nodes
  Node(T data, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    if (next == null) {
      throw new IllegalArgumentException("next node is null");
    } else if (prev == null) {
      throw new IllegalArgumentException("previous node is null");
    } else {
      this.data = data;

      this.next.updatePrev(this);
      this.prev.updateNext(this);
    }
  }

  // counts this node in a deque and adds it to the rest of the nodes
  int countNodesHelp(Boolean seen) {
    return 1 + this.next.countNodesHelp(seen);
  }

  // finds the first node for which the pred returns true, 
  // or the sentinel if none are true
  // keeps track of if the sentinel has been seen
  public ANode<T> findHelp(IPred<T> pred, boolean seen) {
    if (pred.apply(this.data)) {
      return this;
    } else {
      return next.findHelp(pred, seen);
    }
  }

  //removes the given anode from the deque
  void removeNodeHelp() {
    this.prev.removeNext();
  }

  @Override
  T getData() {
    return this.data;
  }

  @Override
  void reverseHelp(boolean seen) {
    this.next.reverseHelp(seen);
    this.reverseThis();

  }

}

class ExamplesDeque {
  Sentinel<String> sentinel1;

  Node<String> abc;
  Node<String> bcd;
  Node<String> cde;
  Node<String> def;

  Sentinel<String> sentinel2;

  Node<String> dog;
  Node<String> cat;
  Node<String> boy;
  Node<String> apple;

  Sentinel<String> sentinel3;
  Node<String> testadd;

  Sentinel<String> sentinel4;

  Deque<String> deque1;
  Deque<String> deque2;
  Deque<String> deque3;
  Deque<String> deque4;
  Deque<String> deque5;
  Deque<String> deque6;
  Deque<String> deque7;
  Deque<String> dequemt;

  Sentinel<String> sentinel1a;
  Node<String> testnode1;
  Sentinel<String> sentinel1b;
  Node<String> testnode2;
  Sentinel<String> sentinel1c;
  Node<String> testnode3;
  Sentinel<String> sentinelmt;

  void initDeque() {
    sentinel1 = new Sentinel<String>();

    abc = new Node<String>("abc", sentinel1, sentinel1);
    bcd = new Node<String>("bcd", sentinel1, abc);
    cde = new Node<String>("cde", sentinel1, bcd);
    def = new Node<String>("def", sentinel1, cde);

    sentinel2 = new Sentinel<String>();

    dog = new Node<String>("dog", sentinel2, sentinel2);
    cat = new Node<String>("cat", sentinel2, dog);
    boy = new Node<String>("boy", sentinel2, cat);
    apple = new Node<String>("apple", sentinel2, boy);

    sentinel3 = new Sentinel<String>();
    testadd = new Node<String>("test", sentinel3, sentinel3);

    deque1 = new Deque<String>();
    deque2 = new Deque<String>(sentinel1);
    deque3 = new Deque<String>(sentinel2);
    deque4 = new Deque<String>(sentinel3);

  }

  void reverseDeque() {
    sentinel4 = new Sentinel<String>();

    def = new Node<String>("def", sentinel4, sentinel4);
    cde = new Node<String>("cde", sentinel4, def);
    bcd = new Node<String>("bcd", sentinel4, cde);
    abc = new Node<String>("abc", sentinel4, bcd);

    deque7 = new Deque<String>(sentinel4);

  }


  void testReverse(Tester t) {
    initDeque();
    deque1.reverse();
    deque2.reverse();
    reverseDeque();
    t.checkExpect(deque1, new Deque<String>());
    t.checkExpect(deque2, deque7);
  }

  boolean testExceptions(Tester t) {
    initDeque();

    return t.checkConstructorException(new IllegalArgumentException("next node is null"), "Node",
        "test", null, new Sentinel<String>());
  }

  boolean testExamples(Tester t) {
    initDeque();

    return t.checkExpect(bcd, new Node<String>("bcd", cde, abc))
        && t.checkExpect(def, new Node<String>("def", sentinel1, cde));
  }

  boolean testSize(Tester t) {
    initDeque();

    return t.checkExpect(deque1.size(), 0) && t.checkExpect(deque2.size(), 4);
  }

  void addHeadDeque() {
    initDeque();
    sentinel1a = sentinel1;
    testnode1 = new Node<String>("test", abc, sentinel1a);

    deque2.addAtHead("test");
  }

  void removeHeadDeque() {
    initDeque();
    sentinel1b = sentinel1;
    testnode2 = new Node<String>("test", sentinel1b, abc);

    deque5 = new Deque<String>(sentinel1b);

    deque5.removeFromHead();
  }

  void testAddHead(Tester t) {
    addHeadDeque();

    t.checkExpect(deque2, new Deque<String>(sentinel1a));
  }

  void testRemoveHead(Tester t) {
    removeHeadDeque();

    t.checkExpect(deque5, new Deque<String>(sentinel1));
    t.checkExpect(deque5.removeFromHead(), "test");
    t.checkException(new RuntimeException("this deque is empty"), new Sentinel<String>(),
        "removeHead");
  }

  void addTailDeque() {
    initDeque();
    sentinel1b = sentinel1;
    testnode2 = new Node<String>("test", sentinel1b, def);

    deque2.addAtTail("test");
  }

  void removeTailDeque() {
    initDeque();
    sentinel1b = sentinel1;
    testnode2 = new Node<String>("test", def, sentinel1b);

    deque5 = new Deque<String>(sentinel1b);

    deque5.removeFromTail();
  }

  void testAddTail(Tester t) {
    addTailDeque();

    t.checkExpect(deque2, new Deque<String>(sentinel1b));
  }

  void testRemoveTail(Tester t) {
    removeTailDeque();

    t.checkExpect(deque5, new Deque<String>(sentinel1));
    t.checkExpect(deque5.removeFromTail(), "test");
    t.checkException(new RuntimeException("this deque is empty"), new Sentinel<String>(),
        "removeTail");

  }

  void testFind(Tester t) {
    initDeque();

    t.checkExpect(deque2.find(new StartsWithC()), cde);
    t.checkExpect(deque3.find(new StartsWithC()), cat);
    t.checkExpect(deque1.find(new StartsWithC()), new Sentinel<String>());
    t.checkExpect(deque2.find(new StartsWithE()), sentinel1);
    t.checkExpect(deque3.find(new StartsWithE()), sentinel2);
    t.checkExpect(deque1.find(new StartsWithE()), new Sentinel<String>());

    t.checkExpect(deque2.find(new LongerThanFour()), sentinel1);
    t.checkExpect(deque3.find(new LongerThanFour()), apple);
    t.checkExpect(deque1.find(new LongerThanFour()), new Sentinel<String>());
  }

  void removeNode() {
    initDeque();

    sentinel1c = sentinel1;
    testnode3 = new Node<String>("test", def, cde);

    deque6 = new Deque<String>(sentinel1c);
    deque6.removeNode(testnode3);

    sentinelmt = new Sentinel<String>();
    dequemt = new Deque<String>(sentinelmt);

    dequemt.removeNode(testnode3);
  }

  void testRemove(Tester t) {
    removeNode();

    t.checkExpect(deque6, new Deque<String>(sentinel1));
    t.checkExpect(dequemt, new Deque<String>(sentinelmt));
  }

}
