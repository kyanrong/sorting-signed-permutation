import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
//start = A, end = B;
public class Mapper {
	//since every time the algo is run max is 
	private HashMap hm;
	private ArrayList<Integer> oriA_Unsigned = new ArrayList<Integer>();
	private ArrayList<Integer> oriB_Unsigned = new ArrayList<Integer>();
	private ArrayList<Boolean> oriA_signs = new ArrayList<Boolean>();
	private ArrayList<Boolean> oriB_signs = new ArrayList<Boolean>();
	
	private int[] newA_Unsigned;
	private boolean[] newA_signs;
	
	
	private ArrayList<Integer> newStartUnsigned = new ArrayList<Integer>();
	private ArrayList<Boolean> newStartSigns = new ArrayList<Boolean>();
	
	
	private Boolean[] signConvert;
	public Mapper(ArrayList<Integer> start, ArrayList<Integer> end, ArrayList<Boolean> signsStart, ArrayList<Boolean> signsEnd) {
		newA_Unsigned = new int[start.size()];
		newA_signs = new boolean[start.size()];
		//as basis of how to convert
		signConvert = new Boolean[start.size()];
		//does = work?
		
		oriA_Unsigned.addAll(start);
		oriA_signs.addAll(signsStart);
		
		
		oriB_Unsigned.addAll(end); 
		oriB_signs.addAll(signsEnd);
		

		
		hm = new HashMap(oriA_Unsigned.size());
		hashSeq();
		createNewSeq();
	}
	
	public void hashSeq() {
		//seq from 0 and has n+1
		for(int i=0;i<oriB_Unsigned.size();i++) {
			int key = oriB_Unsigned.get(i);
			hm.put(key,i);
			/*System.out.println("the key is: "+key +" the i is: "+i);*/
		}
		//change sign convert
		
		for(int i = 0;i < signConvert.length;i++) {
			if(oriB_signs.get(i)) {
				signConvert[i] = true;
			}else {
				signConvert[i] = false;
			}
		}
		/*for(int j = 0; j<signConvert.length;j++) {
			System.out.print(signConvert[j]+"|");
		}
		System.out.println();*/
	}
	
	public void createNewSeq() {
		//this is for getting new unsigned
		for(int i=0;i<oriA_Unsigned.size();i++) {
			
			newA_Unsigned[i] = (Integer) hm.get(oriA_Unsigned.get(i));
			if(signConvert[newA_Unsigned[i]])
				newA_signs[i]=oriA_signs.get(i);
			else{
				if(oriA_signs.get(i))
					newA_signs[i] = false;
				else
					newA_signs[i] = true;
			}
		}
		//shift to arraylist
		for(int i = 0; i<newA_Unsigned.length;i++) {
			newStartUnsigned.add(newA_Unsigned[i]);
			newStartSigns.add(newA_signs[i]);
			
		}
		
	}
	
	//techniqually dunnid new end since following the algo will get to identity 
	
	public ArrayList<Integer> getNewStartUnsigned() {
		return newStartUnsigned;
			
	}
	public ArrayList<Boolean> getNewStartSign() {
		return newStartSigns;
	}
	public ArrayList<Integer> getCurrentReversal(ArrayList<Integer> al,ArrayList<Boolean> signs ) {
		ArrayList<Integer> ans = new ArrayList<Integer>(); 
		for(int i = 0; i < al.size();i++) {
			//if signConvert is positive
			if(signConvert[al.get(i)]) {
				//retain sign
				if(signs.get(i))
					ans.add(oriB_Unsigned.get(al.get(i)));
				else
					ans.add(oriB_Unsigned.get(al.get(i))*-1);
			}
			else {
				if(signs.get(i)) 
					ans.add(oriB_Unsigned.get(al.get(i))*-1);
				else
					ans.add(oriB_Unsigned.get(al.get(i)));
						
				}
			
		}
		return ans;
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> start = new ArrayList<Integer>() ;
		ArrayList<Integer> end = new ArrayList<Integer>();
		
		start.add(0);
		start.add(3);
		start.add(2);
		start.add(1);
		start.add(4);
		start.add(5);
		
		end.add(0);
		end.add(2);
		end.add(4);
		end.add(3);
		end.add(1);
		end.add(5);
		
		ArrayList<Boolean> signsStart = new ArrayList<Boolean>();
		ArrayList<Boolean> signsEnd = new ArrayList<Boolean>();
		
		signsStart.add(true);
		signsStart.add(true);
		signsStart.add(true);
		signsStart.add(true);
		signsStart.add(true);
		signsStart.add(true);
		
		signsEnd.add(true);
		signsEnd.add(false);
		signsEnd.add(true);
		signsEnd.add(false);
		signsEnd.add(true);
		signsEnd.add(true);
		
		Mapper mup = new Mapper(start, end, signsStart,signsEnd);
		ArrayList<Integer> test = mup.getNewStartUnsigned();
		ArrayList<Boolean> test2 = mup.getNewStartSign();
		/*for(int i = 0; i<test.size();i++) {
			
			System.out.print(test.get(i)+"|");
			
			
		}
		System.out.println();
		System.out.println("The line below is the signs of the new function");
		for(int j = 0; j<test2.size();j++) {
			System.out.print(test2.get(j)+"|");
		}
		System.out.println();*/
		ArrayList<Boolean> test3 = mup.getNewStartSign();
		ArrayList<Integer> test4 = mup.getCurrentReversal(test,test3);
		
		/*for(int j = 0; j<test4.size();j++) {
			System.out.print(test4.get(j)+"|");
		}*/
	}
	
	
}
