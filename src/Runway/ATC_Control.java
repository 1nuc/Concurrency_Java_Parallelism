package Runway;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ATC_Control implements Runnable {
    private Resources rec;
    private int index;
    private SanityChecks checks;
    ATC_Control(Resources shared){
        this.rec=shared;
        checks=new SanityChecks(rec);
    }

    @Override
    public void run() {
        System.out.println("======== ATC Thread Active: Managing Air Traffic =========");

        while (!rec.AllPlainDepart()) {
            synchronized (rec.RunwayLock) {
                while ((index = rec.atomicIndex.get()) == -1) {
                    if (rec.WaitingQueue.isEmpty() && rec.DepartingQueue.isEmpty()) {
                        try {

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
                        rec.atomicIndex.set(index);
                    }
                }
                if (EmergencyScenario()) {
                    index=EmergencyHandling();
                    rec.atomicIndex.set(index);
                    rec.Handle_Plane_Queue_Emergency(index);
                    System.out.println("Plane: "+rec.getSpecificPlane(EmergencyHandling())+" requesting for emergency landing");

                }
                if (!rec.WaitingQueue.isEmpty()) {
//                    System.out.println(rec.atomicIndex.get());
//                    System.out.println("The first landing starts 2: "+rec.WaitingQueue);
                    Land_Runway();
                }

                if (rec.DepartingQueue.isEmpty() && !rec.getStatus(index).equals("WaitingToDepart")) continue;
                this.index = rec.DepartingQueue.getFirst();
                rec.atomicIndex.set(index);
                Depart_Land();

            }
        }
        checks.output();
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
                System.out.println(Thread.currentThread().getName() + ": Runway Occupied");
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
        while (!runwayfree()) {
            try {
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to leave");
                rec.RunwayLock.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if(rec.DepartingPrem(index)){
                System.out.println(Thread.currentThread().getName() + ": Runway Occupied");
                return;
            }
            rec.Plaindepart(index);
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
            Thread.sleep(500);

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


    int EmergencyHandling() {
        int WaitingCount=0;
        int ThirdPlaneIndex=0;
        int DepartedCount=0;
        int LandedCount=0;
        for(int i=0; i <rec.getPlanesID().length; i++){
            if(rec.getStatus(index).equals("Departed"))DepartedCount++;
            if(rec.getStatus(index).equals("Landed"))LandedCount++;
            if(rec.getStatus(index).equals("WaitingToLand"))WaitingCount++;ThirdPlaneIndex=i;
            if(DepartedCount==1 && LandedCount==2 && WaitingCount==3){
                return ThirdPlaneIndex;
            }
        }
        return ThirdPlaneIndex;
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

