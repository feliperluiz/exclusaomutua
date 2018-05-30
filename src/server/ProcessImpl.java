package server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 * This is an implementation class for the processes in a distributed environment using Java RMI.
 */
public class ProcessImpl extends UnicastRemoteObject implements Process {

	// Constants to use in program.
	private static final long serialVersionUID = 1L;
	private static final int PORT = 1099;
	private static final String URI = "rmi://localhost:"+ PORT +"/";
	private static final String SIMULATOR = "Simulator";
	private static final java.util.Random RANDOM = new java.util.Random();
	
	/**
	 * Boolean flag to maintain the process status.
	 */
	private static boolean ALIVE = true;
	
	/**
	 * Boolean flag to determine the leader process.
	 */
	private static boolean LEADER = false;
	
	/**
	 * Randomly generated logical clock value.
	 */
	private static int LOGICAL_CLOCK = RANDOM.nextInt(1000);
	
	/**
	 * Unique process name within a distributed environment.
	 */
	private static String PROCESS_NAME;
	
	/**
	 * Constructor to initialize a process with a unique name.
	 * @param pName
	 * @throws RemoteException
	 */
	public ProcessImpl(String pName) throws RemoteException {
		super();
		PROCESS_NAME = pName;
	}

	@Override
	public boolean isAlive() throws RemoteException {
		return ALIVE;
	}
	
	@Override
	public boolean isLeader() throws RemoteException {
		return LEADER;
	}

	@Override
	public int getLogicalClock() throws RemoteException {
		return LOGICAL_CLOCK;
	}

	@Override
	public String getProcessName() throws RemoteException {
		return PROCESS_NAME;
	}

	@Override
	public boolean inquiry(Message message) throws RemoteException {
		return LOGICAL_CLOCK < message.getLogicalClock() ? true : false;
	}

	@Override
	public boolean victory(Message message) throws RemoteException {
		if(LOGICAL_CLOCK < message.getLogicalClock()) {
			LEADER = false; /* respect other leader and step down */
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Main method to start the simulation.
	 * Usage:
	 * 		To start the simulator: java -jar leader.jar
	 * 		To start the process:   java -jar leader.jar P1
	 * @param args
	 */
	public static void main(String args[]) {
		
		String processName = args.length == 0 ? SIMULATOR : args[0];
		
		// Create RMI registry to bind processes on the given port.
		try {
            LocateRegistry.createRegistry(PORT);
            System.out.println("Leader election simulator started, terminating this process will also end the simulation.");
        } catch (Exception e) { /* RMI registry already created */ }
		
		// Bind this process server to the RMI registry with the given uri to keep running.
		try {
			Naming.rebind(URI + processName, new ProcessImpl(processName));
		} catch (Exception e) {
			System.out.println("Error while binding the process [" + processName + "] to the RMI registry.");
		}
		
		// Different console for simulator and process for validating the algorithm.
		if(processName.compareToIgnoreCase(SIMULATOR) == 0) {
			System.out.println("*This process will not participate in leader election and will just monitor the simulation.");
			System.out.println("Please start individual processes in new consoles. \nUsage: java -jar leader.jar P1 \t(where p1 is a unique process name)");
			ALIVE = false; /* do not participate in leader election */
			while(true) { /* Monitor simulation till it gets terminated */
				monitorSimulation();
			}
			
		} else {
			System.out.print("Process [" + processName + "] started ");
			// Monitor this process till it is alive or auto healed */
			monitorProcess();
			System.out.print("to participate in leader election.\n");
		}
	}

	/**
	 * This method monitors the process to participate the leader election.
	 * @param processName
	 */
	private static void monitorProcess() {
		Thread monitorThread = new Thread() {
			public void run() {
				while(true) {
			    	try { /* Add some random delay to slow down the process */
						Thread.sleep(RANDOM.nextInt(3000) + 2000);
			    	} catch (Exception e) { /* do nothing */ }
			    	
			    	if(!LEADER && ALIVE) {
				    	// Check if there is another leader process.
				    	boolean isAnyLeader = false;
						for (int i = 0; i < getProcessCount(); i++) {
							try {
					            Process process = getProcess(Naming.list(URI)[i]);
					            if(process.isAlive() && process.isLeader()) {
					            	isAnyLeader = true;
					            	System.out.println("\nLeader process in distributed system is: " + process.getProcessName());
					            }
							} catch (Exception e) { /* do nothing */ }
						}
				    	
				    	// if there is no leader, ask for election
				    	if(!isAnyLeader) {
							election();
				    	}
			    	}
			    	
			    	// Purposefully introduce an error randomly ( for unlucky 13 !! ) for simulation, reset all values and then let auto heal the process.
			    	if(ALIVE && RANDOM.nextInt(1000) % 13 == 0){
			    		System.out.println("\nProcess [" + PROCESS_NAME + "] encountered an error and became unresponsive now.");
			    		System.out.println("You may wait to get this auto healed or terminate the process by pressing 'Ctrl + C'");
			    		ALIVE = false;
			    		LEADER = false;
			    		try {
							Thread.sleep(5000);
							ALIVE = true;
							LOGICAL_CLOCK = RANDOM.nextInt(1000);
						} catch (Exception e) { /* do nothing*/ }
			    	}
			    	
			    	// Monitor and terminate the process if RMI registry gets unavailable.
					try {
						Naming.lookup(URI + PROCESS_NAME);
					} catch (Exception e) {
						System.out.println("\nLeader election simulator stopped, terminating this process.");
						System.exit(0);
					}
				}
			};
		};
		monitorThread.start();
	}

	/**
	 * This method gets the overall process count in RMI registry.
	 * @return
	 */
	private static int getProcessCount() {
		int processCount = 0;
		try { 
    		processCount = Naming.list(URI).length;
    	} catch (Exception e) { /* do nothing */ }
		return processCount;
	}
	
	/**
	 * This method gets a process of given name within distributed environment.
	 * @param processUri
	 * @return
	 */
	private static Process getProcess(String processUri) {
		String pName = processUri.substring(processUri.lastIndexOf("/") + 1);
        try {
			return (Process)Naming.lookup(URI + pName);
		} catch (Exception e) {
			return null;
		} 
	}

	/**
	 * This method perform the election to choose a leader.
	 * Step 1: Sends inquiry messages to all other N-1 processes, terminates on any -ve response.
	 * Step 2: Announces victory to all other N-1 processes, asks for re-election for any contradiction.
	 */
	private static void election() {
		System.out.println("\nNo leader elected in distributed system, calling inquiry.");
		boolean inquerySuccess = true;
		int processCount = getProcessCount();
		for (int i = 0; i < processCount; i++) {
			try {
				Process process = getProcess(Naming.list(URI)[i]);
				if(process.getProcessName().equalsIgnoreCase(PROCESS_NAME)) { /* ignore self inquiry */
					continue;
				}
	            if(process.isAlive()){
	            	if(!process.inquiry(new Message(PROCESS_NAME, LOGICAL_CLOCK))) { /* another process may be the leader */ 
	            		inquerySuccess = false;
	            		break;
	            	}
	            }
			} catch (Exception e) { /* do nothing */ }
		}
	
    	// if still there is no leader, seems I am the Leader !!
    	if(inquerySuccess) {
    		System.out.println("\nProcess [" + PROCESS_NAME+ "] seems to be a leader, announcing victory.");
    		// Sending the victory message to all other processes
    		boolean isLeader = true;
    		for (int i = 0; i < processCount; i++) {
    			try {
    				Process process = getProcess(Naming.list(URI)[i]);
    				if(process.getProcessName().equalsIgnoreCase(PROCESS_NAME)) { /* ignore self victory */
						continue;
					}
		            if(process.isAlive()){
		            	if(!process.victory(new Message(PROCESS_NAME, LOGICAL_CLOCK))) {
		            		isLeader = false; /* re-election */
		            		System.out.println("Victory countered by process [" + process.getProcessName() + "], re-election.");
		            	}
		            }
    			} catch (Exception e) { /* do nothing */ }
    		}
    		if(isLeader) {
    			LEADER = true;
    			System.out.println("Process [" + PROCESS_NAME+ "] is elacted as a leader: " + new Date());
    		} else {
    			election();
    		}
    	}
	}

	/**
	 * This method monitors the simulation and provides details of every participating processes.
	 */
	private static void monitorSimulation() {
		
		try { /* Monitor the simulation periodically */
			Thread.sleep(100);
    	} catch (Exception e) { /* do nothing */ }
		
		int processCount = getProcessCount();			    	
    	if(processCount == 1) {
    		return;
    	}
    	
		System.out.println("\nTotal number of candidate processes in leader election: " + (processCount - 1));
		String pName = "";
		for (int i = 0; i < processCount; i++) {
    		try {
    			Process process = getProcess(Naming.list(URI)[i]);
    			pName = Naming.list(URI)[i].substring(Naming.list(URI)[i].lastIndexOf("/") + 1);
    			if(SIMULATOR.equalsIgnoreCase(pName)) {
    				continue;
    			}
	            System.out.println("Process ["+ pName + "] is up with clock value (" + process.getLogicalClock() + ") and status: " + (process.isAlive() ? (process.isLeader() ? "Leader" : "Alive") : "Unresponsive"));
    		} catch (Exception e) {
    			System.out.println("Process ["+ pName + "] is down and disconnected, restart it to participate again.");;
			}
		}
	}
}