package Runway;

public class PlaneOperations extends Airplane implements Runnable {
    private int passengers=50;

    PlaneOperations(int index, Resources shared) {
        super(index, shared);
    }

    public void run() {
        try{
            resetPassengers();
            PassengersAssignment();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

       void PassengersAssignment() {
           try {
               Thread.sleep(1000);
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Moves to disembarking passengers");
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
           for (int i = 0; i < 50; i++) disembarkedPas();
           System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" 50 Passengers Disembarks from the plane");
           rec.Add_Planes_Departing_Queue(index);
    }



    synchronized void resetPassengers(){
        this.passengers=50;
    }
    int getPassenger(){return passengers;}

    void disembarkedPas(){
        this.passengers--;
    }
}