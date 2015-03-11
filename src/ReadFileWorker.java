import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.ArrayList;


public class ReadFileWorker implements Callable<HashMap<String, Integer>>{

	private String fname;
	private long pos_start;
	private long to_read;
	
	public ReadFileWorker(String fname, long pos_start, long to_read) {
		this.fname = fname;
		this.pos_start = pos_start;
		this.to_read = to_read;
	}
		
	public HashMap<String, Integer> call() throws Exception {
		// TODO Auto-generated method stub
		
		RandomAccessFile file = new RandomAccessFile(fname, "r");
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String string = "";
		try {

			//daca incepe in cuvant
			if (pos_start > 0) {
				file.seek(pos_start - 1);

				char c1 = (char) file.readByte();
				if (isNotDelimiter(c1)) {
					char c = (char) file.readByte();
					while(isNotDelimiter(c)) {
						c = (char) file.readByte();
						to_read--;
					}
				}
			}	
			else
				file.seek(pos_start);
			
			byte[] all = new byte[(int) to_read];
			file.readFully(all);
			
			
			String s = new String(all);
			String delim = ";:/?~\\.,><~`[]{}()!@#$%^&-_+'=*\"| \t\n";
			StringTokenizer st = new StringTokenizer(s, delim);
			while (st.hasMoreTokens()) {
				string = st.nextToken().toLowerCase();
				if (st.hasMoreTokens()) {
					if (!map.containsKey(string)) {
						map.put(string, 0);
					}
					map.put(string, map.get(string) + 1);
				}
			} 

			//daca ultimul caracter a fost litera/cifra citesc pana cand se termina actualul cuv
			file.seek(file.getFilePointer() - 1);
			char tmp = (char) file.readByte();
			
			if (isNotDelimiter(tmp)) {
				char c = (char) file.readByte();

				while(isNotDelimiter(c)) {
					string += c;
					c = (char) file.readByte();
				}
				if (!map.containsKey(string)) {
					map.put(string, 0);
				}
				map.put(string, map.get(string) + 1);
				string = "";
			}
		} catch (Exception e) {
			System.out.println("eroare");
		}

		file.close();
		return map;
	}
	
	
	boolean isNotDelimiter(char c) {
		
		boolean isNotDel = true;
		ArrayList<Character> delimiters = new ArrayList<Character>();
		
		delimiters.add(';');
		delimiters.add(':');
		delimiters.add('/');
		delimiters.add('?');
		delimiters.add('~');
		delimiters.add('\\');
		delimiters.add('.');
		delimiters.add(',');
		delimiters.add('>');
		delimiters.add('<');
		delimiters.add('~');
		delimiters.add('`');
		delimiters.add('[');
		delimiters.add(']');
		delimiters.add('{');
		delimiters.add('}');
		delimiters.add('(');
		delimiters.add(')');
		delimiters.add('!');
		delimiters.add('@');
		delimiters.add('#');
		delimiters.add('$');
		delimiters.add('%');
		delimiters.add('^');
		delimiters.add('&');
		delimiters.add('-');
		delimiters.add('_');
		delimiters.add('+');
		delimiters.add('\'');
		delimiters.add('=');
		delimiters.add('*');
		delimiters.add('"');
		delimiters.add('|');
		delimiters.add(' ');
		delimiters.add('\t');
		delimiters.add('\n');
		
		for (char del : delimiters) {
			if (c == del) 
				isNotDel = false;
		}
		
		return isNotDel;
	}

}
