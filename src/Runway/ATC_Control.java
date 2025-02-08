package Runway;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ATC_Control implements Runnable {
    private int index;
    private Resources rec;
    ATC_Control(Resources shared){
        this.rec=shared;
    }

    @Override
    public void run(){
        System.out.println("======== ATC Thread =========");
        synchronized (rec.RunwayLock){
            int i=0;
            while(i < rec.getPlanesQ().length){
                index =i;
                if (rec.getSpecificPlaneStatus(index)==null ||!rec.getSpecificPlaneStatus(index).equals("Waiting")){
                    try {
                        rec.RunwayLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                checkRunway();
                i++;
            }
        }
    }


    void checkRunway() {
            while (!runwayfree() || GateStatus()) {
                try {
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Asking premission to land");
                    System.out.println(Thread.currentThread().getName() + ": " + "Checking Runway Status");
                    rec.RunwayLock.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                System.out.println(Thread.currentThread().getName() + ": Available Gates: " + rec.semaphore.availablePermits());
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain permission to land");
                rec.setRunwayStatus(1);
                Thread.sleep(1000);
                if (rec.semaphore.availablePermits() > 0) {
                    rec.semaphore.Acquire();
                    rec.SetPlaneStatus(index, "Landed");
                    for (int i = 0; i < rec.getGates().length; i++) {
                        if (rec.getGate(i) == null) {
                            rec.setGate(i, rec.getSpecificPlane(index));
                            System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " Assined Gate " + rec.getGateNum(i));
                            rec.setRunwayStatus(0);
                            rec.Plainland(index);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


    }



    void departing() {
        synchronized (rec.DepartingObject) {
            while (!canDepart()) {
                try {
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to leave");
                    rec.DepartingObject.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                for (int i = 0; i < rec.getGates().length; i++) {
                    if (rec.getGate(i) != null && rec.getGate(i).equals(rec.getSpecificPlane(index))) {
                        rec.setGate(i, null);
                        break;
                    }
                }
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
                synchronized (rec.LandingObject) {
                    rec.semaphore.Release();
                    rec.LandingObject.notifyAll();
                }
                rec.Plaindepart(index);
                rec.SetPlaneStatus(index, "Depart");
                rec.setRunwayStatus(1);
                rec.DepartingObject.notifyAll();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }


    boolean canCoast() {
        String status = rec.getSpecificPlaneStatus(index);
        return status != null && status.equals("Landed");
    }

    boolean canDisembark() {
        String status = rec.getSpecificPlaneStatus(index);
        return status != null && status.equals("Assigned to Gate " + rec.getGateNum(index));
    }


    boolean canDepart() {
        String status = rec.getSpecificPlaneStatus(index);
        return status != null && status.equals("Passengers Disembarked");

    }


    Boolean runwayfree() {
        return rec.getRunwayStatus() == 0;
    }

    boolean GateStatus() {
        return rec.semaphore.availablePermits() == 0;
    }


}

