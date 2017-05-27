package backend;

import java.util.*;

public class UserSchedulerSRT implements UserScheduler {

	PriorityQueue<ULT> readyQueue;
	List<ULT> notArrivedList;
	ULT running;
	
	public UserSchedulerSRT() {
		
	}
	
	public UserSchedulerSRT(List<ULT> threadList) {
		
		notArrivedList = new LinkedList<>();
		readyQueue = new PriorityQueue<>();
		
		Iterator<ULT> it = threadList.iterator();
		while (it.hasNext()) {
			ULT current = it.next();
			
			if (current.wantsCPU()) {
				readyQueue.add(current);
			} else if (current.wantsIO()) {
				throw new RuntimeException("ULT can't start with I/O");
			} else {
				notArrivedList.add(current);
			}
		}
		if (readyQueue.isEmpty()) {
			throw new RuntimeException("There has to be at least one ready ULT");
		}
		running = readyQueue.poll();
	}
	
	@Override
	public boolean isFinished() {
		return running == null && readyQueue.isEmpty() && notArrivedList.isEmpty();
	}

	@Override
	public boolean wantsCPU() {
		return running != null && running.wantsCPU();
	}

	@Override
	public void runCPU(int core) {
		running.runCPU(core);
		if (running.isFinished()) {
			running = null;
		}
	}

	@Override
	public boolean wantsIO() {
		return running != null && running.wantsIO();
	}

	@Override
	public int getIORequest() {
		return running.getIORequest();
	}

	@Override
	public void runIO(int io) {
		running.runIO(io);
	}

	@Override
	public void oneQuantumGoesBy() {
		Iterator<ULT> it = notArrivedList.iterator();
		while (it.hasNext()) {
			ULT current = it.next();
			current.oneQuantumGoesBy();
			if (current.wantsCPU()) {
				readyQueue.add(current);
				it.remove();
			}
		}
		if (running != null && running.wantsCPU()) {
			readyQueue.add(running);
			running = readyQueue.remove();
		} else if (running == null && !readyQueue.isEmpty()) {
			running = readyQueue.poll();
		}
	}

	@Override
	public void markGanttIO(Gantt gantt, int time, int io, int klt_id) {
		running.markGanttIO(gantt, time, io, klt_id);
	}

	@Override
	public void markGanttCPU(Gantt gantt, int time, int core_id, int klt_id) {
		running.markGanttCPU(gantt, time, core_id, klt_id);		
	}

}