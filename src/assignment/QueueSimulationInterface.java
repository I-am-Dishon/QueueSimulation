/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment;

/**
 *
 * @author n
 */
public interface QueueSimulationInterface {
    public final int Q_LIMIT=100;
    final int BUSY = 1;
    final int IDLE = 0;
    void initialize();
    void  timing();
    void arrive();
    void depart();
    void report();
    void update_time_avg_stats();
    double expon(double mean);
}
