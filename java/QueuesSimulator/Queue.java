package QueuesSimulator;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Queue implements Runnable{

	private LinkedBlockingQueue<Client> clients;
	private AtomicInteger waitingTime;
	private boolean exit;
	private AtomicReference<String> status;

	public Queue() {
		clients = new LinkedBlockingQueue<Client>();
		waitingTime = new AtomicInteger(0);
		status = new AtomicReference<String>();
		exit = false;
	}

	public void addClient(Client client) {
		client.setTotalWaitingTime(client.getServiceTime() + waitingTime.get());
		clients.add(client);
		waitingTime.addAndGet(client.getServiceTime());
	}

	public void run() {
		status.set(Thread.currentThread().getName());
		while(!exit) {
			Client c = clients.peek();
			
			if(c != null) {
				waitingTime.decrementAndGet();
				c.decrementServiceTime();
				if(c.getServiceTime() == 0) {
					c.setProcessed(true);
					clients.poll();
				}
			}
			
			try{ 
				SimulationManager.barrier.await(); 
			}  
			catch (InterruptedException | BrokenBarrierException e)  { 
				System.out.println(e.getMessage());
			}
		}
	}

	public boolean isOpen() {
		if(clients.isEmpty())
			return false;
		return true;
	}

	public String toString() {
		if(clients.isEmpty())
			return status.get() + ": closed";

		String s = status.get() + ": ";
		for(Client c: clients)
			s += c.toString() + "; " ;
		return s;
	}

	public void stopQueue() {
		exit = true;
	}

	public int getWaitingTime() {
		return waitingTime.get();
	}
}
