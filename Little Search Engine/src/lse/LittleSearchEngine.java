package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		HashMap<String, Occurrence> map=new HashMap<String, Occurrence>(100,2f);
		
		Scanner sc= new Scanner(new File(docFile));
		
		while(sc.hasNext()){
			
			String word=sc.next();
			
			word=this.getKeyword(word);
			
			
			if(!(noiseWords.contains(word))){
				
				if(!(map.containsKey(word))){
					try {
					Occurrence occ=new Occurrence(docFile,1);

					map.put(word, occ);
					}catch(Exception e) {
						System.out.println("Error in charTable");
					}
				}
				else if (map.containsKey(word)){
					try {
						
					Occurrence temp=map.get(word);
					
					int f = temp.frequency;
					f++;
					temp.frequency = f;
					map.put(word, temp);
					}catch (Exception e ){
						System.out.println("\\There is an error in the hashTable//");
					}
					
				}
			}
		}
		sc.close();
		return map;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		
		Set<String> set=kws.keySet();
		
		Iterator<String> it=set.iterator();
		
		while(it.hasNext()){
			String key=it.next();
			
			Occurrence occur=kws.get(key);
			
			ArrayList<Occurrence> l1=keywordsIndex.get(key);
			
			if(l1==null){
				try {
				
					l1=new ArrayList<Occurrence>();
				
					keywordsIndex.put(key,l1);
				
				}catch(Exception a) {
					throw new NullPointerException();
				}
			}
			
			l1.add(occur);
			this.insertLastOccurrence(l1);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
	
		word=this.takeOutP(word);
		
		if (word==null)
			return null;
		
		if((noiseWords.contains(word)==false)&&(word.substring(0,1)).matches("[a-zA-Z]+")){
			try {
			word=word.toLowerCase();
			}catch(Exception e) {
				System.out.println("Error");
			}
			return word;
		}
		return null;
		
		
	}
	private String takeOutP(String word){
		
		if (word.substring(0,1).matches("[a-zA-Z]+")==false) {
			return null;
		}
		char last = word.charAt(word.length()-1);
		String puncts = "!?,.:;";
		while(puncts.contains(word.substring(word.length()-1))) {
		switch(last) {
		
			case '!': word=word.substring(0,word.length()-1);
			
			case ',': word=word.substring(0,word.length()-1);
			
			case '.': word=word.substring(0,word.length()-1);
			
			case '?': word=word.substring(0,word.length()-1);
			
			case ':' :word=word.substring(0,word.length()-1);
			
			case ';': word=word.substring(0,word.length()-1);
			
		}
			last =word.charAt(word.length()-1);
		}
		
		
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		
		int ocSize = occs.size();
		
		ArrayList<Integer> mp = new ArrayList<Integer>();
		if (ocSize<2)
			return null;

		int low = 0;
		int high = ocSize;
		int target = occs.get(ocSize-1).frequency;
		
		int mid=0;
		

		while (high >= low)
		{
			mid = ((low+high)/2);
			
			mp.add(mid);

			if (occs.get(mid).frequency == target)
				break;

			else if (occs.get(mid).frequency < target) {
				high = mid - 1;
			}

			else if (occs.get(mid).frequency > target){
				low = mid + 1;
				if (high <= mid)
					mid = mid + 1;
			}
		}
		
		mp.add(mid);

		Occurrence temp = occs.remove(occs.size()-1);
		
		occs.add(mp.get(mp.size()-1), temp);

		return mp;
		
		
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			try {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			
			mergeKeywords(kws);
			}catch(Exception e) {
				System.out.println("This causes an error for some reason lol");
			}
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		
		ArrayList<Occurrence> com1 = new ArrayList<Occurrence>();
		ArrayList<String> r1 = new ArrayList<String>();
		
		ArrayList<Occurrence> occ1 = new ArrayList<Occurrence>();
		
		ArrayList<Occurrence> occ2 = new ArrayList<Occurrence>();
		
		
		if (keywordsIndex.containsKey(kw2))
			occ2 = keywordsIndex.get(kw2);
		if (keywordsIndex.containsKey(kw1))
			occ1 = keywordsIndex.get(kw1);
		
	
		com1.addAll(occ1);
		com1.addAll(occ2);
		
		if (!((occ1.isEmpty()) && (occ2.isEmpty())))
		{
			int x=0;
			
			while(x<com1.size()-1) {
				int y=1;
				while(y<com1.size()-x) {
					int cy1=com1.get(y-1).frequency;
					int cy = com1.get(y).frequency;
					if (cy1 < cy) {
						Occurrence temp = com1.get(y-1);
						
						com1.set(y-1, com1.get(y));
						com1.set(y,  temp);
					}
					
					y++;
				}
				x++;
			}

			int a = 0;
			
			while (a<com1.size()-1)
			{
				int b = a+1;
				while(b<com1.size())
				{
					if (com1.get(a).document == com1.get(b).document)
						com1.remove(b);
				}
			}
		}

		if (com1.size()>5) {
			try {
				
				while (com1.size() > 5) {
					com1.remove(com1.size()-1);
		}
			}catch(Exception e) {
				throw new NullPointerException();
			}
		}
			
		for (int i=0; i<com1.size();i++) {
			String s = com1.get(i).document;
			r1.add(s);
		}
		

		return r1;
	}
}