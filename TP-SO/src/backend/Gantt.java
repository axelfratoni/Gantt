package backend;

public class Gantt {

	private String s;
	private String j;
	
	public Gantt() {
		s = "";
		j = "[";
	}
	
	public void markIO(int time, int io, int klt_id, int ult_id) {
		s += "I/O\nTime: " + time + "\nDevice: " + io + "\nKLT: " + klt_id + "\nULT: " + ult_id + "\n\n";
		j += ",{%22Run%22:%22IO%22,%22Time%22:" + time + ",%22Device%22:" + io + ",%22KLT%22:" + klt_id + ",%22ULT%22:" + ult_id + "}"; 
	}

	public void markCPU(int time, int core_id, int klt_id, int ult_id) {
		s += "CPU\nTime: " + time + "\nCore: " + (core_id+1) + "\nKLT: " + klt_id + "\nULT: " + ult_id + "\n\n";
		if (j.length() > 1){
			j += ",";
		}
		j += "{%22Run%22:%22CPU%22,%22Time%22:" + time + ",%22Core%22:" + (core_id+1) + ",%22KLT%22:" + klt_id + ",%22ULT%22:" + ult_id + "}";
	}
	
	@Override
	public String toString() {
		return s;
	}
	
	public String getJson(){
		j += "]";
		return j;
	}

	public void markSO(int time, int core) {
		s += "Idle process (SO) time: " + time + "\nCore: " + (core+1) + "\n\n";
		j += ",{%22Run%22:%22SO%22,%22Time%22:" + time + ",%22Core%22:" + (core+1) + "}";
	}

}
