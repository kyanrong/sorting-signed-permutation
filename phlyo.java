import java.util.*;
import java.lang.*;

class phylo{
	ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();  //store the distance matrix
		ArrayList<Integer> minDistance = new ArrayLIst<Integer>();   //store the index of minimum distance for each row in the matrix
	
	public static void main (String[] args){
		getInput();

	}

	//Method to obtain input
	private static getInput(){

	}
	
	//Method to obtain the minimum pairwise distance from matrix
	private static getMin(){
		int minRow = 100000000;
		int overallMin = 100000000;

		for(int i=0;i<matrix.size()i++){
			ArrayList<Integer> row = matrix.get(i);
			for(int j=0;j<row.size();j++){
				if(i != j){
					if(minRow > row.get(j)){
						minRow =row.get(j);
					}

				}
			}
			if(overallMin > minRow){
				overallMin = minRow;
			}

			minRow=-1;
		}

	}
}

