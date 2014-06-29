import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


public class Logger {
	public void logMessages(Object message) throws IOException{
		PrintWriter write=new PrintWriter(new BufferedWriter(new FileWriter("D:\\PS3\\SearchEngine\\LogFile.txt",true)));
		Date date=new Date();
		
		write.println(date+"-->"+message);
		write.println();
		write.close();
	}
}
