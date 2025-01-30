package Runway;

import java.util.concurrent.locks.ReentrantLock;



public class Gateway extends Resources implements Runnable{
    protected final Resources rec;
    protected final int index;
    protected static final ReentrantLock Runwaylock= new ReentrantLock();

    Gateway(int index, Resources sharedRec){
        this.rec=sharedRec;
        this.index=index;
    }
    public void run(){

        System.out.println("Thread "+Thread.currentThread().getName()+" Is: "+Thread.currentThread().getState());
        System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: "+rec.getSpecificPlane(index)+" Requesting to land");
        try {
            synchronized (rec) {
                if (rec.getRunwayStatus() == 0) {
                    rec.setRunwayStatus(1);
                }
            }
            landing();
            departing();
            } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void landing(){
        synchronized (rec) {
            while (!runwayfree()) {
                try {
                    System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to land");
                    rec.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try{
                Runwaylock.lock();
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to land");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Landing........");
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Landed Successfully");
                rec.Plainland(index);
                rec.SetPlaneStatus(index, "Landed");
                rec.setRunwayStatus(2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally{
                rec.notifyAll();
                Runwaylock.unlock();
            }
            //notes for tomorrow
            // watch some videos about semaphore operations
            // you should call the assign gate function inside the Plain land function
            // where using the Q you can track which planes first landed so you can assign them directly to another Threads
            // to start the process for the gates
            if(canCoast()|| GateStatus()) {
                try {
                    Thread thread = new Thread(new GatesOperations(index, rec));
                    rec.AssignGate(index);
                    thread.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    void departing(){
        synchronized (rec) {
            if (!canDepart()) {
                try {
                    System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to leave");
                    rec.wait(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Runwaylock.lock();
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
                rec.setRunwayStatus(1);
                rec.Plaindepart(index);
                rec.SetPlaneStatus(index,"Depart");
                rec.OpenGate(index);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally{
                Runwaylock.unlock();
                rec.notifyAll();
            }
        }
    }
    boolean canCoast() {
        String status = rec.getSpecificPlaneStatus(index);
        return status != null && status.equals("Landed");
    }
    boolean canDepart() {
        String status = rec.getSpecificPlaneStatus(index);
        return status != null && status.equals("Assigned to Gate "+rec.getGateNum(index));
    }

    Boolean runwayfree(){
        return rec.getRunwayStatus()==1;
    }

    boolean GateStatus(){
        return rec.getGateNum(index)==0;
    }

}
