import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;




public class tdifComputation {
	
	public void ComputeDocumentFrequency(){
		Scanner readPostingsFile=new Scanner(FileNames.invertedIndexFileName);
		while(readPostingsFile.hasNext()){
			int count=0;
			String line=readPostingsFile.nextLine();
			StringTokenizer tokenizer=new StringTokenizer(line);
			while(tokenizer.hasMoreElements() && (!tokenizer.nextToken().equalsIgnoreCase("#") || 
					!tokenizer.nextToken().equalsIgnoreCase(",")){
				HashMap documentFrequency=new HashMap();
				 count++;
				documentFrequency.put(arg0, arg1)
				
			}
		}
	}
	
public static void main(String[] args) {
	
}
}
