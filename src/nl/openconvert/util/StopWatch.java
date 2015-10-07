package nl.openconvert.util;


public class StopWatch 
{
    
    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;
    private long elapsedTime = 0;
    
    public void start() 
    {
        this.startTime = System.currentTimeMillis();
        this.running = true;
        this.elapsedTime = 0;
    }

    public void restart() 
    {
    	elapsedTime += stopTime - startTime;
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }
    
    public void stop() 
    {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
        elapsedTime += stopTime - startTime;
    }

    
    //elaspsed time in milliseconds
    public long getElapsedTime() 
    {
        long elapsed;
        if (running) 
        {
             elapsed = this.elapsedTime + (System.currentTimeMillis() - startTime);
        }
        else {
            elapsed = this.elapsedTime;
        }
        return elapsed;
    }
    
    
    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        }
        else {
            elapsed = ((stopTime - startTime) / 1000);
        }
        return elapsed;
    }

    
    
    
    //sample usage
    public static void main(String[] args) {
        StopWatch s = new StopWatch();
        s.start();
        //code you want to time goes here
        s.stop();
        System.out.println("elapsed time in milliseconds: " + s.getElapsedTime());
    }
}