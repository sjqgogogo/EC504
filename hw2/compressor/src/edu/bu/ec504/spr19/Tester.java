package edu.bu.ec504.spr19;

import edu.bu.ec504.spr19.Compression.Compressor;
import edu.bu.ec504.spr19.Compression.dumbCompressor;
import edu.bu.ec504.spr19.Compression.myCompressor;
import edu.bu.ec504.spr19.Compression.trivialCompressor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

public class Tester {
    // MAIN method
	public static void main(String[] args) {
		try {
			testResult result = testUrl(URLExample, new myCompressor()); // change this to myCompressor() to test your own compressor
			System.out.println("Encoding time: "+(result.runTime/1000.0)+" seconds");
			System.out.println("Compression ratio:  "+ (result.compressRatio * 100.0)+"%");
			if (result.testPass)
				System.out.println("Decoding test passed!");
			else
				System.out.println("Decoding test FAILED!!!");
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// METHODS
	/**
	 * Performs a test of encoding and decoding a given URL
	 * @param inURL The URL to test - must be http only (no https handshaking)
	 * @param testCompressor The compressor object to test
	 * @return An encapsulation of:
	 * (i) The amount of time needed to decoded
	 * (ii) The compression ratio
	 * (iii) Whether the decoding test passed (true iff passed)
	 * @throws IOException If one of the files (plain, compressed, or decompressed) cannot be opened appropriately.
	 * @throws ClassNotFoundException for a deserialization error.
	 */
	private static testResult testUrl(String inURL, Compressor testCompressor) throws IOException, ClassNotFoundException {
		// constants
		final String plainFile = "plain.txt";              // the plain text of the URL input
		final String compressedFile = "compressed.txt";    // the file where the compression of the URL data is put
		final String decompressedFile = "decompressed.txt";// the file where the decoding of the encodedFile is put

		// variables
		long initTime, endTime;        // used to time the encoding process
		long origSize, compressedSize; // used to compare the uncompressed and compressed file sizes
		File theFile;                  // used for File queries

		URL uu = new URL(inURL);

		// 0. Read the URL into a file and record the file length
		// ... based on https://alvinalexander.com/blog/post/java/simple-https-example
		InputStream plainStream = uu.openStream();
		OutputStream out = new FileOutputStream(plainFile);
		byte[] buf = new byte[1024];
		int len;
		while ((len = plainStream.read(buf)) > 0) {
			out.write(buf,0,len);
		}
		plainStream.close();
		out.close();
		// ... record the file name
		theFile = new File(plainFile);
		origSize = theFile.length();

		// 1. (Re-)read [from the file] and encode the input URL
		BufferedReader in = new BufferedReader( new InputStreamReader(new FileInputStream(plainFile)));
		initTime = System.currentTimeMillis();
		testCompressor.readBuffer(in);
		endTime = System.currentTimeMillis();

		// 2. Serialize the compressed object into a file
		ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(compressedFile));
		outStream.writeObject(testCompressor);
		outStream.close();
		// ... get the size of the file
		theFile = new File(compressedFile);
		compressedSize = theFile.length();

		// 3. Read the coded object from the file and decode it (should provide the same as the input URL)
		ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(compressedFile));
		Compressor decoded = (Compressor) inStream.readObject();
		inStream.close();
		PrintStream outDecode = new PrintStream(new FileOutputStream(decompressedFile));
		outDecode.println(decoded.unCompress());
		outDecode.close();

		// 4. Perform check and output results
		System.out.println("Original size: " + origSize + " - Compressed size:" + compressedSize);
		// ... check that the original and the decoded(encoded(original)) are the same
		if (identical(plainFile, decompressedFile))
			return new testResult(endTime - initTime, (double) origSize / compressedSize, true);
		else
			return new testResult(endTime - initTime, (double) origSize / compressedSize, false);
	}

	/**
	 * Checks whether <code>file1</code> and <code>file2</code> are identical without consideration of termination characters ('\n','\r')
	 * @param file1 one of the files to compare
	 * @param file2 one of the files to compare
	 * @return true iff the contents of file1 and file2 are identical modulo termination characters.
	 */
	private static boolean identical(String file1, String file2) {
		// ... also based on http://www.exampledepot.com/egs/java.io/CopyFile.html
		String sf1, sf2;
		try {
			sf1 = readFile(file1); sf1 = sf1.replaceAll("[\n\r]","");
			sf2 = readFile(file2); sf2 = sf2.replaceAll("[\n\r]","");
		}
		catch (IOException e) {return false;} // any exceptions are treated as a lack of a match

		return sf1.compareTo(sf2)==0;

	}

	/**
	 * Reads a file into a string
	 * @param file1 the file to read
	 * @return The string representing the file
	 * @throws IOException If file reading fails.
	 */
	private static String readFile(String file1) throws IOException {
		StringBuilder data = new StringBuilder(1024);
		BufferedReader rd = new BufferedReader(new FileReader(file1));
		char[] buf = new char[1024];

		int len;
		while ((len = rd.read(buf)) != -1)
			data.append(buf, 0, len);

		rd.close();
		return data.toString();
	}

	// FIELDS
    private final static String URLExample = "http://www.webopedia.com/TERM/H/HTTP.html"; // the default URL to test - must be HTTP (not https)

    // NESTED CLASSES

	/**
	 * A class for storing test results.
	 */
	public static class testResult {
		testResult(long theRunTime, double theCompressRatio, boolean theTestPass) {
			runTime=theRunTime;
			compressRatio=theCompressRatio;
			testPass=theTestPass;
		}


		/**
		 * How long the test ran, in milliseconds.
		 */
		final long runTime;

		/**
		 * The compression ratio achieved by the test.
		 */
		final double compressRatio;

		/**
		 * Whether the test passed.
		 */
		final boolean testPass;
	}

}
