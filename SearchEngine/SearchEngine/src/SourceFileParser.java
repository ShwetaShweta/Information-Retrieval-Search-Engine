import java.io.*;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.stream.events.*;
import javax.xml.stream.*;

public class SourceFileParser {
	private XMLEventReader xmlReader;
	private String sourceXML;
	
	public SourceFileParser(String xmlFile)
	{
		try
		{
			sourceXML = xmlFile;
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	    	inputFactory.setProperty(XMLInputFactory.IS_COALESCING, false);
	    	    	
	    	xmlReader = inputFactory.createXMLEventReader(sourceXML, new FileInputStream(new File(sourceXML)));
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}	
	
    public ArrayList<String> ParseLargeXMLFileAndGetNextNode()
    {
    	ArrayList<String> nextPageValues = new ArrayList<String>();
        try
        {        	
        	String title = "";
        	String text = "";
        	String docId = "";
        	boolean foundNextPage = false;
        	if(xmlReader.hasNext())
        	{
        		XMLEvent evt = xmlReader.nextEvent();
        		while(!(evt.isStartElement() && evt.asStartElement().getName().getLocalPart() == "page") && xmlReader.hasNext())
        		{
        			evt = xmlReader.nextEvent();
        		}    		
    		
    			while(xmlReader.hasNext())
    			{
    				foundNextPage = true;
    				XMLEvent child = xmlReader.nextEvent();
    				if(child.isEndElement()&& child.asEndElement().getName().getLocalPart() == "page")
    				{
    					break;
    				}
    				if(child.isStartElement() && child.asStartElement().getName().getLocalPart() == "title")
    				{
    					XMLEvent titleData = xmlReader.nextEvent();
    					if(titleData.isCharacters())
    					{
    					title = titleData.asCharacters().getData();
    					}
    				}
    				if(child.isStartElement() && child.asStartElement().getName().getLocalPart() == "revision")
    				{
    					while(xmlReader.hasNext())
    					{
        				    XMLEvent revisionData = xmlReader.nextEvent();
        				    if((revisionData.isEndElement() && revisionData.asEndElement().getName().getLocalPart() == "revision"))
        				    {
        				    	break;
        				    }
    				        if(docId.isEmpty() && revisionData.isStartElement() && revisionData.asStartElement().getName().getLocalPart() == "id")
    				        {        				        	
    				        	docId = xmlReader.nextEvent().asCharacters().getData();
    				        }
    				        if(revisionData.isStartElement() && revisionData.asStartElement().getName().getLocalPart() == "text")
    				        {
    				        	while(xmlReader.hasNext())
    				        	{
    				        		XMLEvent textData = xmlReader.nextEvent();
    				        		if(textData.isEndElement() &&  textData.asEndElement().getName().getLocalPart() == "text")
    				        		{
    				        			break;
    				        		}
    				        		text+=textData.asCharacters().getData();
    				        	}
    				        }
        				    
    					}
    				}
    			}        			
    		}
        	else
        	{
        		xmlReader.close();
        	}
        	if(foundNextPage)
        	{
	        	text = ProcessPageContent(text);
	        	title = GetTrimmedTitle(title);
	        	
	            nextPageValues.add(docId);
	            nextPageValues.add(title);
	            nextPageValues.add(text);
        	}
        }
        catch(Exception ex)
        {
        	System.out.println(ex.getMessage());
        }
        return nextPageValues;
    }
    private String GetTrimmedTitle(String title)
    {
    	StringTokenizer tokenizer = new StringTokenizer(title,"\\/", false);
    	while(tokenizer.hasMoreTokens())
    	{
    		title = tokenizer.nextToken();
    	}
    	title = title.trim();
    	
    	return title;
    }
	private String ProcessPageContent(String text)
	{
		text = text.replaceAll("[=][=][=].*?[=][=][=]", ""); 
        text = text.replaceAll("[=][=].*?[=][=]", ""); 
        text = text.replaceAll("[=\\-+#()|\"'.,;:\\*\\?!_]", " "); 
		text = Process(text);
		
		text = text.replaceAll("(?s)[&][l][t][;][r][e][f].*?[&][g][t][;].*?[&][l][t][;][/][r][e][f].*?[&][g][t][;]", ""); 
        text = text.replaceAll("(?s)[<][r][e][f].*?[>].*?[<][/][r][e][f].*?[>]", ""); 
        text = text.replaceAll("(?s)[&][l][t][;][r][e][f].*?[/][&][g][t][;]", ""); 
        text = text.replaceAll("(?s)[<][r][e][f].*?[>].*?[<][/][>]", ""); 
		
        text = text.replaceAll("\\/", ""); 
        text = text.replaceAll("([\\[][\\[])|([\\]][\\]])|([{][{])|([}][}])", " "); 
        text = text.replaceAll("[\\[].*?[\\]]", ""); 
        text = text.replaceAll("&nbsp", ""); 
        text = text.replaceAll("[<>!]",""); 
        text = text.replaceAll("&lt", " ");
        text = text.replaceAll("&gt", " ");
         			
		return text;
	}
	private String Process(String text)
	{
		StringBuffer buf = new StringBuffer();
		int i = 0;
		boolean skip = false;
		int length = text.length();
		int nesting = 0;
		while(i<length)
		{
			if(text.charAt(i) == '{' && i+1 < length && text.charAt(i+1) == '{' )
			{
				nesting++;
			}
			if(text.charAt(i) == '}' && i+1 < length && text.charAt(i+1) == '}')
			{
				nesting--;
			}
			if(!skip)
			{
				if(text.charAt(i) == '{' && i+1 < length && text.charAt(i+1) == '{' )
				{
					skip = true;
					i +=2;
				}
				else
				{
					buf.append(text.charAt(i));
					i++;
				}
				
			}
			else
			{
			    if(text.charAt(i) == '}' && i+1 < length && text.charAt(i+1) == '}')
			    {
			    	if(nesting == 0)
			    	{
				    	skip = false;
			    	}
				    i = i+2;
			    	
			    }
			    else
			    {
			    	i++;
			    }
			}
			
			
		}
		return buf.toString();
	}
	public void ReloadXML()
	{
		try
		{
			if(xmlReader != null)
			{
				xmlReader.close();
			}
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	    	inputFactory.setProperty(XMLInputFactory.IS_COALESCING, false);
	    	    	
	    	xmlReader = inputFactory.createXMLEventReader(sourceXML, new FileInputStream(new File(sourceXML)));
	    	
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	/*public static void main(String[] args)
	{		
		SourceFileParser fileParser = new SourceFileParser("F:\\sjsu docs\\Search Engine\\samp2.xml");
		ArrayList<String> output = fileParser.ParseLargeXMLFileAndGetNextNode();
		fileParser.ReloadXML();
		output = fileParser.ParseLargeXMLFileAndGetNextNode();
		System.out.println(output.get(0));
		System.out.println(output.get(1));
		System.out.println(" end");
	}*/
}
