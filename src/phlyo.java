import java.util.*;
import java.lang.*;

class phylo{
	public static ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();  //store the distance matrix
	public static ArrayList<IntegerPair> min = new ArrayList<IntegerPair>();   //store the index of minimum distance for each row in the matrix
	public static HashMap<String,Integer> SpeciesA =new HashMap<String,Integer>();
	public static HashMap<Integer, String> SpeciesB =new HashMap<Integer,String>();
	public static ArrayList<cluster> clusters = new ArrayList<cluster>();
	public static int numNodes;
	
	public static void main (String[] args){
		
		Scanner sc = new Scanner(System.in);
		
		//GET THE INPUT
		getInput(sc);
		
		//WHILE LOOP WILL START FROM HERE
		
		//GET THE MIN VALUE
		IntegerPair a = getMin();

		//GET THE CHILDREN
		ArrayList<String> childrenList = getChildren(a);

		//MERGE NODE
		merge(a,childrenList);

		//FIND THE NEXT MIN VALUE STEP 1
		ArrayList<IntegerPair> distancesMinVal = getDistances(childrenList,a);

		//FIND THE NEXT MIN VALUE STEP 2
		getMinValues(distancesMinVal);

		//WHILE LOOP WILL END HERE


		
		

	}

	//Method to obtain input
	private static void getInput(Scanner sc){
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
			ArrayList<String> dummy = new ArrayList<String>();
			cluster clusterNew = new cluster(i,dummy);
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
		
			
	}
	
	
	//Method to obtain the minimum pairwise distance from minValues matrix
	private static IntegerPair getMin(){
		IntegerPair  minRow = min.get(0);
		for(int i=1;i<min.size();i++){
			if(min.get(i).getDistance()<minRow.getDistance()){
				minRow = min.get(i);
			}
		}
		return minRow;
	}

	//Method to obtain the children of the min Values
	private static ArrayList<String> getChildren(IntegerPair minV){
		
		ArrayList<String> children = new ArrayList<String>();
		
		//if both are nested clusters, the children arraylist will contain both cluster
		if(clusters.get(minV.getA()).getIndex()>=SpeciesA.size() && clusters.get(minV.getB()).getIndex()>=SpeciesA.size()){
			ArrayList<String> A = clusters.get(minV.getA()).getChildren();
			ArrayList<String> B = clusters.get(minV.getB()).getChildren();
			 children = mergeChildren(A,B);			
		}
		//if A is a cluster and B is a node 
		else if(clusters.get(minV.getA()).getIndex()>=SpeciesA.size()){
			children = clusters.get(minV.getA()).getChildren();
			children.add(SpeciesB.get(clusters.get(minV.getB()).getIndex()));
		}
		//if B is a cluster and A is a node
		else if(clusters.get(minV.getB()).getIndex()>=SpeciesA.size()){
			children = clusters.get(minV.getB()).getChildren();
			children.add(SpeciesB.get(clusters.get(minV.getA()).getIndex()));
		}
		return children;
	}

	//Method to merge 2 ArrayLists
	private static ArrayList<String> mergeChildren(ArrayList<String> A, ArrayList<String> B){
		ArrayList<String> children = A;
		for(int i=0;i<B.size();i++){
			children.add(B.get(i));
		}
		return children;
	}

	//Method to obtain new distances
	private static ArrayList<IntegerPair> getDistances(ArrayList<String> children,IntegerPair minVal){
		int sum=0;
		int count = 0;
		
		ArrayList<String> child = new ArrayList<String>();
		ArrayList<IntegerPair> newDist = new ArrayList<IntegerPair>();
		for(int i=0;i<clusters.size();i++){
			if(i!= minVal.getA() && i != minVal.getB()){
				//case 1: the cluster is a nested cluster
				if(clusters.get(i).getIndex()>=SpeciesA.size()){
					child = clusters.get(i).getChildren();
					for(int x=0;x<children.size();x++){
						int indexA = SpeciesA.get(children.get(x));
						for(int y=0;y<children.size();y++){
						int indexB = SpeciesA.get(child.get(y));
						 	sum = sum +matrix.get(indexA).get(indexB);
					}
					}
					sum = sum * (1/(children.size() * child.size()));
				//case 2: the cluster is a single node
				}else{
					int indexA = SpeciesA.get(clusters.get(i).getIndex());
					for(int j=0;j<children.size();j++){
						int indexB = SpeciesA.get(children.get(j));
						sum = sum + matrix.get(i).get(j);
					}
					sum = sum * (1/(children.size()));
			}
			
				IntegerPair c = new IntegerPair(i,minVal.getA(),sum);
				newDist.add(c);
		sum = 0;
		child.clear();	}
		}
	return newDist;
	}
	//Method to find and store the minimum values
	private static void getMinValues(ArrayList<IntegerPair> newDist){
		ArrayList<IntegerPair> temp = new ArrayList<IntegerPair>();
		int minTotal = 10000;
		int index = 0 ;
		temp.add(new IntegerPair(0,0,0));

		for(int i =0;i<newDist.size();i++){
			if(minTotal > newDist.get(i).getDistance()){
				minTotal = newDist.get(i).getDistance();
				 index = i;
			}
			if(min.get(newDist.get(i).getA()).getDistance() > newDist.get(i).getDistance()){
				IntegerPair x = new IntegerPair(0,i,newDist.get(i).getDistance());
				temp.add(x);

			}
		}
		temp.set(0,new IntegerPair(0,index,minTotal));
		min.clear();
		min = temp;
	}
	
	//Method to merge 2 clusters/node
	private static void merge(IntegerPair minV,ArrayList<String> children){
		cluster newCluster = new cluster(numNodes++,children);
		ArrayList<cluster> tempCluster = new ArrayList<cluster>();
		tempCluster.add(newCluster);
		for(int i=0;i<min.size()-1;i++){
			if(i!=minV.getA() && i!= minV.getB()){
				tempCluster.add(clusters.get(i));
			}
		}
		clusters.clear();
		clusters=tempCluster;
	}

}

