import java.util.*;
import java.lang.*;
import java.io.*;

class P3{
	public static ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();  //store the distance matrix
	public static ArrayList<IntegerPair> min = new ArrayList<IntegerPair>();   //store the index of minimum distance for each row in the matrix
	public static HashMap<String,Integer> SpeciesA =new HashMap<String,Integer>(); //key is the name
	public static HashMap<Integer, String> SpeciesB =new HashMap<Integer,String>(); //key is the index
	public static ArrayList<cluster> clusters = new ArrayList<cluster>();
	public static int numNodes;
	
	public static void main (String[] args){
		
		Scanner sc = new Scanner(System.in);
		
		//GET THE INPUT
		try{
			getInput();
		}catch(IOException e){

		}
		
		CNode root= new CNode();
	
		//WHILE LOOP STARTS
		while(clusters.size()>1){
		//GET THE MIN VALUE
		IntegerPair a = getMin();
		
		//GET THE CHILDREN
		ArrayList<String> childrenList = getChildren(a);
		
		
		//FORM THE NEW NODE
		CNode parentNode = getNewNode(a);
		root = parentNode;
		
		//MERGE NODE
		merge(a,childrenList,parentNode);
	
		//FIND THE NEXT MIN VALUE STEP 1
		ArrayList<IntegerPair> distancesMinVal = getDistances(childrenList);
		
		
		//EDIT THE MIN ARRAYLIST
		editMin(a);
		
		//FIND THE NEXT MIN VALUE STEP 2
		getMinValues(distancesMinVal);
		
		}
		//WHILE LOOP WILL END HERE
		
		//PRINTING THE TREE START FROM ROOT
		printBinaryTree(root,0);

	}

	
	//CODE WITH REFERENCE FROM: http://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram
	public static void printBinaryTree(CNode root,int level){
   		if(root==null)
        	return;
    	printBinaryTree(root.getChildR(),level+1);
    	if(level!=0){
        	for(int i=0;i<level-1;i++)
            	System.out.print("|\t");
            	System.out.println("|-------" +root.getIndex() +"("+root.getDistance()+ ")");
    	}
    	else
        	System.out.println(root.getIndex());
    	
    	printBinaryTree(root.getChildL(),level+1);
}


	private static void getInput() throws IOException {
		Scanner sc = new Scanner(System.in);
		String path = sc.nextLine();
		//String path = "datasets/viral_genome.txt";
		;
		
		FileReader fr = new FileReader(new File(path));
		BufferedReader br = new BufferedReader(fr);
		
		String line;
		String name = null;
		int num = 0;
		int minRow = 10000;
		int rowIndex = 0;
		int colIndex = 0;
		int count = 0;
		while((line=br.readLine())!= null) {
			String[] scores = line.split("\\s+");
			name = scores[0].substring(0,scores[0].length()-1);
			
			SpeciesA.put(name,count);
			SpeciesB.put(count,name);


			CNode newNode = new CNode(null,"leaf",name);
			ArrayList<String> dummy = new ArrayList<String>();
			cluster clusterNew = new cluster(count,dummy,newNode);
			clusters.add(clusterNew);
			ArrayList<Integer> row = new ArrayList<Integer>();
			minRow=10000000;

			for(int x=1;x<scores.length;x++){
				String temp = scores[x].trim();
				 
				 if(x!=scores.length-1){
				 temp = temp.substring(0,scores[x].length()-1);
				 }
				num = Integer.parseInt(temp);
				System.out.println("num is " +num);
				if(num < minRow && count!=x-1){
					minRow = num;
					rowIndex=count;
					colIndex=x-1;
				}
				row.add(num);	

			}

		IntegerPair values = new IntegerPair(rowIndex,colIndex,minRow);
		boolean check = min.add(values);
		matrix.add(row);		
		count++;		
			
		}
		numNodes = count;
		
		br.close();
		sc.close();
	}



	//Method to obtain input
	/*private static void getInput(Scanner sc){
		
		numNodes = sc.nextInt();
		String name ="";
		int num = 0;
		int minRow = 10000;
		int rowIndex = 0;
		int colIndex = 0;
		
		for(int i=0;i<numNodes;i++){
			name = sc.next();
			SpeciesA.put(name,i);
			SpeciesB.put(i,name);
			
			CNode newNode = new CNode(null,"leaf",name);
			ArrayList<String> dummy = new ArrayList<String>();
			cluster clusterNew = new cluster(i,dummy,newNode);
			clusters.add(clusterNew);
			ArrayList<Integer> row = new ArrayList<Integer>();
			minRow=10000000;
			
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
			boolean check = min.add(values);
			matrix.add(row);
			
		}
		
			
	} */
	
	
	//Method to obtain the minimum pairwise distance from minValues matrix
	private static IntegerPair getMin(){
		IntegerPair  minRow = min.get(0);
		
		for(int i=1;i<min.size();i++){
			if(min.get(i).getDistance()< minRow.getDistance()){
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
		else if(clusters.get(minV.getA()).getIndex()>=SpeciesA.size() && clusters.get(minV.getB()).getIndex()<SpeciesA.size()){
			children = clusters.get(minV.getA()).getChildren();
			children.add(SpeciesB.get(clusters.get(minV.getB()).getIndex()));
		}
		//if B is a cluster and A is a node
		else if(clusters.get(minV.getB()).getIndex()>=SpeciesA.size() && clusters.get(minV.getA()).getIndex()<SpeciesA.size()){
			children = clusters.get(minV.getB()).getChildren();
			children.add(SpeciesB.get(clusters.get(minV.getA()).getIndex()));
		}
		//else both A nd B are nodes
		else{
			children.add(SpeciesB.get(clusters.get(minV.getA()).getIndex()));
			children.add(SpeciesB.get(clusters.get(minV.getB()).getIndex()));	
		}
		
		return children;
	}

	//Method to merge 2 ArrayLists
	private static ArrayList<String> mergeChildren(ArrayList<String> A, ArrayList<String> B){
		ArrayList<String> children = A;
		
		int stop = B.size();
		for(int w=0;w<stop;w++){
			String z = B.get(w);
			children.add(z);
			
		}
		return children;
	}

	//Method to obtain new distances
	private static ArrayList<IntegerPair> getDistances(ArrayList<String> children){
		int sum=0;
		int count = 0;
		
		ArrayList<IntegerPair> newDist = new ArrayList<IntegerPair>();
		IntegerPair dum = new IntegerPair(0,0,0);
		newDist.add(dum);

		for(int i=1;i<clusters.size();i++){
			ArrayList<String> child = new ArrayList<String>();
				//case 1: the cluster is a nested cluster
				if(clusters.get(i).getIndex()>=SpeciesA.size()){
					
					child = clusters.get(i).getChildren();
					
					for(int x=0;x<children.size();x++){
						int indexA = SpeciesA.get(children.get(x));
						for(int y=0;y<child.size();y++){
						int indexB = SpeciesA.get(child.get(y));
						 	sum = sum +matrix.get(indexA).get(indexB);
						 	
						}
					}
					//sum = sum * (1/(children.size() * child.size()));
					double tempSum = (double) 1/(children.size() *child.size());
					tempSum = Math.floor(sum * tempSum);
					sum =(int) tempSum;

				//case 2: the cluster is a single node
				}else{
					
					int indexA = clusters.get(i).getIndex();
					for(int j=0;j<children.size();j++){
						int indexB = SpeciesA.get(children.get(j));
						sum = sum + matrix.get(indexA).get(indexB);
						
					}
					
					 double tempSum = (double) 1/(children.size());
					tempSum = Math.floor(sum * tempSum);
					sum =(int) tempSum;
					
			}
				
			IntegerPair c = new IntegerPair(0,i,sum);
			newDist.add(c);
			sum = 0;	
		}
	return newDist;
	}
	
	//Method to find and store the minimum values
	private static void getMinValues(ArrayList<IntegerPair> newDist){
		ArrayList<IntegerPair> temp = new ArrayList<IntegerPair>();
		int minTotal = 10000;
		int index = 0 ;
		temp.add(new IntegerPair(0,0,0));

		for(int i =1;i<newDist.size();i++){
			
			//get the min value for the new cluster	
			if(minTotal > newDist.get(i).getDistance()){
				minTotal = newDist.get(i).getDistance();
				 index = i;
			}
			if(clusters.size()>2){
				//get the min value for the other clusters
				if(min.get(i-1).getDistance() > newDist.get(i).getDistance()){
					IntegerPair x = new IntegerPair(i,0,newDist.get(i).getDistance());
					temp.add(x);
			
				}else{
					IntegerPair v = min.get(i-1);
					temp.add(min.get(i-1));
				}
			}
		
		}

		temp.set(0,new IntegerPair(0,index,minTotal));
		
		min.clear();
		min = temp;
	}
	
	private static CNode getNewNode(IntegerPair minV){
		CNode newParent = new CNode(null,"inner","");
		CNode nodeA = clusters.get(minV.getA()).getNode();
		nodeA.setParent(newParent);
		if(nodeA.getType().equals("leaf")){
			double dist = Math.floor((double)minV.getDistance()/2);
			nodeA.addDistance((int)dist);
			nodeA.addTotalDistance((int)dist);
		}else{
			double dist = Math.floor((double)minV.getDistance()/2);
			int d = (int)dist;
			int currentD = nodeA.getChildR().getTotalDistance();
			nodeA.addDistance(d-currentD);

		}
		
		CNode nodeB = clusters.get(minV.getB()).getNode();
		nodeB.setParent(newParent);
		if(nodeB.getType().equals("leaf")){
			double dist = Math.floor((double)minV.getDistance()/2);
			nodeB.addDistance((int)dist);
			nodeB.addTotalDistance((int)dist);
		}else{
			double dist = Math.floor((double)minV.getDistance()/2);
			int d = (int)dist;
			int currentD = nodeB.getChildR().getTotalDistance();
			nodeB.addDistance(d-currentD);
			
		}

		newParent.addChildL(nodeA);
		newParent.addChildR(nodeB);
		newParent.addTotalDistance(minV.getDistance());

		return newParent;
	}


	//Method to merge 2 clusters/node
	private static void merge(IntegerPair minV,ArrayList<String> children,CNode newParent){

		cluster newCluster = new cluster(numNodes++,children,newParent);
		
		ArrayList<cluster> tempCluster = new ArrayList<cluster>();
		tempCluster.add(newCluster);
		
		for(int i=0;i<min.size();i++){
			
			if(i!=minV.getA() && i!= minV.getB()){
				tempCluster.add(clusters.get(i));
			}
		}
		clusters.clear();
		clusters=tempCluster;
	}
	//EDIT THE MIN ARRAYLIST-UPDATE ACCORDING TO LATEST CLUSTER FORMATION
	private static void editMin(IntegerPair minimum){
		
		ArrayList<IntegerPair> storage = new ArrayList<IntegerPair>();
		for(int i=0;i<min.size();i++){
			int a =0;
			int b=0;
			
			if(i!=minimum.getA() && i!=minimum.getB()){
				a = min.get(i).getA();
				b = min.get(i).getB();
				

				if(min.get(i).getB() != minimum.getA() &&min.get(i).getB() != minimum.getB()){
				//if both min cluster components were above before
				if(minimum.getA() < min.get(i).getA() && minimum.getB() < min.get(i).getA()){
					a--;
				}else if(minimum.getA() > min.get(i).getA() && minimum.getB() > min.get(i).getA()){
					a++;
				}
				
				if(minimum.getA() < min.get(i).getB() && minimum.getB() < min.get(i).getB()){
					b--;
				}else if(minimum.getA() > min.get(i).getB() && minimum.getB() > min.get(i).getB()){
					b++;
				}
				
				
				IntegerPair z = new IntegerPair(a,b,min.get(i).getDistance());
				storage.add(z);
			}else{
				if(minimum.getA() < min.get(i).getA() && minimum.getB() < min.get(i).getA()){
					a--;
				}else if(minimum.getA() > min.get(i).getA() && minimum.getB() > min.get(i).getA()){
					a++;
				}
				IntegerPair z = new IntegerPair(a,0,1000000);
				storage.add(z);
			}
			}
		}
		min.clear();
		min = storage;
	}

}

