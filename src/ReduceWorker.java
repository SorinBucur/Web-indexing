import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


public class ReduceWorker implements Callable<HashMap<String, Integer>>{
	
	private ArrayList<Future<HashMap<String, Integer>>> all;
	
	public ReduceWorker(ArrayList<Future<HashMap<String, Integer>>> all) {
		this.all = all;
	}

	public HashMap<String, Integer> call() throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		for(Future<HashMap<String, Integer>> hm : all) {
			for(Map.Entry<String, Integer> entry : hm.get().entrySet()){
				String string = entry.getKey();
				int nr = entry.getValue();

				if (!map.containsKey(string)) {
					map.put(string, nr);
				}
				else
					map.put(string, map.get(string) + nr);	
			}
		}

		return map;
	}

}
