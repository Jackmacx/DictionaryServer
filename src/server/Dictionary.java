/*********************************************************************
* Author:     Jack Macumber (817548)
* Date:       April 2020

Implements code for Assignment 1 of Distributed Systems
*********************************************************************/

package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Dictionary {
	
	private HashMap<String, String> dict = new HashMap<String, String>();
	
	public Dictionary() {}
	
	public Dictionary(String filename) {
		load(filename);
	}

	// Load Dictionary from File
	private void load(String filename) {
		try {
			Scanner sc = new Scanner(new File(filename));
			while (sc.hasNextLine()) {
				String term = sc.nextLine();
				String info = sc.nextLine();
				dict.put(term.toUpperCase(), info);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary Does Not Exist");
		} catch (Exception e) {
			System.out.println("Dictionary Loading Failed");
		}
		
	}
	
	// Returns Info, Null on Does not Exist
	public synchronized String search(String term) {
		return dict.get(term.toUpperCase());
	}
	
	// Returns Success
	public synchronized boolean add(String term, String info) {
		if (dict.containsKey(term.toUpperCase())) {
			return false;
		}
		dict.put(term.toUpperCase(), info);
		return true;
	}
	
	// Returns Delete
	public synchronized boolean delete(String term) {
		if (!dict.containsKey(term.toUpperCase())) {
			return false;
		}
		dict.remove(term.toUpperCase());
		return true;
	}

	// Returns a List of Close Matched Words
	public ArrayList<String> near(String term) {
		HashSet<String> near = new HashSet<String>();
		String test;
		int len = term.length();
		
		for (int i=0; i<len; i++) {
			// Deletions
			test = term.substring(0, i) + term.substring(i+1, len);
			if (dict.containsKey(test.toUpperCase())) {
				near.add(test);
			}
			// Substitutions
			for (char c = 'a'; c <= 'z'; c++) {
				test = term.substring(0, i) + c + term.substring(i+1, len);
				if (dict.containsKey(test.toUpperCase())) {
					near.add(test);
				}
			}
		}
		// Insertions
		for (int i=0; i<=len; i++) {
			for (char c = 'a'; c <= 'z'; c++) {
				test = term.substring(0, i) + c + term.substring(i, len);
				if (dict.containsKey(test.toUpperCase())) {
					near.add(test.toUpperCase());
				}
			}
		}
		
		return new ArrayList<String>(near);
	}
	
}
