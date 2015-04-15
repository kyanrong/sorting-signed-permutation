class IntegerPair{
	private  int A;
	private  int B;
	private String nameA;
	private String nameB;
	private int distance;


	public IntegerPair(int a, int b,int value){
		A = a;
		B = b;
		distance = value;
	}

	public IntegerPair(int a, int b,int value,String nameX,String nameY){
		A = a;
		B = b;
		distance = value;
		nameA = nameX;
		nameB = nameY;
	}


	public  int getA(){
		return A;
	}

	public  int getB(){
		return B;
	}

	public  int getDistance(){
		return distance;
	}

	
} 