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
            } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        Thread thread=new Thread(new GatesOperations(index, rec), "Thread "+index+"-Planes Operation");
        landing();
        new GatesOperations(index, rec);
        //        thread.start();
        //Potential Improvement include a thread to simulate the process for the Gates Operations
        departing();
    }

    void landing(){
        synchronized (rec.LandingObject) {
            while (!runwayfree() || GateStatus()) {
                try {
                    System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to land");
                    rec.LandingObject.wait();
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
            if(rec.semaphore.availablePermits()!=0){
                    Runwaylock.unlock();
                    rec.LandingObject.notifyAll();
            }
        }
    }


    void departing(){
        synchronized (rec.DepartingObject) {
            while (!canDepart()) {
                try {
                    System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to leave");
                    rec.DepartingObject.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Runwaylock.lock();
                for (int i = 0; i < rec.getGates().length; i++) {
                    if (rec.getGate(i) != null && rec.getGate(i).equals(rec.getSpecificPlane(index))) {
                        rec.setGate(i, null);
                        break;
                    }
                }
                rec.semaphore.release(); // Release semaphore here
                System.out.println(rec.semaphore.availablePermits()+" Gates Available");
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
                System.out.println(Thread.currentThread().getName()+": "+"Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
                rec.Plaindepart(index);
                rec.SetPlaneStatus(index,"Depart");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally{
                rec.DepartingObject.notifyAll();
                Runwaylock.unlock();
            }
        }
    }



    boolean canCoast() {
        String status = rec.getSpecificPlaneStatus(index);
        return status != null && status.equals("Landed");
    }

    boolean canDisembark() {
        String status = rec.getSpecificPlaneStatus(index);
        return status != null && status.equals("Assigned to Gate "+rec.getGateNum(index));
    }


    boolean canDepart(){
        String status = rec.getSpecificPlaneStatus(index);
        return status != null && status.equals("Passengers Disembarked");

    }


    Boolean runwayfree(){
        return rec.getRunwayStatus()==1;
    }

    boolean GateStatus(){
        return rec.semaphore.availablePermits()==0;
    }


}
