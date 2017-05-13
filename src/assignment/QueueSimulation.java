/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Formatter;

/**
 *
 * @author n
 */
public class QueueSimulation implements QueueSimulationInterface{
    
    
    
    /**
     * @server_status it is IDLE or BUSY, server status
     * 
     * @param num_in_q number of customers in the queue
     * 
     * @param num_custs_delayed the number of customers delayed
     * 
     * @num_delays_required
     * 
     * @next_event_type
     * 
     * @num_events
     */
    public int next_event_type, num_custs_delayed, num_delays_required, num_events, 
            num_in_q, server_status;
    
    double area_num_in_q, area_server_status, mean_interarrival, mean_service, 
            time, time_last_event, total_of_delays;
    double[] time_arrival = new double[Q_LIMIT + 1];
    double[] time_next_event = new double[3];
    
    File infile = new File("mm1.txt");
    File outfile;
    
    Formatter output;
    
    /**
     * Initialization function 
     */
    @Override
    public void initialize() {
        
        time = 0;
        /* initialize state variables */
        
        server_status = IDLE;
        num_in_q = 0;
        time_last_event = 0;
        
        /* initialize the statistical counters */
        
        num_custs_delayed = 0;
        total_of_delays = 0;
        area_num_in_q = 0;
        area_server_status = 0;
        
        /* initialize event list. Since no customers are present, the 
        departure (service completion) event is eliminated from 
        consideration. */
        
        time_next_event[1] = time + expon(mean_interarrival);
        time_next_event[2] = (float) 1.0e+30;
                
    }
    
    /**
     * timing function 
     */
    @Override
    public void timing() {
        
        int i;
        double min_time_next_event = 1.0e+25;
        next_event_type = 0;
        
        /* Determine the event type of the next event to occur */
        
        for(i=1; i <= num_events; ++i){
            if(time_next_event[i] < min_time_next_event){
                min_time_next_event = time_next_event[i];
                next_event_type = i;
            }
        }
        
        /* Check to see whether the event list is empty. */
        
        if(next_event_type == 0){
            /* The event list is empty, so stop the simulation. */
            //add code for file
            writedata(output, "\nEvent list empty at the time", time);
            System.exit(1);
        }
        
        /* The event list is not empty, so advance the simulation clock */
        
        time = min_time_next_event;
    }
    
    /**
     * Arrival event function 
     */
    
    @Override
    public void arrive() {
        
        double delay;
        /* schedule next arrival. */
        
        time_next_event[1] = time + expon(mean_interarrival);
        
        /* Check to see whether server is busy */
        
        if(server_status == BUSY){
            /* server is busy, so increment number of customers in queue. */
            
            ++num_in_q;
            
            /* check to see whether an overflow condition exists. */
            
            if(num_in_q > Q_LIMIT){
                
                /* the queue has overflowed so stop the simulation */
                //add code for file
                writedata(output, "\nOverflow of the array time_arrival at");
                writedata(output, "time %f", time);
                System.exit(2);
            }
            /** 
             There is still room in the queue, so store the time of arrival 
             * of the arriving customer at the (new) end of the time_arrival.
             */
            
            time_arrival[num_in_q] = time;
            
        } else {
            
            /** 
             Server is idle,so arriving customer has a delay of zero. (The 
             * following two statements are for program clarity and do not 
             * affect the results of the simulation.) 
             */
            
            delay = 0.0;
            total_of_delays += delay;
            
            /**
             * Increment the number of customers delayed, and make server busy.
             * 
             */
            
            ++num_custs_delayed;
            server_status = BUSY;
            
            /* Schedule a departure (service completion) . */
            
            time_next_event[2] = time + expon(mean_service);
        }
    }
    
    /**
     * Departure event function 
     */
    @Override
    public void depart() {
        
        int i;
        double delay;
        
        /* check to see whether the queue is empty */
        
        if(num_in_q == 0){
            /* The queue is empty so make the server idle and eliminate the 
            departure (service completion) event from consideration. */
            
            server_status = IDLE;
            time_next_event[2] = 1.0e+30;
            
        } else {
            
            /* The queue is nonempty, so decrement the number of customers in 
            queue. */
            
            --num_in_q;
            
            /* Compute the delay of the customer who is beginning service and 
            update the total delay accumulator. */
            
            delay = time - time_arrival[1];
            total_of_delays += delay;
            
            /* Increment the number of customers delayed, and schedule \
            departure. */
            
            ++num_custs_delayed;
            time_next_event[2] = time + expon(mean_service);
            
            /* Move each  customer in queue (if any) up one place. */
            
            for(i=1; i <= num_in_q; ++i){
                
                time_arrival[i] = time_arrival[i + 1];
            }
        }
    }
    
    /**
     * report generator function 
     */
    @Override
    public void report() {
        
        /* compute and write estimates of desired measures of performance */
        
        writedata(output, "\n\nAverage delay in queue minutes", total_of_delays / num_custs_delayed);
        System.out.println();
        writedata(output, "Average number in the queue\n", area_num_in_q / time);
        writedata(output, "Server utilization\n", area_server_status / time);
        writedata(output, "Time simulation ended\n", time);
    }
    
    /**
     * update area accumulators for time-average statistics 
     */
    @Override
    public void update_time_avg_stats() {
        
        double time_since_last_event;
        
        /* compute time since last event, and update last event time marker. */
        
        time_since_last_event = time - time_last_event;
        time_last_event = time;
        
        /* update area under number-in-queue function. */
        area_num_in_q += num_in_q * time_since_last_event;
        
        /* update area under server-busy indicator function. */
        area_server_status += server_status * time_since_last_event;
    }
    
    /**
     * Exponential variate generation function
     */
    @Override
    public double expon(double mean) {
        
        double u;
        /* generate a u(0,1) random variate */
        
        u = Math.random();
        /* return an exponential random  variate with mean "mean". */
        return  -mean * Math.log(u);        
    }
    
    public void openinput(){
        infile = new File("mm1.txt", "r");
        System.out.println("this is open input");
    }
    
    public void openoutput(){
        outfile = new File("mm1.txt", "w");
        try {
            output = new Formatter("out.txt");
        } catch (FileNotFoundException ex) {
            System.out.println("error openning file");
        }
        
    }
    
    public void readdata(File infile, double d1, double d2, int i){
               
        try {
            try (BufferedReader in = new BufferedReader(new FileReader (infile))) {
                
                String data = in.readLine();
                String[] split = data.split(" ");
                String data1 = Arrays.toString(split);
                data1 = data1.trim();
                System.out.println(data1);
                d1 = Double.parseDouble(data1.substring(1,3));
                d2 = Double.parseDouble(data.substring(5, 8));
                i = Integer.parseInt(data.substring(8, 12));
                
                mean_interarrival = d1;
                mean_service = d2;
                num_delays_required = i;
                
                //while(in)
                in.close();
            }
        } catch (IOException ex) {
            System.out.println("error reading file");
        }
        
    }
    public void writedata(Formatter outer, String message){
        
        outer.format("%s%n%n", message);
         
        }
    public void writedata(Formatter outer, String message, Double d){
        outer.format("%s\t\t%11.3f\t%n", message, d);
    }
    public void writedata(Formatter outer, String message, int m){
        
        outer.format("%s\t\t%14d\t%n", message, m);
    }
//    public void closeinput(){
//        if(infile != null)
//    }
    public void closeoutput(){
        if(output != null)
            output.close();            
    } 
        
}
