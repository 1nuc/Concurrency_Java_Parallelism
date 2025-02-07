package Runway;

public class CustomSemaphore {
    private int permits;

    public CustomSemaphore(int permits){
        this.permits=permits;
    }

    public synchronized void Acquire() throws InterruptedException {
            while(permits== 0){
                this.wait();
            }
            permits--;
    }
    public synchronized void Release(){
        permits++;
        if(permits >0) {
            this.notifyAll();
        }
    }

    public int availablePermits(){
        return permits;
    }

}
