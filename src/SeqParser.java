import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SeqParser {

	private ArrayList<String> species = new ArrayList<String>();
    private ArrayList<String> seq  = new ArrayList<String>();
    
    public SeqParser(String filename)
    {
      
       try{
         
    	 BufferedReader input  = new BufferedReader(new FileReader(filename));
         StringBuffer   buffer = new StringBuffer();
         String         line   = input.readLine();
       
         if(line.charAt(0)!='>')
           throw new IOException( "The format is Wrong" );
         else
        	 species.add(line);
         for( line = input.readLine().trim(); line != null; line = input.readLine() )
         {
           if( line.length()>0 && line.charAt(0)=='>')
           {
             seq.add(buffer.toString());
             buffer = new StringBuffer();
             species.add(line);
           } else  
             buffer.append(line.trim());
         }   
         if(buffer.length()!=0)
           seq.add(buffer.toString());
       }catch(IOException e)
       {
         System.out.println("Error when reading "+filename);
         
       }

    }
    //return title as String
    public ArrayList<String> getSpecies() {
      return species;
    }

    //return sequence as a String
    public ArrayList<String> getSequence() { 
      return seq;
    }

    
    
}
	
