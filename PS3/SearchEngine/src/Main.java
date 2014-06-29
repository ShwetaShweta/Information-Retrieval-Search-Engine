
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		double collectionFrequency=2959128;
		CustomSearchEngine objSearch=new CustomSearchEngine(collectionFrequency);
		TdIdf obj=new TdIdf(collectionFrequency);
	   objSearch.StartSearchEngine();
		
	}

}
