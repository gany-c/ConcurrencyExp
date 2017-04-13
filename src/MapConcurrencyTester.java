import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * If a map is an instance variable in a class, 
 *  a. one thread is iterating over it
 *  b. another changes the instance variable pointer to another map i.e. an atomic switch
 *  Will it cause a concurrent modification issue?
 *  
 *  Answer: NO
 * @author gchidam
 *
 */
class MapHolder{
	
	Map<Integer,String> sharedMap = null;
	AtomicInteger ai = null;
	
	public MapHolder(Map<Integer,String> inMap, AtomicInteger inAi){
		sharedMap = inMap;
		ai = inAi;
	}
}

class Reader implements Runnable {
	
	MapHolder mapHolder = null;	
	
	public Reader(MapHolder inHolder){
		mapHolder = inHolder;
	}

	@Override
	public void run() {
		
		for(int i = 0; i < 1000 ; i++){
			
			String startVal = null;
			 
			for(Entry<Integer,String> sharedEntry: mapHolder.sharedMap.entrySet()){
				
				System.out.print(sharedEntry.getKey()+":"+sharedEntry.getValue()+",");
				
				if(startVal == null)
					startVal = sharedEntry.getValue().substring(0, 1);
				else if(!startVal.equalsIgnoreCase(sharedEntry.getValue().substring(0, 1))){
					System.out.println("Concurrent Modification error");
					break;
				}
			}
			
			System.out.println();
			
		}
		
		mapHolder.ai.decrementAndGet();
		
	}
	
	
}

class Writer implements Runnable {
	
	MapHolder mapHolder = null;	
	List<Map<Integer,String>> mapList = null;
	
	public Writer(MapHolder inHolder, List<Map<Integer,String>> inList){
		mapHolder = inHolder;
		mapList = inList;
	}

	@Override
	public void run() {
	
		while(mapHolder.ai.get() >0){
			
			for(Map<Integer,String> map: mapList){
				mapHolder.sharedMap = map;
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}	

public class MapConcurrencyTester {
	
	public static void main(String[] args){

		Map<Integer,String> sharedMap = new HashMap<>();
		List<Map<Integer,String>> sharedList = new ArrayList<>();
		
		sharedMap.put(0, "A");
		sharedMap.put(1, "AA");
		sharedMap.put(2, "AAA"); 
		sharedMap.put(3, "AAAA"); 
		
		Map<Integer,String> sharedMap2 = new HashMap<>();
		sharedMap2.put(0, "B");
		sharedMap2.put(1, "BB");
		sharedMap2.put(2, "BBB"); 
		sharedMap2.put(3, "BBBB"); 		
		
		sharedList.add(sharedMap);
		sharedList.add(sharedMap2);
		
		MapHolder mapHolder = new MapHolder(sharedMap, new AtomicInteger(10));		
		
		Writer writer = new Writer(mapHolder, sharedList);
		ThreadPoolExecutor writeExec = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		writeExec.execute(writer);
		
		Reader reader = new Reader(mapHolder);
		ThreadPoolExecutor readExec = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		readExec.execute(reader);
	}
	


}
