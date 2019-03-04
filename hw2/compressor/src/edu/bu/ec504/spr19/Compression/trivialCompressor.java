package edu.bu.ec504.spr19.Compression;

import java.io.BufferedReader;
import java.io.IOException;

public class trivialCompressor extends Compressor {
	private static final long serialVersionUID = 1L;

	public trivialCompressor() { super(); }
	
	@Override
	public void readBuffer(BufferedReader in) {
		// Read in strings, one by one, and add them as dictionary entries.
		String str;
		try {
			while ((str = in.readLine()) != null) {
				dict.add(str); // add to the dictionary
				compressed.add(new Atom(dict.size()-1,"\n")); /* adds an atom pointing
					to the recently created dictionary entry, and a newline <next> string.
					*/
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
