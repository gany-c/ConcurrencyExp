// "static void main" must be defined in a public class.
public class ReadWriteLock {
    
    private Map<String, String> someMap = new HashMap<>();
    private int readCount = 0;
    private int writeCount = 0;
    
    private  String businessRead(String key){
        return someMap.get(key);
    }
    
    private void businessWrite(String key, String value){
        someMap.put(key, value);
    }
    
    //multiple threads should be allowed to read concurrrently
    // as long as no thread is writing - should wait for write to complete
    public String read(String key){
        
        synchronized(this){
           this.readCount++;
        }

        //unprotected section, so the write method relies on the count
        String output = this.businessRead(key);
        
        // The increments and decrements have to be enclosed in the synchronized block.
        // They are actually 3 steps - Read from Memory, increment, write back to Mem
        // This is why AtomicIntegers have been introduced.
        synchronized(this){
            this.readCount--;
        }
        
        return output;
    }
    
    // write should have exclusive access - wait for all reads to complete
    public synchronized void write(String key, String value){     
           
            while(readCount > 0){
                Thread.sleep(1000);                
            }            
            this.businessWrite(key, value);
        
    }
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
        ReadWriteLock myLock = new ReadWriteLock();
        myLock.businessWrite("Ganapathy", "Seattle");
        myLock.businessRead("Ganapathy");
    }
}
