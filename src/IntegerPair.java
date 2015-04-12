class IntegerPair{
	private static int A;
	private static int B;
	private static int distance;


	public IntegerPair(int a, int b,int value){
		A = a;
		B = b;
		distance = value;
	}

	public static int getA(){
		return A;
	}

	public static int getB(){
		return B;
	}

	public static int getDistance(){
		return distance;
	}
}