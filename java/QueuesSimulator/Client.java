package QueuesSimulator;

import java.util.concurrent.atomic.AtomicInteger;

public class Client implements Comparable<Client>{
	
	private int id;
	private int arrivalTime;
	private AtomicInteger serviceTime;
	private volatile int totalWaitingTime;
	private volatile boolean processed;
	
	public Client(int id, int arrivalTime, int serviceTime) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.serviceTime = new AtomicInteger(serviceTime);
	}
	
	public String toString() {
		return "(" + id + "," + arrivalTime + "," + serviceTime + ")";
	}

	public int compareTo(Client o) {
		if((this.arrivalTime - o.arrivalTime) == 0)
			return o.serviceTime.get() - this.serviceTime.get();
		return this.arrivalTime - o.arrivalTime;
	}

	public int getId() {
		return id;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public int getServiceTime() {
		return serviceTime.get();
	}

	public void decrementServiceTime() {
		serviceTime.decrementAndGet();
	}

	public int getTotalWaitingTime() {
		return totalWaitingTime;
	}

	public void setTotalWaitingTime(int waitingTime) {
		this.totalWaitingTime = waitingTime;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
}

