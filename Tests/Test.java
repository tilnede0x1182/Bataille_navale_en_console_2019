import java.util.Arrays;

class Test {
	IA ia;
	public Test () {
		int i;
		ia = new IA();

		int [] tmp = {2, 4, 1};

		Arrays.sort(tmp);

		for (i=0; i<tmp.length; i++) {
			aff("tmp["+i+"] = "+tmp[i]);
		}
	}

	public void aff (String oo) {
		System.out.println(oo);
	}

	public void affnn (String oo) {
		System.out.print(oo);
	}

	public static void main (String [] args) {
		Test test = new Test();
	}
}