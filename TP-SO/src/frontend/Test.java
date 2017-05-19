package frontend;

import backend.*;
import java.util.*;

public class Test {

	public static void main(String[] args) {
		
		Queue<Burst> trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.CPU, 3));
		trace.add(new Burst(Burst.BurstType.IO_1, 2));
		trace.add(new Burst(Burst.BurstType.CPU, 1));
		ULT ult1 = new ULT(1, trace);
		trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.DEAD, 1));
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		trace.add(new Burst(Burst.BurstType.IO_2, 3));
		trace.add(new Burst(Burst.BurstType.CPU, 4));
		ULT ult2 = new ULT(2, trace);
		List<ULT> userThreads = new LinkedList<>();
		userThreads.add(ult1);
		userThreads.add(ult2);
		UserScheduler us1 = new UserSchedulerSPN(userThreads);
		KLT klt1 = new KLT(1, 0, us1);

		trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.DEAD, 2));
		trace.add(new Burst(Burst.BurstType.CPU, 4));
		trace.add(new Burst(Burst.BurstType.IO_1, 1));
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		ult1 = new ULT(1, trace);
		trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		trace.add(new Burst(Burst.BurstType.IO_2, 2));
		trace.add(new Burst(Burst.BurstType.CPU, 1));
		ult2 = new ULT(2, trace);
		userThreads = new LinkedList<>();
		userThreads.add(ult1);
		userThreads.add(ult2);
		us1 = new UserSchedulerSPN(userThreads);
		KLT klt2 = new KLT(2, 2, us1);
		
		trace = new LinkedList<>();
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		trace.add(new Burst(Burst.BurstType.IO_0, 2));
		trace.add(new Burst(Burst.BurstType.CPU, 2));
		ult1 = new ULT(1, trace);
		userThreads = new LinkedList<>();
		userThreads.add(ult1);
		us1 = new UserSchedulerSPN(userThreads);
		KLT klt3 = new KLT(3, 1, us1);
		
		List<KLT> kernelThreads = new LinkedList<>();
		kernelThreads.add(klt1);
		kernelThreads.add(klt2);
		kernelThreads.add(klt3);
		
		KernelScheduler ks = new KernelSchedulerFIFO(2, kernelThreads);
		
		Gantt gantt = ks.solve();
		
		System.out.println(gantt);
	}

}
