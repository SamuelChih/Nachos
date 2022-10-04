package nachos.threads;

public class KThreadTimer implements Comparable<Long>{
	private KThread currentThread;
	private long time = 0;
	public KThreadTimer(KThread thread, long waitTime){
		this.currentThread=thread;
		this.time=waitTime;
	}
	public KThread getThread(){
		return currentThread;
	}
	public long getTime(){
		return time;
	}
	// @Override
	// public int compareTo(KThreadTimer o) {
	// 	if (o.time < this.time) {
	// 		return 1;		
	// 	} else if (o.time == this.time) {
	// 		return 0;
	// 	} else {
	// 		return -1;
	// 	}
	// }
	@Override
	public int compareTo(Long o) {
		if (o < this.time) {
			return 1;		
		} else if (o == this.time) {
			return 0;
		} else {
			return -1;
		}
	}
}