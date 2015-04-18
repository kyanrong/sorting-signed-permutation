import java.util.*;
import java.lang.*;
import java.io.*;

class P3{
	public static ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();  //store the distance matrix
	
	public static HashMap<String,Integer> SpeciesA =new HashMap<String,Integer>(); //key is the name
	
	public static HashMap<Integer, String> SpeciesB =new HashMap<Integer,String>(); //key is the index
	
	public static ArrayList<CNode> nodes = new ArrayList<CNode>();
	
	public static int numNodes;
	public P3(){

		try{
			
			//Create an instance of program 2 to obtain distance matrix
			P2NoPrint p2 = new P2NoPrint();
			
			//GET THE INPUT
			int[][] dist = p2.getMatrix();
			ArrayList<String> names = p2.getNames();

			//TRANSFER THE INPUT TO APPROPIATE DATA STRUCTURES
			process(dist,names);

			int countNodes = numNodes;

			while(countNodes > 1){
				//CALCULATE r VALUES
				ArrayList<Double> r = getRValues(countNodes);

				//CALCULATE Mij VALUES AND FIND THE SMALLEST VALUE
				IntegerPair min = getMinM(r);
				

				//CREATE A NEW NODE and update distance and node list
				createNewNode(min,r);

				//UPDATE AND CREATE NEW MATRIX
				updateMatrix(min);

				countNodes--;
			}

			//PRINT THE TREE
			printBinaryTree(nodes.get(0),0);

		}catch(IOException e){

		}
	}

	public static void printBinaryTree(CNode root,int level){
   		
   		if(root==null)
        	return;
    	printBinaryTree(root.getChildR(),level+1);
    	if(level!=0){
        	for(int i=0;i<level-1;i++)
            	System.out.print("|\t");
            	if(root.getDistance()==0.0){
            		System.out.println("|-------" +root.getIndex() +"|");
            	}else{
            		System.out.println("|-------" +root.getIndex() +"("+root.getDistance()+ ")");
    			}
    	}
    	else
        	System.out.println(root.getIndex());
    	
    	printBinaryTree(root.getChildL(),level+1);
	}

	//PROCESS THE INPUT
	private static void process(int[][] dist, ArrayList<String> names){
		
		numNodes = names.size();

		for(int i=0;i<names.size();i++){
			SpeciesA.put(names.get(i),i);
			SpeciesB.put(i,names.get(i));
			CNode newNode = new CNode(null,"leaf",names.get(i));
			nodes.add(newNode);
			ArrayList<Double> row = new ArrayList<Double>();
		for(int j=0;j<names.size();j++){
			row.add((double)dist[i][j]);
		}
		
		matrix.add(row);			

		}
	}

	//CALCULATE r VALUES
	private static ArrayList<Double> getRValues(int countNodes){
		ArrayList<Double> r = new ArrayList<Double>();
		double value = 0.0;
		double n = (double)1/(countNodes-2);
		for(int i=0;i<countNodes;i++){
			value = 0;
			
			for(int j=0;j<countNodes;j++){
				value = value + matrix.get(i).get(j);
			}
			value = value * n;
			r.add(value);
		}

		return r;
	}

	//CALCULATE Mij VALUES AND FIND THE MIN
	private static IntegerPair getMinM(ArrayList<Double> r){
		int indexA = 0 ;
		int indexB = 0 ;
		double minValue = 10000000.00;
		int start = 0;
		for(int i=0;i<r.size();i++){
			for(int j= start;j<r.size();j++){
				if(i!=j){
					double value = matrix.get(i).get(j) - r.get(i) - r.get(j);
					if(value<minValue){
						minValue = value;
						indexA = i;
						indexB = j;
					}
				}	
			}
			start++;
		}
		
		IntegerPair min = new IntegerPair(indexA,indexB,minValue);
		return min;
	}		

	//CREATE A NEW NODE and update distance and nodes list
	private static void createNewNode(IntegerPair min,ArrayList<Double> r){
		CNode A = nodes.get(min.getA());
		CNode B = nodes.get(min.getB());
		double distA =  matrix.get(min.getA()).get(min.getB()) + r.get(min.getA()) -r.get(min.getB()) ;
		double distB = matrix.get(min.getA()).get(min.getB()) + r.get(min.getB()) -r.get(min.getA()) ;
		distA = distA/2;
		distB = distB/2;
		String name ="";
		
		if(nodes.size()<3){
			if(!A.getIndex().equals("")){
				A.addDistance(matrix.get(min.getA()).get(min.getB()));
			}
			if(!B.getIndex().equals("")){
				B.addDistance(matrix.get(min.getA()).get(min.getB()));
			}
			
			name ="|";
		}else{
			A.addDistance(distA);
			B.addDistance(distB);
			
		}

		CNode parent = new CNode(null,"inner",name);

		
		A.setParent(parent);
		B.setParent(parent);
		
		
		
		
		parent.addChildL(A);
		parent.addChildR(B);

		ArrayList<CNode> temp = new ArrayList<CNode>();
		temp.add(parent);
		for(int i=0;i<nodes.size();i++){
			if(i!=min.getA() && i!=min.getB()){
				temp.add(nodes.get(i));
			}
		}
		nodes.clear();
		nodes = temp;
	}

	//UPDATE AND CREATE NEW MATRIX
	private static void updateMatrix(IntegerPair min){
		//Compute the new m values for new node
		ArrayList<Double> tempM = new ArrayList<Double>();
		tempM.add(0.0);
		for(int i=0;i<matrix.size();i++){
			if(i!=min.getA() && i!=min.getB()){
				double distance = matrix.get(i).get(min.getA()) + matrix.get(i).get(min.getB()) - matrix.get(min.getA()).get(min.getB());
				distance = distance * 0.5;
				tempM.add(distance);
			}
		}

		//update matrix
		
		ArrayList<ArrayList<Double>> tempMatrix = new ArrayList<ArrayList<Double>>();
		tempMatrix.add(tempM);
		int countX = 1;
		
		for(int x=0;x<matrix.size();x++){
			ArrayList<Double> tempRow = new ArrayList<Double>();
			if(x!=min.getA() && x!=min.getB()){
				tempRow.add(tempM.get(countX));
				for(int y=0;y<matrix.size();y++){
					if(y!=min.getA() && y!=min.getB()){
						if(x==y){
							tempRow.add(0.0);
						}else{
							tempRow.add(matrix.get(x).get(y));
						}
					}


				}
				countX++;
			tempMatrix.add(tempRow);
			}
			
		}
		matrix.clear();
		matrix = tempMatrix;

	}		
	
}