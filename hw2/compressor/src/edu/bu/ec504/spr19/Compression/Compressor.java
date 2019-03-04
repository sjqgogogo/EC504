package edu.bu.ec504.spr19.Compression;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public abstract class Compressor  implements Serializable {
	
	// METHODS
	/**
	 * Constructor - Initializes internal data structures.
	 */
	Compressor() {
		dict = new ArrayList<>();
		compressed = new ArrayList<>();
	}
	
	/**
	 * Reads data from input stream <code>in</code> into the Compressor object, overwriting
	 * any existing data.
	 * @param in An input stream to read.
	 * @modifies {@link Compressor#dict} - updates the dictionary
	 * @modifies {@link Compressor#compressed} - updates the compressed version of this object
	 */
	public abstract void readBuffer(BufferedReader in);
	
	/**
	 * Decodes the meta-data stored within this object into an uncompressed text.
	 * @return The uncompressed text corresponding to this object.
	 */
	public String unCompress() {
		// Go through each atom, in turn, decompressing its contents
		StringBuilder result= new StringBuilder();
		
		Enumeration<Atom> cEnum = Collections.enumeration(compressed);
		while (cEnum.hasMoreElements()) {
			Atom currentElement = cEnum.nextElement();
			result.append(dict.get(currentElement.getIndex())); 	  // the dictionary word
            result.append(currentElement.getNext());                  // next element
		}
		
		return result.toString();
	}
	
	// FIELDS
	private static final long serialVersionUID = 1L;

	/** A dictionary of words used in the compression. */
	final ArrayList<String> dict;

	/** A compressed version of the object, based on the dictionary {@link Compressor#dict}.
	 Each Atom (containing [index], [next]) represents a substring of the uncompressed text, interpreted
	 as the [index]-th word in {@link Compressor#dict} followed by the string [next].*/
	final ArrayList<Atom> compressed;


    // NESTED CLASSES
    /**
     * An immutable atom of compression includes:
     * a.  An integer representing an index of the dictionary {@link Compressor#dict}
     * b.  A string that follows the dictionary word
     */
    final  class Atom implements Serializable {
        private static final long serialVersionUID = 2L;
        Atom(int myIndex, String myNext) { index = myIndex; next = myNext; }
        int getIndex() { return index; }
        String getNext() { return next; }
        private final int index;
        private final String next;
    }
}
