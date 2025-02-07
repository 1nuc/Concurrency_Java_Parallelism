package Runway;

public class PlaneOperations extends Airplane implements Runnable {
    private int passengers=50;

    PlaneOperations(int index, Resources shared) {
        super(index, shared);
    }

    public void run() {
        try{
            AssignedToGate();
            resetPassengers();
            PassengersAssignment();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void AssignedToGate() {
        synchronized (rec.GatesObject) {
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
                System.out.println("Available Gates after Assigning Plane "+rec.getSpecificPlane(index)+" is: " + rec.semaphore.availablePermits());
            }
    }

    void PassengersAssignment(){
            try{
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName()+": Plane-"+rec.getSpecificPlane(index)+" Moves to disembarking passengers");
            } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                    for (int i = 0; i < 50; i++) {
                            try {
                                Thread.sleep(50);
                                System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" Passenger " + getPassenger() + " Disembarks from the plane");
                                disembarkedPas();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                              }
                        }
            synchronized (rec.DepartingObject){
                rec.SetPlaneStatus(index, "Passengers Disembarked");
                rec.DepartingObject.notifyAll();
                System.out.println("Done");
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