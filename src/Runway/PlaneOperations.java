package Runway;

import java.util.concurrent.locks.ReentrantLock;

public class PlaneOperations extends Airplane implements Runnable {
    private Thread PassangerThread;
    protected int passengers=50;
    protected Object PassengerLock=new Object();
    protected boolean PassengerdisEmbarked=false;
    protected boolean PassengerEmbarked=false;
    PlaneOperations(int index, Resources shared) {
        super(index, shared);
    }

    public void run() {
        try{
            PassangerThread=new Thread(new Passengers(index, rec, this), "Passengers Thread "+index);
            PassangerThread.start();

            resetPassengers();
            PassengersDisembarking();
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
           synchronized (PassengerLock){
               PassengerdisEmbarked=true;
               PassengerLock.notifyAll();
           }
           try {
               Thread.sleep(500);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
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
                Thread.sleep(1000);
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
           synchronized (PassengerLock){
               PassengerEmbarked=true;
               PassengerLock.notifyAll();
           }
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }

           rec.lock2.lock();
           rec.Add_Planes_Departing_Queue(index);
           rec.changeStatus(index, "PassengerEmbarked");
           try {
               rec.condition2.signalAll();
           } catch (Exception e) {
               throw new RuntimeException(e);
           }finally {
               rec.lock2.unlock();
           }

    }

    synchronized void resetPassengers(){
        this.passengers=50;
    }


}