package Runway;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class  GatesOperations extends Gateway implements Runnable {
    private int passengers=50;

    GatesOperations(int index, Resources shared) {
        super(index, shared);
    }

    public void run() {
        try{
            AssignedToGate();
            resetPassengers();
            PassengersAssignment();
            synchronized (rec.DepartingObject) {
                rec.DepartingObject.notifyAll();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void AssignedToGate() {
        synchronized (rec.GatesObject) {
            while (!canCoast() || GateStatus()) {
                try {
                    rec.GatesObject.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                System.out.println("Available Gates: " + rec.semaphore.availablePermits());

                rec.semaphore.acquire();
                rec.setRunwayStatus(1);
                for (int i = 0; i < rec.getGates().length; i++) {
                    if (rec.getGate(i) == null) {
                        rec.setGate(i, rec.getSpecificPlane(index));
                        System.out.println("Gate " + rec.getGateNum(index) + ": Opens!!");
                        System.out.println("Current Index " + index);
                        rec.SetPlaneStatus(index, "Assigned to Gate " + rec.getGateNum(index));
                        break;
                    }
                }
                System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " docking at Gate " + rec.getGateNum(index));
                System.out.println("Available Gates after release: " + rec.semaphore.availablePermits());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                rec.GatesObject.notifyAll();
            }
        }
    }

    void PassengersAssignment(){
        synchronized (rec.PassengerObject){
            while(!canDisembark()){
                try {
                    rec.PassengerObject.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try{
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName()+": Plane-"+rec.getSpecificPlane(index)+" Moves to disembarking passengers");
                for (int i=0; i<50; i++){
                        try {
                            Thread.sleep(50);
                            System.out.println(Thread.currentThread().getName()+": Plane-"+rec.getSpecificPlane(index)+" Passenger "+ getPassenger()+ " Disembarks from the plane");
                            disembarkedPas();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                rec.SetPlaneStatus(index,"Passengers Disembarked");
                System.out.println("Done");
                rec.PassengerObject.notifyAll();
            }
        }
    }

    synchronized void resetPassengers(){
        this.passengers=50;
    }
    int getPassenger(){return passengers;}

    void disembarkedPas(){
        this.passengers--;
    }
}