package edu.bu.ec504.spr19;

import java.util.Random;
import java.util.Vector;

/**
 * Represents an unordered vector with the following properties:
 * 0.  All elements *must* be different.
 * 1.  Some elements are null.
 * 2.  Each non-null elements contain a pointer to the next (non-null) element in sorted order.
 */
public abstract class ThreadedVector<BASE extends Comparable<? super BASE>> {

  // NESTED CLASSES

  /**
   * A wrapper for the BASE, representing one link in the linked-list representation.
   */
  class link {

    public link(BASE myDatum, Integer myNext) {
      datum=myDatum;
      next=myNext;
    }

    public String toString() {
      return datum.toString()+" [next: "+next+" ]";
    }

    /**
     * The datum stored in this link.
     */
    final BASE datum;

    /**
     * The index of the link containing the next larger datum.
     */
    Integer next;
  }

  /**
   * An exception thrown when accessing a null link.
   */
  public static class nullElementException extends Exception {
  }



  // CONSTRUCTORS
  /**
   * Construct the ThreadedVector object.
   */
  public ThreadedVector() {
    this(new Random());
  }

  /**
   * Construct ThreadedVector object with a given randomness source.
   * @param myRand A source of randomness for the data structure.
   */
  ThreadedVector(Random myRand) {
    rnd=myRand;
    size=0;

    data = new Vector<>();
    addNull();
  }


  // METHODS
  /**
   * Adds element <code>val</code> to this object.
   * @param val The element to be added.
   */
  final public void add(BASE val) {

    // 0. ensure enough room in the data structure
    while (data.size() < neededStorage()) {
      addNull();
    }

    // 1. find a place to put <code>val</code> in {@link #data}
    int newLoc=-1; // new location for element <code>val</code>; dummy initialization is used to pacify the compiler
    try {
      while (true) {
        newLoc = rnd.nextInt(neededStorage());
        getLink(newLoc);
      }
    } catch (nullElementException ignored) {
      // if we got here, then we found a null index in the array
    }

    // 2. find the next largest and next smallest items in the data structure
    Integer nextIndex = null; // the index of the smallest item larger than val
    Integer prevIndex = null; // the index of the largest item smaller than val
    for (int ii = 0; ii < getSize(); ii++) {
      link theLink = null;
      try {
        theLink = getLink(ii);
      } catch (nullElementException ignored) {
        continue; // skip this index, it is pointing to a null
      }
      if (theLink.datum.compareTo(val) > 0 &&          // theLink > val
          (nextIndex == null ||                         // nextIndex is defined
              theLink.datum.compareTo(data.get(nextIndex).datum) < 0))  {  // theLink < nextIndex
        nextIndex = ii;
      }
      if (theLink.datum.compareTo(val) <= 0 &&         // theLink <= val
          (prevIndex == null ||                        // prevIndex is defined
            (theLink.datum.compareTo(data.get(prevIndex).datum) > 0))) {  // theLink > prevIndex
        prevIndex = ii;
      }
    }

    // 3. package it all together
    link newLink = new link(val, nextIndex);
    try {
      if (prevIndex!=null)
        getLink(prevIndex).next = newLoc;
    } catch (nullElementException ignored) {
      // nothing needs to be updated
    }
    // put the package in the correct location
    data.set(newLoc, newLink);
    size++; // update the size counter
  }

  final public Boolean isNull(int index) {
    try {
      getLink(index);
    } catch (nullElementException ignored) {
      return true;
    }
    return false;
  }

  /**
   * Retrieve the i-th link in this object in constant time.
   * Note that this is not necessarily
   * @param index The index of the element to retrieve.
   * @return The link corresponding to the i-th
   * @throws nullElementException if accessing a null item in the array
   */
  final public link getLink(int index) throws nullElementException {
    getLinkCount++;

    link theLink = data.get(index);
    if (theLink==null)
      throw new nullElementException();

    return theLink;
  }

  /**
   * @param index The index of the element to retrieve.
   * @return the i-th element in this object in constant time.
   * @throws nullElementException if accessing a null item in the array
   */
  final public BASE getElement(int index) throws nullElementException {
    return getLink(index).datum;
  }

  /**
   * Retrieve the link containing the next larger element after the i-th element in this object in constant time.
   * @param index The index of the element whose next larger element we wish to retrieve.
   * @return The link representing the next larger item in the data structure.
   * @throws nullElementException if accessing a null item in the array
   */
  final public link getNextLarger(int index) throws nullElementException {
    return getLink(getLink(index).next);
  }

  /**
   * Same as {@link #getNextLarger(int)} but the parameter is a current link.
   */
  final public link getNextLarger(link current) throws nullElementException {
    if (current.next==null)
      throw new nullElementException();
    else
      return getLink(current.next);
  }

  /**
   * @return The number of elements currently stored in the array.
   */
  final public int getNumElements() {
    return size;
  }

  /**
   * @return The number of calls to getLink since this object was instantiated.
   */
  final public long getGetLinkCount() { return getLinkCount; }

  /**
   * @return The number of indexes addressable in the data structure.
   */
  final public int getSize() {
    return data.size();
  }

  /**
   * @return A pretty-printed representation of this object.
   */
  final public String toString() {
    StringBuilder result = new StringBuilder();
    for (int ii=0; ii<getSize(); ii++) {
      link theLink;
      try {
        theLink = getLink(ii);
        result.append(ii) // index
            .append(": ")
            .append(theLink)
            .append("\n");
      } catch (nullElementException ignored) {
        result.append(ii)
            .append(": null\n");
      }
    }
    return result.toString();
  }

  /**
   * @return The link containing the smallest datum in this object.
   */
  final public link getSmallest() {
    // mark each item that appears as a next index of something
    Boolean[] mark = new Boolean[getSize()];
    for (int ii=0; ii<getSize(); ii++) {
      try {
        mark[getLink(ii).next]=true;
      } catch (nullElementException | NullPointerException ignored) {
      }
    }

    for (int ii=0; ii<getSize(); ii++) {
      if (!isNull(ii) && mark[ii]==null) {
        try {
          return getLink(ii);
        } catch (nullElementException ignored) {
          // you shouldn't get here
        }
      }
    }

    return null; // there are no elements?
  }

  /**
   * @param datum The datum we are seeking.
   * @return true iff <code>datum</code> is in this object.
   */
  abstract Boolean search(BASE datum);

  /**
   * Adds a null element to {@link #data}.
   */
  private void addNull() {
    data.add(null);
  }

  /**
   * @return The amount of storage needed within {@link #data}.
   */
  private int neededStorage() {
    return (int) (1+size*PAD_FACTOR);
  }

  // FIELDS
  /**
   * Internal storage for this data structure.
   */
  private final Vector<link> data;

  /**
   * The number of non-null elements in the data structure.
   */
  private int size;

  /**
   * Randomness source.
   */
  private final Random rnd;

  /**
   * Keeps track of the number of getLink calls.
   */
  private long getLinkCount=0;


  // CONSTANTS

  /**
   * Multiply the underlying Vector by this fraction for padding in the array.
   */
  final private double PAD_FACTOR=2.0;
}
