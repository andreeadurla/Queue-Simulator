package Strategy;
import java.util.ArrayList;

import QueuesSimulator.Client;
import QueuesSimulator.Queue;

public interface Strategy {
	
	public void addClient(ArrayList<Queue> queues, Client c);
	
}