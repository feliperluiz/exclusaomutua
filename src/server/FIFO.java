package server;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class FIFO {
	private Queue<Semaphore> queue = new LinkedList<Semaphore>();
	private Semaphore mutex = new Semaphore(1);
	
	public void add(Semaphore s) throws InterruptedException{
		mutex.acquire();
		queue.add(s);
		mutex.release(); //immediatly release mutex to allow other waiting threads to add their Semaphore to queue.
		s.acquire();
	}
	
	public void remove() throws InterruptedException{
		mutex.acquire();
		Semaphore s = queue.remove();
		mutex.release();
		s.release();
	}
	
	public void remove(int num){
		Semaphore s = null;
		for(int i = 0; i < num; i++){
			s = queue.poll();
			s.release();
		}
	}
	
	public int size() throws InterruptedException{
		mutex.acquire();
		int s = queue.size();
		mutex.release();
		return s; 
	}
}
