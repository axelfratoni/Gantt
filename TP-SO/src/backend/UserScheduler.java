package backend;

public interface UserScheduler {

	public boolean isFinished();

	public boolean wantsCPU();

	public void runCPU(int core);

	public boolean wantsIO();

	public int getIORequest();

	public void runIO(int io);

	public void oneQuantumGoesBy();

	public void markGanttIO(Gantt gantt, int time, int io, int klt_id);

	public void markGanttCPU(Gantt gantt, int time, int core_id, int klt_id);
}
