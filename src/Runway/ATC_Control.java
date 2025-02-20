package Runway;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ATC_Control implements Runnable {
    private Resources rec;
    private int index;
    ATC_Control(Resources shared){
        this.rec=shared;
    }

    @Override
    public void run() {
        System.out.println("======== ATC Thread Active: Managing Air Traffic =========");

        while (!rec.AllPlainDepart()) {
            synchronized (rec.RunwayLock) {
                while ((index = rec.atomicIndex.get()) == -1) {
                    if (rec.WaitingQueue.isEmpty() && rec.DepartingQueue.isEmpty() || rec.UnderOperation){
                        try {
                            rec.RunwayLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                else if(!rec.WaitingQueue.isEmpty()){
                        this.index = rec.WaitingQueue.getFirst();
                        rec.atomicIndex.set(this.index);
                    }
                    else {
                        this.index=rec.DepartingQueue.getFirst();
                        rec.atomicIndex.set(index);
                    }
                }
                if(!rec.WaitingQueue.isEmpty())Land_Runway();

                if (rec.DepartingQueue.isEmpty())continue;
                this.index=rec.DepartingQueue.getFirst();
                if(rec.DepartingQueue.contains(index)) {
                    Depart_Land();
                }
            }
        }

        System.out.println("ATC: All planes have departed. ATC shutting down.");
    }

    void Land_Runway() {
        while (!runwayfree() || GateStatus()) {
            try {
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Asking permission to land");
                System.out.println(Thread.currentThread().getName() + ": " + "Checking Runway Status");
                rec.RunwayLock.wait();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        rec.RunwayLock.notifyAll();
        try {
            rec.setRunwayStatus(1);
            System.out.println(Thread.currentThread().getName() + ": Available Gates: " + rec.semaphore.availablePermits());
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain permission to land");
            Thread.sleep(1000);
            rec.Plainland(index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rec.lock.lock();
            try {
                rec.condition.signalAll();
            } finally {
                rec.lock.unlock();
            }
        }
    }



    void Depart_Land() {
        while (!runwayfree()) {
            try {
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to leave");
                rec.RunwayLock.wait();
                rec.atomicIndex.reset(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
            rec.Plaindepart(index);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        rec.lock2.lock();
        try {
            rec.condition2.signalAll();
        } finally {
            rec.lock2.unlock();
        }

    }

//
//    boolean canCoast() {
//        String status = rec.getSpecificPlaneStatus(index);
//        return status != null && status.equals("Landed");
//    }
//
//    boolean canDisembark() {
//        String status = rec.getSpecificPlaneStatus(index);
//        return status != null && status.equals("Assigned to Gate " + rec.getGateNum(index));
//    }
//
//
//    boolean canDepart() {
//        String status = rec.getSpecificPlaneStatus(index);
//        return status != null && status.equals("Passengers Disembarked");
//
//    }


    Boolean runwayfree() {
        return rec.getRunwayStatus() == 0;
    }

    boolean GateStatus() {
        return rec.semaphore.availablePermits() == 0;
    }


}

