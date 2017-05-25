package backend;

import java.util.*;

public class KernelSchedulerRR implements KernelScheduler {

	private Queue<KLT> readyQueue;
	private Queue<KLT> commingFromIOQueue;
	private KLT[] cores;
	private int[] quantumsLeft;
	private List<KLT> notArrivedList;
	private Queue<KLT>[] ioQueues;
	private final int INITIAL_QUANTUMS;
	
	public KernelSchedulerRR() {
		INITIAL_QUANTUMS = 0;
	}
	
	@SuppressWarnings("unchecked")
	public KernelSchedulerRR(int numberOfCores, List<KLT> threadList, int initialQuantums) {
		
		INITIAL_QUANTUMS = initialQuantums;
		quantumsLeft = new int[numberOfCores];
		readyQueue = new LinkedList<>();
		cores = new KLT[numberOfCores];
		notArrivedList = new LinkedList<>();
		ioQueues = new LinkedList[3];
		for (int i = 0; i < ioQueues.length; i++) {
			ioQueues[i] = new LinkedList<KLT>();
		}
		Iterator<KLT> it = threadList.iterator();
		while (it.hasNext()) {
			KLT current = it.next();
			if (current.isArrived()) {
				readyQueue.add(current);
			} else {
				notArrivedList.add(current);
			}
		}
		commingFromIOQueue = new LinkedList<>();
	}
	
	public Gantt solve() {
		
		Gantt gantt = new Gantt();
		boolean done = false;
		
		for (int time = 0; !done; time++) {
			
			// Check CPU
			for (int i = 0; i < cores.length; i++) {
				if (cores[i] != null || !readyQueue.isEmpty()) {
					if (cores[i] == null) {
						cores[i] = readyQueue.poll();
						quantumsLeft[i] = INITIAL_QUANTUMS;
					}
				}
			}
			
			// Check and run I/O
			for (int i = 0; i < ioQueues.length; i++) {
				
				if (!ioQueues[i].isEmpty()) {

					KLT current = ioQueues[i].peek();
					current.markGanttIO(gantt, time, i);
					current.runIO(i);
					if (current.wantsCPU()) {
						commingFromIOQueue.add(ioQueues[i].poll());
					}
				}
			}
			
			// Run CPU
			for (int i = 0; i < cores.length; i++) {
				if (cores[i] != null) {
					cores[i].markGanttCPU(gantt, time, i);
					cores[i].runCPU(i);
					quantumsLeft[i]--;
					if (cores[i].isFinished()) {
						cores[i] = null;
					} else if (cores[i].wantsIO()) {
						ioQueues[cores[i].getIORequest()].add(cores[i]);
						cores[i] = null;
					} else if (quantumsLeft[i] == 0) {
						readyQueue.add(cores[i]);
						cores[i] = null;
					}
				} else {
					gantt.markSO(time, i);
				}
			}

			// Dump IO->ready
			while (!commingFromIOQueue.isEmpty()) {
				readyQueue.add(commingFromIOQueue.remove());
			}
			
			// Tell the active threads that a quantum has gone by
			for (int i = 0; i < cores.length; i++) {
				if (cores[i] != null) {
					cores[i].oneQuantumGoesBy();
				}
			}
			for (int i = 0; i < ioQueues.length; i++) {
				for (KLT k: ioQueues[i]) {
					k.oneQuantumGoesBy();
				}
			}
			for (KLT k: readyQueue) {
				k.oneQuantumGoesBy();
			}
			
			// Check not arrived
			Iterator<KLT> it = notArrivedList.iterator();
			while (it.hasNext()) {
				KLT current = it.next();
				current.oneQuantumGoesBy();
				if (current.isArrived()) {
					readyQueue.add(current);
					it.remove();
				}
			}
			
			// Check if we're done
			done = true;
			if (!notArrivedList.isEmpty() || !readyQueue.isEmpty()) {
				done = false;
			}
			for (int i = 0; done && i < cores.length; i++) {
				if (cores[i] != null) {
					done = false;
				}
			}
			for (int i = 0; done && i < ioQueues.length; i++) {
				if (!ioQueues[i].isEmpty()) {
					done = false;
				}
			}
		}
		
		return gantt;
	}
}
