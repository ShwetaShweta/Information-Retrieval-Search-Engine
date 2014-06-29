
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class TdIdf {
	double totalDoc;
	Logger objLog=new Logger();
	ComputeScore objCompute=new ComputeScore();
	public TdIdf(double N){
		totalDoc=N;
	}
	public String SearchInFile(String term,String fileName,String delimiter) throws IOException
	{
		String postingList = "";
		try
		{
			RandomAccessFile indexFile = new RandomAccessFile(fileName,"r");
			indexFile.seek(0);
			String line = indexFile.readLine();
			String currentTerm="";
			int index = line.indexOf(delimiter);
			currentTerm = line.substring(0,index);
			if(currentTerm!= "")
			{

				if(currentTerm.compareTo(term)==0)
				{				
					objLog.logMessages("Loading posting list for '"+term+"' complete");

					indexFile.close();
					return line;
				}
				if(currentTerm.compareTo(term)>0)
				{
					indexFile.close();
					return "";
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
					int ind = line.indexOf(delimiter);
					currentTerm = line.substring(0,ind);
					if(currentTerm != "")
					{
						if(currentTerm.compareTo(term) == 0)
						{
							objLog.logMessages("Loading posting list for '"+term+"' complete");
							indexFile.close();
							return line;

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
						return "";
					}
				}
			}
			indexFile.close();
		}
		catch(Exception ex)
		{
			objLog.logMessages(ex);
		}

		return postingList;
	}
	public HashMap<String, Double> getQueryResults(String input) throws IOException{
		input=input.toLowerCase();
		String[] terms=input.split(" ");
		HashMap<String,String> termFile=new HashMap<String,String>(terms.length);
		HashMap<String,HashMap<String,Double>> TFIDF=new HashMap<String,HashMap<String,Double>>();
		for(int i=0;i<terms.length;i++){
			char val=terms[i].charAt(0);
			
			String filename="D:\\PS3\\letterWise\\"+val+".txt";
            termFile.put(terms[i], filename);
			}
		for(String key:termFile.keySet()){
			String List=SearchInFile(key,termFile.get(key),"#");
			if(List!=""){
				//System.out.println(List);
				TFIDF.put(key, objCompute.computeTFIDF(List, totalDoc));
			}	
		}
		HashMap<String,Double> hash0=TFIDF.get(terms[0]);
		HashMap<String,Double> Collectivehash=new HashMap<String,Double>();
		if(terms.length==1){
			return hash0;
		}
		else{
			for(int l=1;l<terms.length;l++){
				if(TFIDF.containsKey(terms[l])){
					Collectivehash.putAll(objCompute.computeWeight(hash0,TFIDF.get(terms[l])));

				}
			}
		}

		return Collectivehash;
	}


	
}

