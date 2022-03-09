import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Node
{
    private static int count = 0;

    // Each node will store a costs vector, its own distance vector
    // and a distance vector for each of its neighbors
    private int[] neighbors;
    private int[] cost = new int[DVRoute.NUMNODES];
    private int[] myDV = new int[DVRoute.NUMNODES];
    private int[][] neighborDV = new int[DVRoute.NUMNODES][DVRoute.NUMNODES];
    private int id;
    // fwd table specifies for reaching each destination (index) from current node
    // which neighbor node we should visit first
    private int[] fwdTable = new int[DVRoute.NUMNODES];
    // bestPath is a temporary changing forwarding table
    private int[] bestPath = new int[DVRoute.NUMNODES];
    private int numUpdates = 0;
    

    public Node() {
        this.id = count++;
        ArrayList<Integer> neighbors_temp = new ArrayList<Integer>();
        for (int i = 0; i<DVRoute.NUMNODES; i++) {
            // reading from the DVRoute variables,
            // for each node:
            // 1. initialize its cost and myDV value
            // 2. specify the neighbors
            // 3. Initialize the forwarding table (bestPath variable)
            // BestPath to any node should be initialized as follows:
            // If node has id = this node's id, use id
            // Else if node is a direct neighbor, use the neighbor id
            // Otherwise, choose a random neighbor (see randomNeighbor method)
            // WRITE YOUR CODE HERE
        	
        	// init cost: 
        	cost[i] = DVRoute.cost[id][i];
         	// initial distance vector:
        	myDV = cost;
        	
        	if(cost[i] != 999 && cost[i] != 0) { // Found Neighbor! 
        		neighbors_temp.add(i);
        	} 	
       	
        }
        
       neighbors = new int[neighbors_temp.size()];
       for(int i = 0; i<neighbors_temp.size(); i++) {
    	   neighbors[i] = neighbors_temp.get(i);
    	   // System.out.println(neighbors[i]);
       }
      
       for (int i = 0; i<DVRoute.NUMNODES; i++) {
	    	if( i == id ) {
				bestPath[i] = id;
			
				
			}
			else if(neighbors_temp.contains(i)) {
				bestPath[i] = i;
			}
			else {
				bestPath[i] = randomNeighbor();
			}
       }
       
    	
        // send initial DV to neighbors
        notifyNeighbors();
    }

    public int getId() {
        return id;
    }


    public void printDV() {
        System.out.print("i            " );
        for (int i = 0; i<DVRoute.NUMNODES; i++) {
            System.out.print(i + "      ");
        }
        System.out.println();
        System.out.print("cost         " );
        for (int i = 0; i<DVRoute.NUMNODES; i++) {
            System.out.print(myDV[i] + "      ");
        }
        System.out.println();
    }

    public void printFwdTable() {
        System.out.println("dest         next Node" );
        for (int i = 0; i<DVRoute.NUMNODES; i++) {
            System.out.println(i + "            " + fwdTable[i]);
        }
    }

    public int randomNeighbor() {
        int rnd = new Random().nextInt(neighbors.length);
        return neighbors[rnd];
    }
    
	/*
	  public int randomNeighborArrayList(ArrayList<Integer> x) { int rnd = new
	  Random().nextInt(x.size()); return x.get(rnd); }
	 */

    public void notifyNeighbors() {
        // for each neighbor, create a new packet (see Packet class)
        // with current node id as source, neighbor id as destination
        // and current node's DV as the dv
        // then send packet using helper method sendPacket in DVRoute

        // WRITE YOUR CODE HERE
    	
    	 for(int i = 0; i<neighbors.length; i++) {
      	   Packet pkt = new Packet(id, neighbors[i], myDV);
      	   DVRoute.sendPacket(pkt);
         }
    	
    	
    }

    public void updateDV(Packet p) {
        // this method is called by the simulator each time a packet is received from a neighbor
        int neighbor_id = p.getSource();
        neighborDV[neighbor_id] = p.getDV();
        // for each value in the DV received from neighbor, see if it provides a cheaper path to
        // the corresponding node. If it does, update myDV and bestPath accordingly
        // current DV of i is min { current DV of i, cost to neighbor + neighbor's DV to i  }
        // If you do any changes to myDV:
        // 1. Notify neighbors about the new myDV using notifyNeighbors() method
        // 2. Increment the convergence measure numUpdates variable once
        // WRITE YOUR CODE HERE
       
        boolean anyUpdates = false;
        for(int i=0; i<neighborDV.length; i++) {
        	
        	// compare your current cost to node i , versus , (neighbor's cost for i + your cost for this neighbor)
        	
        	if(myDV[i] > (neighborDV[neighbor_id][i] + myDV[neighbor_id])) {
        		System.out.println("Node " + id + " has cost: " + myDV[i] + " for node " + i + " but neighbor node " + 
        	neighbor_id + " can access to that node with cost " + neighborDV[neighbor_id][i]);
        		myDV[i] = (neighborDV[neighbor_id][i] + myDV[neighbor_id]);
        		bestPath[i] = neighbor_id;
        		anyUpdates = true;
        	}	
        }
        
        if(anyUpdates) {
        	System.out.println("*** myDV changes for node " + id);
        	System.out.println("Here's my new DV : " + Arrays.toString(myDV) + " notifying neighbors...");
        	notifyNeighbors();
        	numUpdates++;
        }  
    }

    public void buildFwdTable() {
        // just copy the final values of bestPath vector
        for (int i = 0; i < DVRoute.NUMNODES; i++) {
            fwdTable[i] = bestPath[i];
        }
    }

    public int getNumUpdates() {
        return numUpdates;
    }
}
