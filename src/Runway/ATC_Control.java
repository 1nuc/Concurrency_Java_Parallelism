package Runway;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ATC_Control implements Runnable {
    private Resources rec;
    private int index;
    private SanityChecks checks;
    private long startTime;
    private long endTime;
    ATC_Control(Resources shared){
        this.rec=shared;
        checks=new SanityChecks(rec);
    }

    @Override
    public void run() {
        startTime=System.currentTimeMillis();
        System.out.println("======== ATC Thread Active: Managing Air Traffic =========");

        while (!rec.AllPlainDepart()) {
            synchronized (rec.RunwayLock) {
                while ((index = rec.atomicIndex.get()) == -1) {
                    if (rec.WaitingQueue.isEmpty() && rec.DepartingQueue.isEmpty()) {
                        try {
                            if(rec.AllPlainDepart())break;
                            rec.RunwayLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    } else if (!rec.WaitingQueue.isEmpty()) {
                        this.index = rec.WaitingQueue.getFirst();
                        rec.atomicIndex.set(this.index);
                    } else {
                        this.index = rec.DepartingQueue.getFirst();
                        rec.atomicIndex.set(this.index);

                    }
                }
                if (EmergencyScenario()) {
                    index=rec.getEmergencyIndex();
                    rec.atomicIndex.set(index);
                    rec.Handle_Plane_Queue_Emergency(this.index);
                    System.out.println(Thread.currentThread().getName()+": Plane: "+rec.getSpecificPlane(index)+" requesting for emergency landing");

                }
                if (!rec.WaitingQueue.isEmpty() && rec.WaitingQueue.contains(index)) {
                    Land_Runway();
                }

                if (rec.DepartingQueue.isEmpty()) continue;
                Integer firstIndex= rec.DepartingQueue.getFirst();
                if(firstIndex!=null){
                    this.index=firstIndex;
                    rec.atomicIndex.set(this.index);
                    Depart_Land();
                }

            }
        }
        endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime) / 1000;
        checks.output(totalTime);
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
        try {
            if (rec.LandingPrem(index)) {
                return;
            }
            Thread.sleep(1000);
            rec.setRunwayStatus(1);
            rec.Plainland(index);
            System.out.println(Thread.currentThread().getName() + ": Available Gates: " + rec.semaphore.availablePermits());
            System.out.println(Thread.currentThread().getName() + ": Runway is free");
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain permission to land");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rec.lock.lock();
            try {
                rec.condition.signalAll();
            } finally {
                rec.lock.unlock();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    void Depart_Land() {
        while (!runwayfree() || !rec.getStatus(index).equals("WaitingToDepart")) {
            try {
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to leave");
                rec.RunwayLock.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if(rec.DepartingPrem(index)){
                return;
            }
            rec.Plaindepart(index);
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
            Thread.sleep(500);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    boolean EmergencyScenario(){
        int WaitingCount=0;
        int DepartedCount=0;
        int LandedCount=0;
        for(int i=0; i <rec.getPlanesID().length; i++){
            if(rec.getStatus(i).equals("Departed"))DepartedCount++;
            if(rec.getStatus(i).equals("Landed"))LandedCount++;
            if(rec.getStatus(i).equals("WaitingToLand"))WaitingCount++;
        }
        return LandedCount==2 && WaitingCount==3 && DepartedCount==1;
    }


    Boolean runwayfree() {
        return rec.getRunwayStatus() == 0;
    }

    boolean GateStatus() {
        return rec.semaphore.availablePermits() == 0;
    }


}

