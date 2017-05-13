/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment;

import java.io.File;
import java.util.Formatter;

/**
 *
 * @author n
 */
public class QueueSimulationTest {

    //public Assignment assign;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        QueueSimulation assign = new QueueSimulation();
        Formatter output2 = null;
        
        /* open input and output files*/
        assign.openinput();
        assign.openoutput();
        
        /* specify the number of events for the timing function. */
        
        assign.num_events = 2;
        File infile1  =new File("mm1.txt");
        
        
        
        /* read input parameters. */
        assign.readdata(infile1, assign.mean_interarrival, 
        assign.mean_service, assign.num_delays_required);
        System.out.println(assign.mean_interarrival);
        System.out.println(assign.mean_service);
        System.out.println(assign.num_delays_required);
        /* write report heading and input parameters. */
        assign.writedata(assign.output, "Single-server queueing system");
        assign.writedata(assign.output, "Mean interarrival time minutes", assign.mean_interarrival);
        assign.writedata(assign.output, "Mean service time minutes", assign.mean_service);
        assign.writedata(assign.output, "Number of customers", assign.num_delays_required);
        
        /* Initialize the simulation*/
        assign.initialize();
        
        /* Run the simulation while more delays are still needed. */
        
        while(assign.num_custs_delayed < assign.num_delays_required) {
            
            /* Determine the next event. */
            
            assign.timing();
            
            /* update time-average statistical accumulators. */
            
            assign.update_time_avg_stats();
            
            /* invoke the appropriate event function. */
            
            switch (assign.next_event_type) {
                case 1:
                    assign.arrive();
                    break;
                case 2:
                    assign.depart();
                    break;
            }
        }
        
        /* invoke the report generator and end the simulation. */
        
        assign.report();
        
//        assign.closeinput();
        assign.closeoutput();
    }
    
}
