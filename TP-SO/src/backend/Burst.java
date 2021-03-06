package backend;

public class Burst {

	public BurstType type;
	public int time;

	public Burst(BurstType type, int time) {
		this.type = type;
		this.time = time;
	}
	
	public boolean isCPU() {
		return type == BurstType.CPU;
	}

	public boolean isIO() {
		return 0 <= type.number && type.number <= 2;
	}

	public int getRequestNumber() {
		return type.number;
	}

	public boolean isNotArrived() {
		return type == BurstType.DEAD;
	}

	public enum BurstType {
		IO_0(0), IO_1(1), IO_2(2), CPU(3), DEAD(4);
		private BurstType(int number) {
			this.number = number;
		}
		private int number;
	}
	
}
