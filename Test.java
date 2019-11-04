/*
 * This class contains static methods that run the simulation by
 * creating a set of threads.
 */
public class Test {

	public static void main(String[] args) {
		testMLFQ();
	}
	
    /*
     * Test three threads with I/O burst times of 4 and CPU burst times of 4, 8 and 5.
     */
    public static void test3()
    {
        OS os = new OS();
        // createSimThread(cpuburst, ioburst, priority, cycles)
        os.createSimThread(4, 4, 0, 3);
        os.createSimThread(8, 4, 0, 3);
        os.createSimThread(5, 4, 0, 3);
    }
    
    public static void testMLFQ() {
    	OS os = new OS();
    	for(int i=0; i<4; i++) {
    		os.createSimThread(30, 50, 0, 10);
    	}
    	for(int i=0; i<6; i++) {
    		os.createSimThread(4);
    	}
    }
    
    /*
     * Test ten threads with random CPU burst and I/O burst times.
     */
    public static void test10()
    {
        OS os = new OS();
        for(int i=0; i<5; i++) {
            os.createSimThread(0);
        }
        for(int i=0; i<5; i++) {
        	os.createSimThread(1);
        }
    }
}