
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ComputingIndegree {
	private String textFile;
	private Integer length;
	Logger objLog=new Logger();


	public ComputingIndegree(String file,Integer len){
		textFile=file;
		length=len;
	}

	public String ProcessText(String input){
		String inp=input.replaceAll("category:", "");
		inp=inp.replaceAll("," ,"");
		return inp;
	}
	public ArrayList<String> FindRedirects(String text){
		String substring=null;
		ArrayList<String>outlinks=new ArrayList<String>(length);
		Pattern pattern=Pattern.compile("[\\[\\[][\\w\\s()\\-\\|\\/\"\'!\\.,\\+:]+[\\]\\]]");
		Matcher matcher=pattern.matcher(text);
		while(matcher.find()){
			substring=matcher.group();
			substring=substring.replaceAll("[\\[\\]]", "");
			if(substring.indexOf(".org")==-1 && substring.indexOf("http://")==-1 && substring.indexOf("file:")==-1
					&& substring.indexOf("image:")==-1){
				if(substring.indexOf('|')!=-1){
					String[] pipeSeparated=(substring.split("\\|"));
					for(int l=0;l<pipeSeparated.length;l++){	
						outlinks.add(pipeSeparated[0]);
					}
				}
				else{

					outlinks.add(substring);
				}
			}
		}

		return outlinks;
	}
	public void findOutlinks() throws IOException{
		try{
		BufferedReader reader=new BufferedReader(new FileReader(new File(textFile)));
		String readline=null;
		ArrayList<String> subStrings=new ArrayList<String>(length);
		File file=new File("D:\\PS3\\SearchEngine\\AdjacenyList.txt");
		File file1=new File("D:\\PS3\\SearchEngine\\Index.txt");
		PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(file)));
		PrintWriter writer1=new PrintWriter(new BufferedWriter(new FileWriter(file1)));
		while((readline=reader.readLine())!=null){
			String page="";
			String title="";
			String id="";

			Pattern pattern=Pattern.compile("<title>.+</title>");
			Pattern pattern1=Pattern.compile("<id>.+</id>");
			while(readline != null && !readline.equals("</page>")){
				page=page+readline;
				readline=reader.readLine();

			}

			page=page.toLowerCase();	
			page=ProcessText(page);
			Matcher matcher=pattern.matcher(page);
			Matcher matcher1=pattern1.matcher(page);
			while(matcher.find()){
				title=matcher.group(0);
				title=title.replace("<title>","");
				title=title.replace("</title>","");
			}
			while(matcher1.find()){
				id=matcher1.group(0);
				id=id.replace("<id>","");
				id=id.replace("</id>","");
			}

			subStrings=FindRedirects(page);
			if(title!=null && !(subStrings.isEmpty())){
				writer.println(title+"="+subStrings+"#");
				writer1.println(title+"#"+id);}
		}

		reader.close();
		writer.close();
		writer1.close();

		}

		catch(Exception ex){
			objLog.logMessages(ex);
		}
	}
	public HashMap<String,Object> titleIndexMap() throws IOException{
		
		BufferedReader reader=new BufferedReader(new FileReader(new File("D:\\PS3\\SearchEngine\\Index.txt")));
		HashMap<String,Object> titleIndex=new HashMap<String,Object>(length);
		String readLine="";
		while((readLine=reader.readLine())!=null){
			String[] 	arr=readLine.split("#");
			titleIndex.put(arr[0], arr[1]);
		}
		reader.close();
		return titleIndex;
	}
	public void findIndegree() throws IOException{
		try{
		HashMap<String,Object> titleIndex=titleIndexMap();
		BufferedReader reader=new BufferedReader(new FileReader(new File("D:\\PS3\\SearchEngine\\AdjacenyList.txt")));
		File file1=new File("D:\\PS3\\SearchEngine\\Indegree.txt");
		PrintWriter writer1=new PrintWriter(new BufferedWriter(new FileWriter(file1)));
		HashMap<Object,Integer> Indegree=new HashMap<Object,Integer>(length);
		
		for(String element:titleIndex.keySet()){
			Object Identi=titleIndex.get(element);
			Indegree.put(Identi, 0);
		}
		String readLine="";
		String outlinks="";
		Object id="";
		while((readLine=reader.readLine())!=null){
			String[] spl=readLine.split("=");
			if(!spl[1].isEmpty()){
			outlinks=spl[1];}
			outlinks=outlinks.replaceAll("[\\[\\]]", "");
			String[] arr=outlinks.split(",");
			for(int i=0;i<arr.length;i++){
				arr[i]=arr[i].trim();
				if(titleIndex.containsKey(arr[i])){
					id=titleIndex.get(arr[i]);
					if(Indegree.containsKey(id)){
						int value=Indegree.get(id);
		             	value++;
						Indegree.put(id, value);
						
					}

					else{
						Indegree.put(id, 1);
					}
				}

			}
		}
		reader.close();
		for(Object element:Indegree.keySet()){
			writer1.println(element+" "+Indegree.get(element));
		}
		writer1.close();
		}
		catch(Exception ex){
			objLog.logMessages(ex);
		}
	}

}
