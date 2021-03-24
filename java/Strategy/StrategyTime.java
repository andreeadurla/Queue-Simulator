package Strategy;
import java.util.ArrayList;

import QueuesSimulator.Client;
import QueuesSimulator.Queue;

public class StrategyTime implements Strategy{

	public void addClient(ArrayList<Queue> queues, Client c) {
		int minTime = queues.get(0).getWaitingTime();
		int indexQ = 0;
		
		int i = 0;
		for(Queue q: queues) {
			int time = q.getWaitingTime();
			if(time < minTime) {
				minTime = time;
				indexQ = i;
			}
			i++;
		}
		
		queues.get(indexQ).addClient(c);
	}

}