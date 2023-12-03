package project4;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class calculates the total size of a file or directory, and a list of
 * the largest files in a directory.
 * It also provides a String representation of a file/directory, its size and path.
 * 
 * @author Anna Prenowitz
 * @version 11-10-2023 
 */

public class FileOnDisk extends File {
	
	// Data fields
	// Store pathname, size of all files, list of all files, and total size of all files
	
	private String canonicalPathName;
	private LinkedList<FileOnDisk> llFile = new LinkedList<FileOnDisk>();
	static LinkedList<String> llAllPaths = new LinkedList<String>();
	static long totalSize;
	
	
	/**
     * This constructor calls the superclass' default constructor, checks for
     * null pathname arguments, and sets the canonical path to a variable.
     * @param pathname String used to call the super constructor as FileOnDisk
     * extends File class, which requires a pathname to create a File object.
     */
	public FileOnDisk(String pathname) throws NullPointerException {
		super(pathname);
		if (pathname==null) {
			throw new NullPointerException("Pathname cannot be null.");
		}
		try {
			this.canonicalPathName = this.getCanonicalPath();
		} catch (IOException ex) {
			System.err.println("Cannot construct canonical path.");
		}
	}
	
	
	/**
     * This method overrides the toString() method in the Object class.
     * It returns a String representation of a FileOnDisk object, with its
     * size, unit of size, and pathname.
     * @return String representation of FileOnDisk object.
     */
	public String toString() {
		
		// call recursive function to get the total size of FileOnDisk object.
		
		this.exploreDir(this.canonicalPathName);
		int count = 0;
		double newTotalSize = totalSize;
		
		// If method called on a file not directory, assign the file size to be
		// the total size
		
		if (this.isFile()) {
			newTotalSize = this.length();
		}
		
		// Factor down the number of bytes, and calculate units for file size.
		// Format these numbers as strings.
		
		while (newTotalSize > 1024) {
			newTotalSize /= 1024;
			count += 1;
		}
		String strTotalSize = String.format("%.2f", newTotalSize);
		String strTotalSize2 = String.format("%8s", strTotalSize);
		// Assume total size is never over 1 TB.
		String unit = "bytes";
		if (count == 1)
			unit = "KB";
		else if (count == 2) {
			unit = "MB";
		}
		else if (count == 3) {
			unit = "GB";
		}
		String unitStr = String.format("%-7s", unit);
		
		// Arrange various Strings together into desired output: size, unit, path
		// and return the String
		
		String returnString = String.format(strTotalSize2 + " " + 
		unitStr + canonicalPathName);
		return returnString;
	}
	
	
	
	/**
     * This method returns the total size of a file or directory.
     * @return long total size.
     */
	long getTotalSize() {
		
		// If method is called on a directory, return calculated size including all
		// files inside it. If it's a file, just return file size.
		
		if (this.isDirectory()) {
			return(totalSize);
		} else {
			return this.length();
		}
	}
	

	
	/**
     * This method gets the largest files in the given directory.
     * @param numOfFiles determines how many files are printed.
     * The default value is 20, but it can be given as a command line argument.
     * @return list of the numOfFiles largest FileOnDisk objects.
     */
	List<FileOnDisk> getLargestFiles(int numOfFiles) {
		
		// Ensure numOfFiles is >= 0, as you can't have negative files.
		
		try {
			if (numOfFiles < 0) {
				throw new IllegalArgumentException();
			}
		} catch(IllegalArgumentException ex) {
			System.err.println(numOfFiles + " is an invalid number of files. "
					+ "Number cannot be negative.");
			System.exit(1);
		}
		
		// If FileOnDisk object is a file itself, return null.
		
		if (this.isFile()) {
			return null;
		}
		
		// If FileOnDisk object is a directory, run recursive function , exploreDir.
		// This should calculate the total size of the directory if it hasn't
		// already been done.
		
		else {
			try {
				this.exploreDir(this.getCanonicalPath());
			} catch (IOException ex) {
				System.err.println("Canonical path cannot be constructed.");
			}
			
			// Sort the list of FileOnDisk objects found in exploreDir
			// using the FileOnDiskComparatorBySize, which implements Comparator<>.
			
			try {
				Collections.sort(llFile, new FileOnDiskComparatorBySize());
			} catch (Exception ex) {
				System.err.println("Can only run getLargestFiles() on File object.");
			}
			
			// Add the largest numOfFiles files to a new list and return this list.
			
			LinkedList<FileOnDisk> llFileTop = new LinkedList<FileOnDisk>();
			int count = 0;
			for (FileOnDisk thisFile : llFile) {
				if (count < numOfFiles) {
					llFileTop.add(thisFile);
				}
				count += 1;
			}
			return llFileTop;
		}
		
		
	}
	
	
	
	
	/**
     * This method recursive recursively searches through a directory,
     * adding up the total sizes of files and making a list of files
     * contained in the directory. If the method is called on a file (base case)
     *  it just returns the size of the file.
     * @param potentialDirName, the representing the pathname to be
     * explored recursively.
     */
	
	void exploreDir(String potentialDirName) {
		
		// Set count and change if file or directory has already been explored.
		int count = 0;
		if (llAllPaths.contains(potentialDirName)) {
			count = 1;
		}
		
		// If file or directory hasn't been explored, make a new FileOnDisk object.
		// Add the file or directory to a LinkedList storing the file paths.
		
		if (count == 0) {
			FileOnDisk newFile = new FileOnDisk(potentialDirName);
			llAllPaths.add(canonicalPathName);
			
			// If it's a directory, recursively explore the directory.
			
			if (newFile.isDirectory()) {
				for (File direc : newFile.listFiles()) {
					try {
						exploreDir(direc.getCanonicalPath());
					}
					catch (IOException ex) {
						System.err.println("Canonical path cannot be constructed.");
					}
				}
			}
			
			// If it's a file (base case), add the size of the file to totalSize.
			
			else {
			
				this.llFile.add(newFile);
				totalSize += newFile.length();
			}
		}
	}
}
