package backend;

public class Gantt {

	private String s;
	private String j;
	private String[] ready;
	private String[][] io;
	private int fullTime;
	
	public Gantt() {
		s = "";
		j = "[";
		ready = new String[100];
		for (int i = 0; i < 100; i++) {
			ready[i] = "";
		}
		io = new String[3][100];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 100; j++) {
				io[i][j] = "";
			}
		}
		fullTime = 0;
	}
	
	public void markIO(int time, int io, int klt_id, int ult_id) {
		s += "I/O\nTime: " + time + "\nDevice: " + io + "\nKLT: " + klt_id + "\nULT: " + ult_id + "\n\n";
		j += ",{%22Run%22:%22IO%22,%22Time%22:" + time + ",%22Device%22:" + io + ",%22KLT%22:" + klt_id + ",%22ULT%22:" + ult_id + "}"; 
		fullTime = Math.max(fullTime, time);
	}

	public void markCPU(int time, int core_id, int klt_id, int ult_id) {
		s += "CPU\nTime: " + time + "\nCore: " + (core_id+1) + "\nKLT: " + klt_id + "\nULT: " + ult_id + "\n\n";
		if (j.length() > 1){
			j += ",";
		}
		j += "{%22Run%22:%22CPU%22,%22Time%22:" + time + ",%22Core%22:" + (core_id+1) + ",%22KLT%22:" + klt_id + ",%22ULT%22:" + ult_id + "}";
		fullTime = Math.max(fullTime, time);
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
		fullTime = Math.max(fullTime, time);
	}
	
	public void addReady(int time, int klt_id) {
		ready[time] += Integer.toString(klt_id) + " ";
		fullTime = Math.max(fullTime, time);
	}

	public void addIO(int time, int device, int klt_id) {
		io[device][time] += Integer.toString(klt_id) + " ";
		fullTime = Math.max(fullTime, time);
	}
	
}
