import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

class Pair {
	int a;
	int b;
	float c;
	
	public Pair(int a, int b, float c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
}

public class ComparisonWorker implements Callable<Pair>{
	
	private Future<HashMap<String, Integer>> future1;
	private Future<HashMap<String, Integer>> future2;
	int file1; // indice file1
	int file2; // indice file2
	float n1; //numitor1
	float n2; //numitor2
	
	public ComparisonWorker(Future<HashMap<String, Integer>> f1, Future<HashMap<String, Integer>> f2, int file1, int file2, float n1, float n2) {
		this.future1 = f1;
		this.future2 = f2;
		this.file1 = file1;
		this.file2 = file2;
		this.n1 = n1;
		this.n2 = n2;
	}
	
	public Pair call() throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, Integer> h1 = future1.get();
		HashMap<String, Integer> h2 = future2.get();
		
		float suma = 0;
		
		for (String s : h1.keySet()) {
			if (h2.containsKey(s)) {
				suma += ((h1.get(s) * 100) / n1) * ((h2.get(s) * 100)/ n2);
			}
		}
		
		return new Pair(file1, file2, suma / 100);
	}
}