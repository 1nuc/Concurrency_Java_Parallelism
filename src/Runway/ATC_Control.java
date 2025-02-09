package Runway;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ATC_Control implements Runnable {
    private Resources rec;
    ATC_Control(Resources shared){
        this.rec=shared;
    }

    @Override
    public void run() {
        System.out.println("======== ATC Thread Active: Managing Air Traffic =========");

        while (!rec.AllPlainDepart()) {
            synchronized (rec.RunwayLock) {
            int liveindex;
                while ((liveindex = rec.atomicIndex.get()) == -1) {
                    try {
                        rec.RunwayLock.wait();// Small wait to reduce CPU usage
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                Land_Runway(liveindex);

            }
        if (rec.DepartingQueue.isEmpty())continue;
        synchronized (rec.DepartingObject) {
            int departindex=rec.DepartingQueue.getFirst();
            Depart_Land(departindex);
            }

        }

        System.out.println("ATC: All planes have departed. ATC shutting down.");
    }

    void Land_Runway(int index) {
            while (!runwayfree() || GateStatus() ) {
                try {
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Asking premission to land");
                    System.out.println(Thread.currentThread().getName() + ": " + "Checking Runway Status");
                    rec.RunwayLock.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                rec.setRunwayStatus(1);
                System.out.println(Thread.currentThread().getName() + ": Available Gates: " + rec.semaphore.availablePermits());
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain permission to land");
                Thread.sleep(1000);
                if (rec.semaphore.availablePermits() > 0) {
                    rec.semaphore.Acquire();
                    for (int i = 0; i < rec.getGates().length; i++) {
                        if (rec.getGate(i) == null) {
                            rec.setGate(i, rec.getSpecificPlane(index));
                            System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " Assined Gate " + rec.getGateNum(index));
                            rec.Plainland(index);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }



    void Depart_Land(int index) {
            while (!runwayfree()) {
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
                        System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Undock from gate "+rec.getGateNum(index));
                        rec.setGate(i, null);
                        break;
                    }
                }
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
                rec.Plaindepart(index);

            } catch (Exception e) {
                throw new RuntimeException(e);
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

