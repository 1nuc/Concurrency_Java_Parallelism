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
            new GatesOperations(index, rec).run();
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
        }
    }


    void departing(){
        synchronized (rec) {
            while (!canDepart()) {
                try {
                    System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to leave");
                    rec.wait(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Runwaylock.lock();
                int gateNum = rec.getGateNum(index);
                for (int i = 0; i < rec.getGates().length; i++) {
                    if (rec.getGate(i) != null && rec.getGate(i).equals(rec.getSpecificPlane(index))) {
                        rec.setGate(i, null);
                        break;
                    }
                }
                rec.semaphore.release(); // Release semaphore here
                System.out.println("Available Gates after release: " + rec.semaphore.availablePermits());
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
                rec.Plaindepart(index);
                rec.SetPlaneStatus(index,"Depart");
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
        return rec.semaphore.availablePermits()==0;
    }

}
