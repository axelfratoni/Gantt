package backend;

public class KLT {

	private int id;
	private int arrivalTime;
	private UserScheduler us;
	
	public KLT() {
		
	}
	
	public KLT(int id, int arrivalTime, UserScheduler us) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.us = us;
	}
	
	public boolean isFinished() {
		return us.isFinished();
	}

	public boolean wantsCPU() {
		return arrivalTime == 0 && us.wantsCPU();
	}

	public void runCPU(int core) {
		if (arrivalTime != 0) {
			throw new RuntimeException("Running CPU for KLT that has not arrived");
		}
		us.runCPU(core);
	}

	public boolean wantsIO() {
		return arrivalTime == 0 && us.wantsIO();
	}

	public int getIORequest() {
		if (arrivalTime != 0) {
			throw new RuntimeException("Getting I/O request from KLT that has not arrived");
		} else if (!us.wantsIO()) {
			throw new RuntimeException("Getting I/O request from KLT that does not want I/O");
		}
		return us.getIORequest();
	}

	public void runIO(int i) {
		us.runIO(i);
	}

	public void oneQuantumGoesBy() {
		if (arrivalTime == 0) {
			us.oneQuantumGoesBy();
		} else {
			arrivalTime--;
		}
	}

	public void markGanttIO(Gantt gantt, int time, int io) {
		us.markGanttIO(gantt, time, io, id);
	}

	public void markGanttCPU(Gantt gantt, int time, int core_id) {
		us.markGanttCPU(gantt, time, core_id, id);
	}

	public boolean isArrived() {
		return arrivalTime == 0;
	}
	
	public int getID() {
		return id;
	}
	
}
