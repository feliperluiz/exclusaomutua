package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is a remote interface to access processes in distributed environment using Java Remote Method Invocation (RMI).
 */
public interface Process extends Remote {
	
	/**
	 * This method tells if the process is alive and can participate in leader election.
	 * @return
	 * @throws RemoteException
	 */
	public boolean isAlive() throws RemoteException;
	
	/**
	 * This method tells if the process in a leader in distributed environment.
	 * @return
	 * @throws RemoteException
	 */
	public boolean isLeader() throws RemoteException;
	
	/**
	 * This method gets the logical clock value (random) for this process.
	 * @return
	 * @throws RemoteException
	 */
	public int getLogicalClock() throws RemoteException;
	
	/**
	 * This method gets the process name, given during initialization.
	 * @return
	 * @throws RemoteException
	 */
	public String getProcessName() throws RemoteException;
	
	/**
	 * This method gets called in election to compare the logical clock values.
	 * @param message
	 * @return
	 * @throws RemoteException
	 */
	public boolean inquiry(Message message) throws RemoteException;
	
	/**
	 * This method is called to announce victory after the election.
	 * @param message
	 * @return
	 * @throws RemoteException
	 */
	public boolean victory(Message message) throws RemoteException;

}