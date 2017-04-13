import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 * @author gchidam
 * 
 * How much time does it take for the readWriteLock methods to lock and unlock?
 * 0.004 milliseconds
 *
 */
class Readable implements Runnable {

	Map<Integer,String> readMap = null;
	ReentrantReadWriteLock rwl = null;
	
	public Readable(Map<Integer,String> inMap, ReentrantReadWriteLock inLock){
		readMap = inMap;
		rwl = inLock; 
	}
	
	@Override
	public void run() {
		Random rn = new Random();
		for(int i = 0; i < 10 ; i++){
			
			int key = rn.nextInt(5);
			long startTime = System.nanoTime();
			//rwl.readLock().lock();
			String value = readMap.get(key);
			//rwl.readLock().unlock();
			long stopTime = System.nanoTime();
			System.out.println(/*"Time taken to retrieve value = "+value+", ="+*/(stopTime - startTime));
		}
		
	}
	
}


public class RWLockTimeTester {
	
	public static void main(String[] args){
		
		Map<Integer,String> readMap = new HashMap<>();
		
		readMap.put(1, "A");
		readMap.put(2, "B");
		readMap.put(3, "C");
		readMap.put(4, "D");
		readMap.put(0, "E");
		
		
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		
		for(int i = 0; i < 100 ; i++){
			Readable r = new Readable(readMap, rwl);
			executor.execute(r);
		}
		
		
		
	}

}
