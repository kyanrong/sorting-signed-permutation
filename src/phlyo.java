import java.util.*;
import java.lang.*;

class phylo{
	public static ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();  //store the distance matrix
	ArrayList<Integer> minDistance = new ArrayList<Integer>();   //store the index of minimum distance for each row in the matrix
	public static HashMap<String,Integer> SpeciesA =new HashMap<String,Integer>();
	public static HashMap<Integer, String> SpeciesB =new HashMap<Integer,String>();
	public static ArrayList<cluster> clusters = new ArrayList<cluster>();
	public static void main (String[] args){
		Scanner sc = new Scanner(System.in);
		ArrayList<IntegerPair> minValues = getInput(sc);
		IntegerPair a = getMin(minValues);
		System.out.println(a.getA());
		System.out.println(a.getB());
		System.out.println(a.getDistance());
		

	}

	//Method to obtain input
	private static ArrayList<IntegerPair> getInput(Scanner sc){
		ArrayList<IntegerPair> min = new ArrayList<IntegerPair>();
		int numNodes = sc.nextInt();
		String name ="";
		int num = 0;
		int minRow = 10000;
		int rowIndex = 0;
		int colIndex = 0;
		
		for(int i=0;i<numNodes;i++){
			name = sc.next();
			SpeciesA.put(name,i);
			SpeciesB.put(i,name);
			cluster clusterNew = new cluster(i,null,0);
			clusters.add(clusterNew);
			ArrayList<Integer> row = new ArrayList<Integer>();
			num=10000000;
			for(int j=0;j<numNodes;j++){
				num = sc.nextInt();
				if(num < minRow && i!=j){
					minRow = num;
					rowIndex=i;
					colIndex=j;
				}
				row.add(num);	
			}
			IntegerPair values = new IntegerPair(rowIndex,colIndex,minRow);
			min.add(values);
			matrix.add(row);
			
		}
		return min;
			
	}
	
	
	//Method to obtain the minimum pairwise distance from minValues matrix
	private static IntegerPair getMin(ArrayList<IntegerPair> minValues){
		IntegerPair  minRow = minValues.get(0);
		for(int i=1;i<minValues.size();i++){
			if(minValues.get(i).getDistance()<minRow.getDistance()){
				minRow = minValues.get(i);
			}
		}
		return minRow;
	}

	//Method to obtain new distance values
	private static ArrayList<IntegerPair> distances(ArrayList<IntegerPair> minValues, IntegerPair min){
		int sum = 0;
		//if both are clusters
		if(clusters.get(min.getA())>=SpeciesA.size() && clusters.get(min.getB())>=SpeciesA.size()){
			ArrayList<String> A = clusters.get(min.getA());
			ArrayList<String> B = clusters.get(min.getB());
			ArrayList<String> children = mergeChildren(A,B);
			for(int i=0;i<minValues;i++){
				
			}


					
		}
		
	}

	//Method to merger 2 ArrayLists
	private static ArrayLists<String> mergeChildren(ArrayList<String> A, ArrayList<String> B){
		ArrayList<String> children = A;
		for(int i=0;i<B.size();i++){
			children.add(B.get(i));
		}
	}

	//Method to merge 2 clusters/node
	private static void merge(IntegerPair min){



	}

}

