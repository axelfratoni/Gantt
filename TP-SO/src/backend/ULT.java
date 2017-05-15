package backend;

import java.util.*;

public class ULT {

	private Queue<Burst> trace;	
	private int id;
	
	public ULT() {
		
	}
	
	public ULT(int id, Queue<Burst> trace) {
		this.id = id;
		this.trace = trace;
	}
	
	public boolean isFinished() {
		return trace.isEmpty();
	}

	public boolean wantsCPU() {
		return trace.peek().isCPU();
	}

	public void runCPU(int core) {
		Burst current = trace.peek();
		if (!current.isCPU()) {
			throw new RuntimeException("Running CPU in ULT that doesn't want CPU");
		}
		current.time--;
		if (current.time == 0) {
			trace.remove();
		}
	}

	public boolean wantsIO() {
		return trace.peek().isIO();
	}

	public int getIORequest() {
		Burst current = trace.peek();
		if (!current.isIO()) {
			throw new RuntimeException("Getting I/O request from ULT that doesn't want I/O");
		}
		return current.getRequestNumber();
	}

	public void runIO(int io) {
		Burst current = trace.peek();
		if (!current.isIO()) {
			throw new RuntimeException("Running I/O in ULT that doesn't want I/O");
		}
		if (current.getRequestNumber() != io) {
			throw new RuntimeException("Running incorrect I/O in ULT");
		}
		current.time--;
		if (current.time == 0) {
			trace.poll();
		}
	}

	public void oneQuantumGoesBy() {
		Burst current = trace.peek();
		if (!current.isNotArrived()) {
			throw new RuntimeException("Skipping quantum on arrived ULT: " + id);
		}
		current.time--;
		if (current.time == 0) {
			trace.remove();
		}
	}

	public void markGanttIO(Gantt gantt, int time, int io, int klt_id) {
		gantt.markIO(time, io, klt_id, id);
	}

	public void markGanttCPU(Gantt gantt, int time, int core_id, int klt_id) {
		gantt.markCPU(time, core_id, klt_id, id);
	}

}
