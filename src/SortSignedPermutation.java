import java.io.IOException;
import java.util.Scanner;


public class SortSignedPermutation {

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Please input the program number (1, 2, 3): ");
		int progNum = sc.nextInt();
		
		switch(progNum) {
			// 2 inputs
			case 1: P1 p1 = new P1(); 
					break;
			// file input
			case 2: 
					P2 p2 = new P2();
					break;
			// file input
			case 3: 
					break;
			
		}
	}
}

