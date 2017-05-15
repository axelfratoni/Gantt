package backend;

public class Gantt {

	private String s;
	
	public Gantt() {
		s = "";
	}
	
	public void markIO(int time, int io, int klt_id, int ult_id) {
		s += "I/O\nTime: " + time + "\nDevice: " + io + "\nKLT: " + klt_id + "\nULT: " + ult_id + "\n\n";
	}

	public void markCPU(int time, int core_id, int klt_id, int ult_id) {
		s += "CPU\nTime: " + time + "\nCore: " + (core_id+1) + "\nKLT: " + klt_id + "\nULT: " + ult_id + "\n\n";
	}
	
	@Override
	public String toString() {
		return s;
	}

}
