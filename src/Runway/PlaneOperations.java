package Runway;

public class PlaneOperations extends Airplane implements Runnable {
    private int passengers=50;

    PlaneOperations(int index, Resources shared) {
        super(index, shared);
    }

    public void run() {
        try{
            synchronized (rec.RunwayLock){
                rec.UnderOperation=true;
            }
            resetPassengers();
            PassengersDisembarking();
            CleaningAircraft();
            RefillSupplies();
            Refuelling();
            PassengersAssignment();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

       void PassengersDisembarking() {
           try {
               Thread.sleep(1000);
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Moves to disembarking passengers");
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
           for (int i = 0; i < 50; i++) disembarkedPas();
           System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " 50 Passengers Disembarks from the plane");
       }

    void CleaningAircraft() {
           try {
               Thread.sleep(1000);
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Moves to Aircraft CLeaning");
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
           System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" Successfully cleaned");
    }


    void RefillSupplies() {
           try {
               Thread.sleep(1000);
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Moves to Aircraft Replenishment");
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
           System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" refills supplies successfully");
    }


    void Refuelling() {
        while(rec.RefuellingSemaphore.availablePermits()==0){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Waiting to be refueled");
        }
           try {
               rec.RefuellingSemaphore.Acquire();
               Thread.sleep(1000);
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Moves to Refuelling aircraft");
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" refuelled sucessfully");
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }finally{
               rec.RefuellingSemaphore.Release();
           }

    }

    void PassengersAssignment() {
           try {
               Thread.sleep(1000);
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Moves to Embarking passengers");
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
           for (int i = 0; i < 50; i++) embarkedPas();
           System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" 50 Passengers Embark to the plane");
           synchronized (rec.RunwayLock){
                rec.Add_Planes_Departing_Queue(index);
                rec.UnderOperation=false;
                rec.RunwayLock.notifyAll();
           }
    }



    synchronized void resetPassengers(){
        this.passengers=50;
    }
    int getPassenger(){return passengers;}

    void disembarkedPas() {
        this.passengers--;
    }

    void embarkedPas(){
        this.passengers++;
    }

}