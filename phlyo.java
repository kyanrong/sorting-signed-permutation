import java.util.*;
import java.lang.*;

class phylo{
	ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();  //store the distance matrix
	ArrayList<Integer> minDistance = new ArrayLIst<Integer>();   //store the index of minimum distance for each row in the matrix
	ArrayList<String> Species =new ArrayList<String>();
	
	public static void main (String[] args){
		Scanner sc = new Scanner(System.in);
		ArrayList<IntegerPair> minValues = getInput(sc);
		IntgerPair a = getMin(minValues);
		System.out.println(a.getA());
		System.out.println(a.getB());
		System.out.println(a.getDistance());

	}

	//Method to obtain input
	private static ArrayList<IntgerPair> getInput(Scanner sc){
		ArrayList<IntegerPair> min = new ArrayList<IntegerPair>();
		int numNodes = sc.nextInt();
		String name ="";
		int num = 0;
		int minRow = 10000;
		int row = 0;
		int col = 0;
		
		for(int i=0;i<numNodes;i++){
			name = sc.next();
			Species.add(name);
			ArrayList<Integer> row = new ArrayList<Integer>();
			num=10000000
			for(int j=0;j<numNodes;j++){
				num = sc.nextInt();
				if(num < minRow){
					minRow = num;
					row=i;
					col=j;
				}
				row.add(num);	
			}
			IntgerPair values = new IntegerPair(row,col,minRow);
			min.add(values);
			matrix.add(row);
			
		}
		return min;
			
	}
	
	
	//Method to obtain the minimum pairwise distance from minValues matrix
	private static IntegerPair getMin(ArrayList<IntegrePair> minValues){
		IntegerPair  minRow = minValues.get(0);

		
		for(int i=1;i<minValues.size()i++){
			if(minValues.get(i).getDistance()<minRow){
				minRow = minValues.get(i);
			}
		}
		return minRow;

	}
}

