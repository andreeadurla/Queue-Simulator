package QueuesSimulator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import Strategy.*;

public class Scheduler {
	
	private ArrayList<Queue> queues = new ArrayList<Queue>();
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	private ArrayList<Client> distributedClients = new ArrayList<Client>();
	private Strategy strategy = new StrategyTime();
	
	public Scheduler(int nrQueues) {
		for(int i = 1; i <= nrQueues; i++) {
			Queue auxQ = new Queue();
			queues.add(auxQ);
			threads.add(new Thread(auxQ, "Queue " + i));
		}
	}
	
	public void startThreads() {
		for(Thread t: threads)
			t.start();
	}
	
	public void stopQueues() {
		for(Queue q: queues)
			q.stopQueue();
	}
	
	public void sendClient(Client c) {
		strategy.addClient(queues, c);
		distributedClients.add(c);
	}
	
	public boolean queuesIsAlive() {
		for(Queue q: queues)
			if(q.isOpen() == true)
				return true;
		return false;
	}
	
	public void printStatusQueues(FileWriter writer) throws IOException {
		for(Queue q: queues)
			writer.write(String.format("%s\n", q.toString()));
	}
	
	public double averageWaitingTime() {
		int waitingTime = 0;
		int nrProcessedClients = 0;
		for(Client c: distributedClients) {
			if(c.isProcessed() == true) {
				waitingTime += c.getTotalWaitingTime();
				nrProcessedClients++;
			}
		}
		return waitingTime * 1.0 / nrProcessedClients;
	}
}
