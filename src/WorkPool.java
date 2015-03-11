import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class WorkPool {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		
		int nr_workers = 0;
		int num_threads = Integer.parseInt(args[0]);
		
		ArrayList<ArrayList<Future<HashMap<String, Integer>>>> all = new ArrayList<ArrayList<Future<HashMap<String, Integer>>>>();
		ArrayList<Future<HashMap<String, Integer>>> merged = new ArrayList<Future<HashMap<String, Integer>>>();

		String name = args[1];
		ArrayList<String> filenames = new ArrayList<String>();
		
		Scanner scanner = new Scanner(new File(name));
		long toRead = scanner.nextLong();
		float grade = scanner.nextFloat();
		int nr_files = scanner.nextInt();
		for(int i = 0; i < nr_files; i++) {
			filenames.add(scanner.next());
		}
		scanner.close();
		
		ExecutorService pool = Executors.newFixedThreadPool(num_threads);
		
		for(String fname : filenames) {
			RandomAccessFile file = new RandomAccessFile(fname, "r");
			long pos_start = 0;
			long len = file.length();
			long l = 0;
			while (l < len) {
				l += toRead;
				nr_workers++;
			}
			file.close();
			
			ArrayList<Future<HashMap<String, Integer>>> test = new ArrayList<Future<HashMap<String, Integer>>>();
			
			for (int i = 0; i < nr_workers; i++) {
				if (i == (nr_workers - 1)) {
					test.add(pool.submit(new ReadFileWorker(fname, pos_start, len - pos_start - 1)));
				}
				else
					test.add(pool.submit(new ReadFileWorker(fname, pos_start, toRead)));
				pos_start += toRead;
			}	
			all.add(test);
			nr_workers = 0;
			
		}

		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.SECONDS);
		
		
		ExecutorService pool_hash = Executors.newFixedThreadPool(num_threads);
		
		for (int i = 0; i < nr_files; i++) {
			merged.add(pool_hash.submit(new ReduceWorker(all.get(i))));
		}
				
		pool_hash.shutdown();
		pool_hash.awaitTermination(1, TimeUnit.SECONDS);
		
		//nr cuvinte
		ArrayList<Long> nr_words = new ArrayList<Long>();
		for(int i = 0; i < merged.size(); i++) {
			long nr = 0;
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			map = merged.get(i).get();
			for (String s : map.keySet()) {
				nr += map.get(s);
			}
			nr_words.add(nr);
		}
		
		ExecutorService pool_compare = Executors.newFixedThreadPool(num_threads);
		ArrayList<Pair> pair = new ArrayList<Pair>();
		ArrayList<Future<Pair>> future_pair = new ArrayList<Future<Pair>>();
		
		for (int i = 0; i < filenames.size(); i ++) {
			for (int j = i + 1; j < filenames.size(); j++) {
				future_pair.add(pool_compare.submit(new ComparisonWorker(merged.get(i), merged.get(j), i, j, nr_words.get(i), nr_words.get(j))));
			}
		}
		
		pool_compare.shutdown();
		pool_compare.awaitTermination(1, TimeUnit.SECONDS);
		
		for (Future<Pair> p : future_pair) {
			pair.add(p.get());
		}
		
		Collections.sort(pair, new Comparator<Pair> () {

			public int compare(Pair arg0, Pair arg1) {
				// TODO Auto-generated method stub
				if (arg0.c > arg1.c)
					return -1;
				else if (arg1.c > arg0.c)
					return 1;
				return 0;
			}});
			
			

		RandomAccessFile out = new RandomAccessFile(args[2], "rw");
		
		for(Pair p : pair) {
			float c = p.c;
			c *= 10000;
			c = (float) Math.floor(c);
			c /= 10000;
			p.c = c;
			String s = filenames.get(p.a) + ";" + filenames.get(p.b) + ";" + p.c + "\n";
			if (p.c > grade)
				out.write(s.getBytes());
		}

		out.close();

	}

}
