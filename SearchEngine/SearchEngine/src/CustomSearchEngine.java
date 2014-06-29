import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;


public class CustomSearchEngine {

	private Scanner scanner;
	
	private static String LogMessages(String message)
	{
		try 
		{
			Date dateTime = new Date();
			FileWriter writer = new FileWriter(FileNames.LogFileName, true);
			writer.write("\n");
			writer.write(dateTime.toString()+": "+message);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Date().toString();
	}
	public CustomSearchEngine()
	{
		try
		{
		scanner = new Scanner(System.in);
		}
		catch(Exception ex)
		{
			
		}
	}
	public void StartSearchEngine()
	{
	  String userQuery;
	  String doAnotherSearch;
      
	  do
	  {
	      System.out.println("Please enter the search query");
	      
	      userQuery = scanner.nextLine();
	      LogMessages("Starting search");
	      ArrayList<String> searchResults = GetQueryResults(userQuery);
	      LogMessages("Search completed");
	      DisplaySearchResults(searchResults);
	      
	      System.out.println("Do you want to give a new query? (yes/no)");
	      doAnotherSearch = scanner.nextLine();
	  }while(doAnotherSearch.equalsIgnoreCase("yes"));
	}
	private ArrayList<String> GetQueryResults(String userQuery)
	{
		ArrayList<String> searchResults = new ArrayList<String>();
		ArrayList<String> terms = GetTermsFromQuery(userQuery);
		for(int i = 0; i<terms.size(); i++)
		{			
			if(searchResults.size() == 0)
			{
			    searchResults = GetPostingListForTerm(terms.get(i));
			}
			else
			{
				List<String> currentPostingList = GetPostingListForTerm(terms.get(i));
				searchResults = GetIntersectedResults(currentPostingList, searchResults);
			}
		}
	    return searchResults;	
	}
	private ArrayList<String> GetTermsFromQuery(String userQuery)
	{
		userQuery = userQuery.replaceAll("[=\\-+#()|\"'.,;:\\*\\?!<>/_]", " ");
		
		
		ArrayList<String> terms = new ArrayList<String>();
		StringTokenizer stringTokenizer = new StringTokenizer(userQuery, " ", false);
        while(stringTokenizer.hasMoreTokens())
        {
        	terms.add(stringTokenizer.nextToken().toLowerCase());
        }
		
		return terms;
	}
	private ArrayList<String> GetPostingListForTerm(String term)
	{
		ArrayList<String> postingList = new ArrayList<String>();
		ArrayList<Long> postingListLongVal = new ArrayList<Long>();
		Long[] sortedArr = new Long[0];
		try
		{
			File indexFileDir = new File(FileNames.indexFilesLocation);
			File[] indexFiles = indexFileDir.listFiles();
			for(int i = 0; i<indexFiles.length; i++)
			{
				ArrayList<String> currentList = SearchInFile(term, indexFiles[i].getAbsolutePath());
				for(int j = 0; j < currentList.size(); j++)
				{
					postingListLongVal.add(Long.parseLong(currentList.get(j)));
				}
			}
			sortedArr = postingListLongVal.toArray(new Long[0]);
			Arrays.sort(sortedArr);
			for(int i = 0;i<sortedArr.length;i++)
			{
				postingList.add(sortedArr[i].toString());
			}
		}
		catch(Exception ex)
		{
			this.LogMessages(ex.getMessage());
		}
		return postingList;
	}
	
	private void DisplaySearchResults(ArrayList<String> searchResults)
	{
		String doYouWantToContinue;
		List<String> documentNames = GetDocumentNamesFromIds(searchResults);
		for(int i = 0; i < documentNames.size(); i++){
			String result = documentNames.get(i);
			System.out.println(result);
			if((i+1)%10 == 0)
			{
				System.out.println("");
				System.out.println("Do you want to see the next 10 results? (yes/no)");
				doYouWantToContinue = scanner.nextLine();
				if(doYouWantToContinue.compareToIgnoreCase("yes") != 0)
				{
					return;
				}
			}
		}
	}
	private ArrayList<String> GetIntersectedResults(List<String> currentPostingList, List<String> existingPostingList)
	{
		ArrayList<String> intersectedList = new ArrayList<String>();
		int i = 0;
		int j = 0;
		long curr;
		long existing;
		while(i<currentPostingList.size() && j < existingPostingList.size())
		{
			curr = Long.parseLong(currentPostingList.get(i));
			existing = Long.parseLong(existingPostingList.get(j));
			
			if(curr == existing)
			{
				intersectedList.add(curr+"");
				i++;
				j++;
			}
			else if(curr<existing)
			{
				i++;
			}
			else if(curr>existing)
			{
				j++;
			}			
		}
		return intersectedList;
	}
	public ArrayList<String> SearchInFile(String term, String fileName)
	{
		ArrayList<String> postingList = new ArrayList<String>();
		
		LogMessages("Loading posting list for '"+term+"'");
		try
		{
			RandomAccessFile indexFile = new RandomAccessFile(fileName,"r");
			indexFile.seek(0);
			String line = indexFile.readLine();
			StringTokenizer tokenizer = new StringTokenizer(line, "#,", false);
			String currentTerm="";
			if(tokenizer.hasMoreTokens())
			{
				currentTerm = tokenizer.nextToken();
			}
			if(currentTerm!= "")
			{
				if(currentTerm.compareTo(term)==0)
				{
					while(tokenizer.hasMoreTokens())
					{
						postingList.add(tokenizer.nextToken());
					}
					LogMessages("Loading posting list for '"+term+"' complete");
					indexFile.close();
					return postingList;
				}
				if(currentTerm.compareTo(term)>0)
				{
					indexFile.close();
					return postingList;
				}
				long start = 0;
				long end = indexFile.length();
				while(start<=end)
				{
					currentTerm = "";
					long mid = (start+end)/2;
					indexFile.seek(mid);
					indexFile.readLine();
					line = indexFile.readLine();
					tokenizer = new StringTokenizer(line, "#,", false);
					if(tokenizer.hasMoreTokens())
					{
						currentTerm = tokenizer.nextToken();
					}
					if(currentTerm != "")
					{
						if(currentTerm.compareTo(term) == 0)
						{
							while(tokenizer.hasMoreTokens())
							{
								postingList.add(tokenizer.nextToken());
							}
							LogMessages("Loading posting list for '"+term+"' complete");
							indexFile.close();
							return postingList;
						}
						if(currentTerm.compareTo(term)>0)
						{
							end = mid-1;
						}
						if(currentTerm.compareTo(term)<0)
						{
							start = mid+1;
						}
					}
					else
					{
						indexFile.close();
						return new ArrayList<String>();
					}
				}
			}
			indexFile.close();
		}
		catch(Exception ex)
		{
			
		}
		
		return postingList;
	}
	
	public ArrayList<String> GetDocumentNamesFromIds(ArrayList<String> searchResults)
	{
			ArrayList<String>ParsedDocument=new ArrayList<String>();
			try{
				
				File fileName=new File(FileNames.docIndexFileName);
				Scanner scnr=new Scanner(fileName);
				HashMap<String, String> hash=new HashMap<String,String>();
				
					while(scnr.hasNext()){
						String line=scnr.nextLine();
						StringTokenizer stringtokenizer = new StringTokenizer(line, "->");
						String docID=stringtokenizer.nextToken();
						hash.put(docID, stringtokenizer.nextToken());
					}
					scnr.close();
					for(String ele:searchResults ){
						String value=hash.get(ele);
						ParsedDocument.add(value);
				}
			}
			catch(Exception ex){
				//CreateLog.LogMessage(ex.getMessage());
			}
			return ParsedDocument;


		}
	public static void main(String[] args) {
		CustomSearchEngine searchEngine  = new CustomSearchEngine();
		searchEngine.StartSearchEngine();
		//List<String> list = searchEngine.SearchInFile("0");
		

	}

}