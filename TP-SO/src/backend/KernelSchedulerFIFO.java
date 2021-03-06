package backend;

import java.util.*;

public class KernelSchedulerFIFO implements KernelScheduler {

	private Queue<KLT> readyQueue;
	private KLT[] cores;
	private List<KLT> notArrivedList;
	private Queue<KLT>[] ioQueues;
	
	public KernelSchedulerFIFO() {
		
	}
	
	@SuppressWarnings("unchecked")
	public KernelSchedulerFIFO(int numberOfCores, List<KLT> threadList) {
		
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
						readyQueue.add(ioQueues[i].poll());
					}
				}
			}
			
			// Run CPU
			for (int i = 0; i < cores.length; i++) {
				if (cores[i] != null) {
					cores[i].markGanttCPU(gantt, time, i);
					cores[i].runCPU(i);
					if (cores[i].isFinished()) {
						cores[i] = null;
					} else if (cores[i].wantsIO()) {
						ioQueues[cores[i].getIORequest()].add(cores[i]);
						cores[i] = null;
					}
				} else {
					gantt.markSO(time, i);
				}
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
			
			// Dump ready queue
			Queue<KLT> auxQueue = new LinkedList<>();
			while(!readyQueue.isEmpty()) {
				KLT k = readyQueue.remove();
				gantt.addReady(time, k.getID());
				auxQueue.add(k);
			}
			while(!auxQueue.isEmpty()) {
				readyQueue.add(auxQueue.remove());
			}
			
			// Dump IO queues
			for (int i = 0; i < 3; i++) {
				while(!ioQueues[i].isEmpty()) {
					KLT k = ioQueues[i].remove();
					gantt.addIO(time, i, k.getID());
					auxQueue.add(k);
				}
				while(!auxQueue.isEmpty()) {
					ioQueues[i].add(auxQueue.remove());
				}
			}
			
		}
		
		return gantt;
	}
}
