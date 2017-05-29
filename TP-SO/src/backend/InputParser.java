package backend;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;

import backend.Burst.BurstType;
import frontend.Test;

public class InputParser {
	private static String path = "src/Pruebas/2 - RR3 - HRRN";
	private static int MAX_KLT = 3;
	private static int MAX_ULT = 3;
	private static int MAX_CORES = 2;
	private static int MAX_PROCESSES = 10;
	private static int MAX_BURST_SIZE = 5;
	private static int MIN_BURST_SIZE = 1;
	private static int MAX_IO_SIZE = 5;
	private static int MIN_IO_SIZE = 1;
	private static int MAX_IO_TYPE = 2;
	private static int MAX_QUANTUM_SIZE = 5;
	private static int MIN_QUANTUM_SIZE = 1;
	
	public static KernelScheduler build() throws IOException {
		Path filePath = Paths.get(path);
		Scanner scanner = new Scanner(filePath);
		String threadsCount = "[";
		int amountOfProcesses = 0;
		int amountOfKernelThreads = Integer.parseInt(scanner.nextLine());
		if(!validAmountOfKernelThreads(amountOfKernelThreads)){
			throw new RuntimeException("Cantidad invalida de kernel threads. 0 <= KLTs <= " + MAX_KLT );
		}
		String kernelScheduler = scanner.nextLine();
		if(!validKernelScheduler(kernelScheduler)){
			throw new RuntimeException("Kernel scheduler invalido. Opciones validas: \"FIFO\" , \"RR (num)\"");
		}
		int amountOfCores = Integer.parseInt(scanner.nextLine());
		if(!validAmountOfCores(amountOfCores)){
			throw new RuntimeException("Cantidad de cores invalida. 1 <= Cores <= " + MAX_CORES);
		}
		List<KLT> kernelThreads = new LinkedList<>();
		for(int i = 1; i <= amountOfKernelThreads ; i++){
			if(!scanner.nextLine().equals("KLT")){
				throw new RuntimeException("Debes indicar que empieza el KLT escribiendo \"KLT\"");
			}
			int kernelThreadArrivalTime = Integer.parseInt(scanner.nextLine());
			if(kernelThreadArrivalTime < 0){
				throw new RuntimeException("El tiempo de llegada del KLT " + i + " debe ser mayor a cero.");
			}
			List<ULT> userThreads = new LinkedList<>();
			int amountOfUserThreads= Integer.parseInt(scanner.nextLine());
			amountOfProcesses += amountOfUserThreads;
			if(amountOfProcesses > MAX_PROCESSES){
				throw new RuntimeException("Cantidad de procesos invalida. 1 <= Procesos <= " + MAX_PROCESSES);
			}
			threadsCount += "{%22ULT%22:"+ amountOfUserThreads + "}";
			if( i != amountOfKernelThreads){
				threadsCount += ",";
			}else{
				threadsCount += "]";
			}
			if(!validAmountOfUserThreads(amountOfUserThreads)){
				throw new RuntimeException("Cantidad invalida de user threads. 0 <= ULTs <= " + MAX_ULT);
			}
			String userScheduler = scanner.nextLine(); 
			if(!isAValidUserScheduler(userScheduler)){
				throw new RuntimeException("User scheduler del KLT " + i + ". Opciones validas: \"FIFO\", \"HRRN\", \"SPN\", \"SRT\", \"RR (num)\"");
			}
			for(int j = 1; j <= amountOfUserThreads; j++){
				if(!scanner.nextLine().equals("ULT")){
					throw new RuntimeException("Debes indicar que empieza el ULT escribiendo \"ULT\"");
				}
				Queue<Burst> trace = new LinkedList<>();
				int arrivalTime = Integer.parseInt(scanner.nextLine());
				if(arrivalTime < 0){
					throw new RuntimeException("El tiempo de llegada del ULT " + j + " del KLT " + i + "debe ser mayor o igual a cero." );
				}
				if(arrivalTime > 0){
					trace.add(new Burst(Burst.BurstType.DEAD, arrivalTime));
				}
				int cpu1Time= Integer.parseInt(scanner.nextLine());
				if(!validBurstSize(cpu1Time)){
					throw new RuntimeException("Tiempo de la primera rafaga de CPU del ULT " + j + " del KLT " + i + " invalido. " + MIN_BURST_SIZE + " <= Tama�o <= " + MAX_BURST_SIZE);
				}
				trace.add(new Burst(Burst.BurstType.CPU, cpu1Time));
				StringTokenizer IO = new StringTokenizer(scanner.nextLine());
				int ioTime = Integer.parseInt(IO.nextToken());
				int ioType = Integer.parseInt(IO.nextToken());
				System.out.println(ioTime + " " + ioType);
				if(IO.hasMoreTokens()){
					System.out.println("XDDDDDDD");
				}
				if(IO.hasMoreTokens() || !validIOSize(ioTime) || !isValidIOType(ioType)){
					throw new RuntimeException("Peticion de IO del ULT " + j + " del KLT " + i + " invalido. Forma correcta: \"Tama�o_de_rafaga Tipo_de_io\". " + MIN_IO_SIZE + " <= Tama�o_de_rafaga <= " + MAX_IO_SIZE + " , 0 <= Tipo_de_io <= " + MAX_IO_TYPE);
				}
				trace.add(new Burst(getBurstType(ioType), ioTime));
				int cpu2Time = Integer.parseInt(scanner.nextLine());
				if(!validBurstSize(cpu2Time)){
					throw new RuntimeException("Tiempo de la segunda rafaga de CPU del ULT " + j + " del KLT " + i + " invalido. " + MIN_BURST_SIZE + " <= Tama�o <= " + MAX_BURST_SIZE);
				}
				trace.add(new Burst(Burst.BurstType.CPU, cpu2Time));
				ULT ult = new ULT(j, trace);
				userThreads.add(ult);
			}
			UserScheduler us = getUserSchedulerType(userScheduler, userThreads);
			KLT klt = new KLT(i, kernelThreadArrivalTime, us);
			kernelThreads.add(klt);

		}
		if(scanner.hasNextLine()){
			throw new RuntimeException("El arhivo sigue mas alla de lo que deberia ser el final.");
		}
		scanner.close();
		Test.threadsCount = threadsCount;
		return getKernelSchedulerType(amountOfCores, kernelScheduler, kernelThreads);
	}
	
	private static KernelScheduler getKernelSchedulerType(int amountOfCores, String kernelScheduler,
			List<KLT> kernelThreads) {
		if(kernelScheduler.equals("FIFO")){
			return new KernelSchedulerFIFO(amountOfCores, kernelThreads);
		}else{
			StringTokenizer st = new StringTokenizer(kernelScheduler);
			st.nextToken();
			int initialQuantums = Integer.parseInt(st.nextToken());
			return new KernelSchedulerRR(amountOfCores, kernelThreads, initialQuantums);
		}
	}

	private static boolean validKernelScheduler(String kernelScheduler){
		if(kernelScheduler.equals("FIFO")){
			return true;
		}else{
			StringTokenizer st = new StringTokenizer(kernelScheduler);
			if(st.nextToken().equals("RR")){
				int initial_quantums = Integer.parseInt(st.nextToken());
				if(!st.hasMoreTokens()){
					if(validAmountOfQuantums(initial_quantums)){
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
	}
	private static UserScheduler getUserSchedulerType(String userScheduler, List<ULT> userThreads) {
		if(userScheduler.equals("FIFO")){
			return new UserSchedulerFIFO(userThreads);
		}else if(userScheduler.equals("HRRN")){
			return new UserSchedulerHRRN(userThreads);
		}else if(userScheduler.equals("SPN")){
			return new UserSchedulerSPN(userThreads);
		}else if(userScheduler.equals("SRT")){
			return new UserSchedulerSRT(userThreads);
		}
		StringTokenizer st = new StringTokenizer(userScheduler);
		if(st.nextToken().equals("RR")){
			int initial_quantums = Integer.parseInt(st.nextToken());
			if(!st.hasMoreTokens() && validAmountOfQuantums(initial_quantums)){
				return new UserSchedulerRR(initial_quantums, userThreads);
			}
		}
		throw new RuntimeException("Tipo de scheduler invalido.");
	}

	private static boolean isAValidUserScheduler(String userScheduler) {
		boolean isValid = userScheduler.equals("FIFO") || userScheduler.equals("HRRN") || userScheduler.equals("SPN") || userScheduler.equals("SRT");
		if(!isValid){
			StringTokenizer st = new StringTokenizer(userScheduler);
			if(st.nextToken().equals("RR")){
				int initial_quantums = Integer.parseInt(st.nextToken());
				if(!st.hasMoreTokens()){
					if(validAmountOfQuantums(initial_quantums)){
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		return isValid;
	}

	private static BurstType getBurstType(int ioType) {
		switch(ioType){
			case 0:
				return BurstType.IO_0;
			case 1:
				return BurstType.IO_1;
			case 2:
				return BurstType.IO_2;
		}
		throw new RuntimeException("Burst type invalido.");
	}
	
	private static boolean isValidIOType(int ioType) {
		return 0 <= ioType && ioType <= MAX_IO_TYPE;
	}
	
	private static boolean validIOSize(int ioTime) {
		return MIN_IO_SIZE <= ioTime && ioTime <= MAX_IO_SIZE;
	}
	
	private static boolean validBurstSize(int burstSize) {
		return MIN_BURST_SIZE <= burstSize && burstSize <= MAX_BURST_SIZE;
	}
	
	private static boolean validAmountOfUserThreads(int amountOfUserThreads) {
		return 0 <= amountOfUserThreads && amountOfUserThreads <= MAX_ULT;
	}
	
	private static boolean validAmountOfCores(int amountOfCores) {
		return 0 <= amountOfCores && amountOfCores <= MAX_CORES;
	}
	
	private static boolean validAmountOfKernelThreads(int amountOfKernelThreads) {
		return 0 <= amountOfKernelThreads && amountOfKernelThreads <= MAX_KLT;
	}
	
	private static boolean validAmountOfQuantums(int initial_quantums) {
		return MIN_QUANTUM_SIZE <= initial_quantums && initial_quantums <= MAX_QUANTUM_SIZE;
	}
}
