package Runway;

public class Passengers implements Runnable {
    private int index;
    private Resources rec;
    private PlaneOperations planeOps; // Reference to the parent plane

    Passengers(int index, Resources shared, PlaneOperations planeOps) {
        this.index = index;
        this.rec = shared;
        this.planeOps = planeOps;
    }

    // Use planeOps.PassengerLock, planeOps.passengers, etc.

    public void run(){
        synchronized (planeOps.PassengerLock) {
            while (!planeOps.PassengerdisEmbarked) {
                try {
                    planeOps.PassengerLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName()+": "+ planeOps.passengers + " Passengers Disembark from the plane: " + rec.getSpecificPlane(index));
            for (int i = 0; i < 50; i++) disembarkedPas();
            while (!planeOps.PassengerEmbarked) {
                try {
                    planeOps.PassengerLock.wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            for (int i = 0; i < 50; i++){
                rec.PassengersBoarded++;
                embarkedPas();
            }
            System.out.println(Thread.currentThread().getName() +": "+ planeOps.passengers+ " Passengers board to the plane: " + rec.getSpecificPlane(index));

        }
    }


    void disembarkedPas() {
        planeOps.passengers--;
    }

    void embarkedPas(){
        planeOps.passengers++;
    }
    int getPassenger(){return planeOps.passengers;}

}
