import java.util.ArrayList;


public class SeqExtractor {
	private ArrayList<ArrayList<Integer>> extractedSeq = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> extractedUnsignedSeq = new ArrayList<ArrayList<Integer>>();
	private int lengthInput = 0;
	//true is positive, false is negative
	private ArrayList<ArrayList<Boolean>> extractedSigns = new ArrayList<ArrayList<Boolean>>();
	
	public SeqExtractor(ArrayList<String> seqList) {
		
		for(int i=0;i<seqList.size();i++) {
			String tempStr = seqList.get(i);
			tempStr = tempStr.replace(',', ' ');
			String[] tempStrArr = tempStr.split("\\s+");
			convert(tempStrArr);
		}
	}
	//appended change to arraylist
	private void convert(String[] strArr) {
		
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		ArrayList<Integer> unsignedList = new ArrayList<Integer>();
		ArrayList<Boolean> signsList = new ArrayList<Boolean>();
		int len = strArr.length + 1;
		lengthInput = len;
		//int[] intArr = new int[len];
		
		tempList.add(0);
		unsignedList.add(0);
		signsList.add(true);
		
		for(int i=0;i<strArr.length;i++) {
			int tempInt = Integer.parseInt(strArr[i]);
			tempList.add(tempInt);
			if(tempInt<0) {
				tempInt = tempInt*-1;
				unsignedList.add(tempInt);
				signsList.add(false);	
			}
			else {
				unsignedList.add(tempInt);
				signsList.add(true);	
			}
			
		}
		
		tempList.add(len); 
		unsignedList.add(len);
		signsList.add(true);	

		extractedSeq.add(tempList);
		extractedUnsignedSeq.add(unsignedList);
		extractedSigns.add(signsList);
		
		//return tempList;
	}
	//complete to use for hashing i guess
	public ArrayList<ArrayList<Integer>> getList() {
		
		return extractedSeq;
		
	}
	
	public ArrayList<ArrayList<Integer>> getUnsigned() {
		
		return extractedUnsignedSeq;
		
	}
	
	public ArrayList<ArrayList<Boolean>> getSign() {
		
		return extractedSigns;
		
	}
	
	public int getSize() {
		
		return lengthInput;
		
		
	}
	
}
