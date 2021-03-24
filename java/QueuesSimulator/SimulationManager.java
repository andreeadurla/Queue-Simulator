package QueuesSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SimulationManager implements Runnable{

	private int numberOfClients;
	private int numberOfQueues;
	private int simulationTime;
	private int minArrivalTime;
	private int maxArrivalTime;
	private int minServiceTime;
	private int maxServiceTime;

	private ArrayList<Client> generatedClients = new ArrayList<Client>();
	private Scheduler scheduler;

	public static CyclicBarrier barrier; 
	private int currentTime;
	private boolean stop = false;
	private FileWriter writer;

	public SimulationManager(String inputFile, String outputFile) {
		File file = new File(inputFile);
		readData(file);
		openWriteFile(outputFile);
	}
	
	private void readData(File inputFile) {
		Scanner scanner;
		try {
			scanner = new Scanner(inputFile);
			numberOfClients = scanner.nextInt();
			numberOfQueues = scanner.nextInt();
			simulationTime = scanner.nextInt();
			scanner.nextLine();

			String line = scanner.nextLine();
			String[] arr = line.split(",", 2); 

			minArrivalTime = Integer.parseInt(arr[0]); 
			maxArrivalTime = Integer.parseInt(arr[1]);

			line = scanner.nextLine();
			arr = line.split(",", 2); 

			minServiceTime = Integer.parseInt(arr[0]); 
			maxServiceTime = Integer.parseInt(arr[1]);

			generateNRandomClients();
			scheduler = new Scheduler(numberOfQueues);

			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} 
	}

	private void generateNRandomClients() {
		Random rand = new Random();
		for(int i = 1; i <= numberOfClients; i++) {
			int arrivalTime = rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
			int serviceTime = rand.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
			generatedClients.add(new Client(i, arrivalTime, serviceTime));
		}

		Collections.sort(generatedClients);
	}

	private void openWriteFile(String outputFile) {
		try {
			writer = new FileWriter(outputFile);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void closeWriteFile() {
		try {
			writer.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void print() {
		try {
			writer.write(String.format("Time %d\n", currentTime));
			writer.write(String.format("%s", "Waiting clients: "));

			for(Client c : generatedClients)
				writer.write(String.format("%s; ", c.toString()));
			writer.write(System.lineSeparator());
			
			scheduler.printStatusQueues(writer);
			writer.write(System.lineSeparator());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void findClients(int currentTime) {
		int i = 0;
		while(i < numberOfClients) {
			Client c = generatedClients.get(i);

			if(c.getArrivalTime() == currentTime) {
				scheduler.sendClient(c);
				generatedClients.remove(c);
				numberOfClients--;
			}
			else
				i++;
		}
	}

	private void writeAverageWaitingTime() {
		try {
			writer.write(String.format("Average waiting time: %.2f", scheduler.averageWaitingTime()));
		} catch (IOException e) {
			e.getMessage();
		}
	}

	public void run() {
		barrier = new CyclicBarrier(numberOfQueues + 1, new Runnable() {
			public void run() {
				if(!generatedClients.isEmpty())
					findClients(currentTime);
				else
					if(!scheduler.queuesIsAlive()) {
						stop = true;
						scheduler.stopQueues();
					}
				if(currentTime == simulationTime) {
					stop = true;
					scheduler.stopQueues();
				}
				print();}});
		scheduler.startThreads();
		while(true) {
			try{ 
				SimulationManager.barrier.await(); 
			}  
			catch (InterruptedException | BrokenBarrierException e)  { 
				System.out.println(e.getMessage());
			}
			
			if(stop)
				break;
			
			currentTime++;
		}
		writeAverageWaitingTime();
		closeWriteFile();
	}

	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("Number of arguments is incorrect!");
		}
		else {
			SimulationManager sim = new SimulationManager(args[0], args[1]);
			Thread t = new Thread(sim);
			t.start();
		}
	}
}
