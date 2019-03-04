package edu.bu.ec504.spr19.Compression;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class myCompressor extends Compressor {

	@Override
	public void readBuffer(BufferedReader in) {
		// DO SOMETHING HERE

		String str, dictstr;
		int saved=0;
		try {
			while ((str = in.readLine()) != null) {
				saved = 0;
				if(dict.size()==0) {
					dict.add(str);
					continue;
				}
				dictstr = str;
				while(true) {
					for(int ii=0;ii<dict.size();ii++) {
						saved = 0;
						if (dict.get(ii) == dictstr) {
							saved = 1;
							break;
						}
					}
					if (saved == 0) {
						dict.add(dictstr);
						//compressed.add(new Atom(dict.size()-1,"\n"));
						break;
					}
					else {
						str = in.readLine();
						dictstr = dictstr + str;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}





		/*String str;
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

		*/





	}
	public String unCompress() {
		// simply spits out the dictionary
		StringBuilder result = new StringBuilder();
		for (String s : dict) result.append(s);
		return result.toString();
	}
}
