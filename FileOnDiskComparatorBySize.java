package project4;

import java.io.IOException;
import java.util.Comparator;
/**
 * This class compares two FileOnDisk objects. It compares them first by size
 * (number of bytes) and then by their pathnames lexicographically.
 * 
 * @author Anna Prenowitz
 * @version 11-10-2023 
 */
public class FileOnDiskComparatorBySize implements Comparator<FileOnDisk> {
	
	/**
     * This method takes the two FileOnDisk objects, compares them, and returns
     * different values to indicate relative size.
     * @param o1, o2 are the two FileOnDisk objects taken to be compared.
     * @return int indicating relative size of the objects:
     * -1, 0, and 1 depending on if o1 is less than, equal to, or greater than o2.
     */ 
	public int compare (FileOnDisk o1, FileOnDisk o2) {
		
		// Uses File.length() to find and compare size in bytes.

		if (o1.length() < o2.length()) {
			return 1;
		} else if (o1.length() > o2.length()) {
			return -1;
		}
		
		// If equal in size, compares path names of files lexicographically.
		
		else {
			try {
				return o1.getCanonicalPath().compareTo(o2.getCanonicalPath());
			}
			
			// Catch IOException thrown by getCanonicalPath() method.
			catch (IOException ex) {
				System.err.println("Cannot get cannonical path.");
			}
		}
		return 1;
	}
}
