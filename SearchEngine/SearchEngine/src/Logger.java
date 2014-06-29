import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.io.PrintWriter;
	import java.util.Date;


	public class Logger {
		public void log(String indicates,String message) throws IOException{
			String type=indicates;
		
			String msg=message;
			Date date=new Date();
			PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(FileNames.LogFileName,true)));
			for (int i = type.length(); i <= 20; i++){
	            type += " ";
	    }	
			writer.println(date+":" +type+":"+ msg);
			writer.close();
			
			
		}
		public static void LogMessage( String message) 
		{
			try
			{
			String msg=message;
			Date date=new Date();
			PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(FileNames.LogFileName,true)));
			
			writer.println(date+":"+ msg);
			writer.close();
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
		public void deleteLog(File filename){
			File logFile=filename;
			if(logFile.exists()){
	             logFile.delete();
			}
		}
	}


