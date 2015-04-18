class IntegerPair{
	private  int A;
	private  int B;
	private String nameA;
	private String nameB;
	private double distance;


	public IntegerPair(int a, int b,double value){
		A = a;
		B = b;
		distance = value;
	}

	public IntegerPair(int a, int b,double value,String nameX,String nameY){
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

	public  double getDistance(){
		return distance;
	}

	
} 