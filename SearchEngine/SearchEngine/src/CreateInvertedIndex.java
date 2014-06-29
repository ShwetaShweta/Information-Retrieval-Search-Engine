import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;


public class CreateInvertedIndex {
	String xmlFile="./samp1.xml";
	String invertedIndex="./InvertedIndex.txt";
	static ArrayList<String> stopWords;
	public void SearchAndAppendFile(ArrayList<String> terms, long docId)
	{
		try
		{
			StringBuffer fileContents = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(invertedIndex));
			String currentLine = reader.readLine();
			String currentWordFromFile="";
			boolean refreshDocIdArray = true;
			ArrayList<Long> docIds = new ArrayList<Long>();
			int termInd = 0;
			boolean skipNewLine = true;
			while(currentLine!=null && termInd < terms.size())
			{
				if(skipNewLine == false)
				{
				fileContents.append("\n");
				}
				else
				{
				   skipNewLine = false;
				}
				if(refreshDocIdArray)
				{
					currentWordFromFile = "";
					StringTokenizer tokenizer = new StringTokenizer(currentLine, "#,", false);
					if(tokenizer.hasMoreTokens())
					{
						currentWordFromFile = tokenizer.nextToken();
					}
					docIds = new ArrayList<Long>();
					while(tokenizer.hasMoreTokens())
					{
						docIds.add(Long.parseLong(tokenizer.nextToken()));
					}
					refreshDocIdArray = false;
				}
				String currentTerm = terms.get(termInd);
				int compareResult = currentWordFromFile.compareTo(currentTerm);
				if(compareResult == 0)
				{
					docIds.add(docId);
					int currInd = docIds.size()-1;
					int j = currInd-1;
					while( j>=0 && docIds.get(j)>docId)
					{
						
							Long prev = docIds.get(j);
							docIds.set(j+1, prev);
							j--;
						
						
					}
					docIds.set(j+1,  docId);
					
					fileContents.append(GetFormattedTermWithPostingList(currentTerm, docIds));
					currentLine = reader.readLine();
					refreshDocIdArray = true;
					termInd++;
				}
				if(compareResult<0)
				{
					fileContents.append(currentLine);
					currentLine = reader.readLine();
					refreshDocIdArray = true;
				}
				if(compareResult>0)
				{
					fileContents.append(currentTerm+"#"+docId);
					termInd++;
				}
			}
			while(termInd<terms.size())
			{
				fileContents.append("\n");
				fileContents.append(terms.get(termInd)+"#"+docId);
				termInd++;
			}
			while(currentLine != null)
			{
				fileContents.append("\n");
				fileContents.append(currentLine);
				currentLine = reader.readLine();
			}
			reader.close();
			
			FileWriter writer  = new FileWriter(new File(invertedIndex),false);
			writer.write(fileContents.toString());
			writer.close();
			
		}
		catch(Exception ex)
		{
			
		}
		return ;
	}
	public static void writefile(HashMap hash,String fileName) throws FileNotFoundException{
		HashMap inputHash=hash;
		String file=fileName;
		PrintWriter write1=new PrintWriter(file);
		for(Object ele:inputHash.keySet()){
			write1.println(ele+"->"+inputHash.get(ele));
		}
		write1.close();
		
	}
	private String GetFormattedTermWithPostingList(String term, ArrayList<Long> docIds)
	{
		StringBuffer result = new StringBuffer();
		result.append(term);
		result.append("#");
		
		for(int i = 0;i<docIds.size();i++)
		{
			if(i == 0)
			{
				result.append(docIds.get(i));
			}
			else
			{
				result.append(","+docIds.get(i));
			}
		}
		return result.toString();
	}
	SourceFileParser parserObject=new SourceFileParser(xmlFile);
	public static String regularExp(String input){
		String output=input;
		try{
			output=output.replaceAll("[^\\w]"," ");
			output=output.replaceAll("\\s+", " ");
			output=output.replaceAll("[^\\w\\n]"," ");
			output=output.replaceAll("_+", " ");
			output=output.toLowerCase();
		}
		catch(Exception ex){
			Logger.LogMessage(ex.getMessage());
		}
		return output;
	}
	public ArrayList<HashMap> CreateIndex() throws FileNotFoundException{
		HashMap<String,String> docIDMap=new HashMap<String,String>();
		ArrayList<HashMap> returnHashes=new ArrayList<HashMap>();
		HashMap<String,Integer> wordCount=new HashMap<String,Integer>();
		if(stopWords == null)
		{
			stopWords = new ArrayList<String>();
			stopWords.add("http");
			stopWords.add("is");
			stopWords.add("it");
			stopWords.add("the");
			stopWords.add("however");
			stopWords.add("in");
			stopWords.add("if");
			stopWords.add("i");
			stopWords.add("image");
			stopWords.add("instead");
			stopWords.add("into");
			stopWords.add("its");
			stopWords.add("www");
			stopWords.add("with");
			
		}
		try{
			//Variable Initializations
			String documentID="";
			String DocURL="";
			String inputText="";
			String word="";
			PrintWriter write=new PrintWriter(invertedIndex);
			Map <String,Object>postingList=new HashMap<String,Object>();
			ArrayList <String>fileData=new ArrayList<>();

			fileData=parserObject.ParseLargeXMLFileAndGetNextNode();
			int count = 0;
			
			while(fileData.size()!=0 && count <2000){
				ArrayList<String> wordArray=new ArrayList<String>();
				if(fileData.get(0)!= null) {documentID=fileData.get(0);}      
				if(fileData.get(1)!= null) {DocURL=fileData.get(1);}
				if(fileData.get(2)!= null) {inputText=fileData.get(2);}
				String Text=regularExp(inputText);
				docIDMap.put(documentID, DocURL);
				StringTokenizer stringtokenizer = new StringTokenizer(Text, " ");

				while (stringtokenizer.hasMoreElements()) {
					word=stringtokenizer.nextToken();
					if(!stopWords.contains(word)&&!wordArray.contains(word))
					{
						
						wordArray.add(word);
					}
					if(wordCount.containsKey(word)){

						wordCount.put(word, wordCount.get(word)+1);
					}
					else{
						wordCount.put(word,1);
					}
				}				
				
				Collections.sort(wordArray,String.CASE_INSENSITIVE_ORDER);
			SearchAndAppendFile(wordArray, Long.parseLong(documentID));
                count++;


				fileData=parserObject.ParseLargeXMLFileAndGetNextNode();
				write.close();

			}

		}


		catch(Exception ex){
			Logger.LogMessage(ex.getMessage());
		}
		returnHashes.add(docIDMap);
		returnHashes.add(wordCount);
		return returnHashes;
	}


	public static void main(String[] args) throws FileNotFoundException
	{
		
			CreateInvertedIndex indexCreator = new CreateInvertedIndex();
			ArrayList<HashMap> output=new ArrayList<HashMap>();
			Logger.LogMessage("Index creation started");
			output = indexCreator.CreateIndex();
			Logger.LogMessage("Index creation ended");
			writefile(output.get(0), FileNames.docIndexFileName);
			writefile(output.get(1), FileNames.wordFrequencyFileName);
			System.out.println("Processing ended");
	
			/*
			RandomAccessFile file = new RandomAccessFile(FileNames.xmlFileName, "r");
			file.seek(0);
			StringBuffer s = new StringBuffer();
			for(long i = 0;i<1500000;i++)
			{
				file.readLine();
			}
			for(long i = 0;i<1000000;i++)
			{
				file.readLine();
			}
			for(long i = 0;i<500000;i++)
			{
				s.append("\n");
				s.append(file.readLine());
			}
			FileWriter temp = new FileWriter("/home/sri/sjsu docs/Search engine/temp.txt");
			temp.write(s.toString());
			file.close();
			temp.close();
			
		}
		catch(Exception ex)
		{
			Logger.LogMessage(ex.getMessage());
		}
*/
}
	}


