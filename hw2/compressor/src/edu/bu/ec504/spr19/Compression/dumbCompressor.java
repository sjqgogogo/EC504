package edu.bu.ec504.spr19.Compression;

import java.io.BufferedReader;
import java.io.IOException;

public class dumbCompressor extends Compressor {

	private static final long serialVersionUID = 1L;

	@Override
	public void readBuffer(BufferedReader in) {
		// Stores all the data, in chunks, in the dictionary; ignores the <compressed> variable
		String str;
		try {
			while ((str = in.readLine()) != null) {
				dict.add(str); // add to the dictionary
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String unCompress() {
		// simply spits out the dictionary
		StringBuilder result= new StringBuilder();
		for (String s : dict) result.append(s);
		return result.toString();
	}

}
