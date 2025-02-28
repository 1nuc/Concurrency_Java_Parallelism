package Runway;

import java.util.concurrent.locks.ReentrantLock;

public class PlaneOperations extends Airplane implements Runnable {
    private Thread PassangerThread;
    private Thread RestockingThread;
    private Thread CleaningThread;
    private Thread RefuellingThread;
    protected int passengers=50;
    protected Object PassengerLock=new Object();
    protected boolean PassengerdisEmbarked=false;
    protected boolean PassengerEmbarked=false;
    PlaneOperations(int index, Resources shared) {
        super(index, shared);
    }

    public void run() {
        try{
            RestockingThread=new Thread(new PlanesRestock(index, rec, this), "GTO-Restocking Thread "+index);
            CleaningThread=new Thread(new PlaneCleaning(index, rec, this), "GTO-Cleaning Thread "+index);
            RefuellingThread=new Thread(new RefuellingAircraft(index, rec, this), "GTO-Refuelling Thread "+index);
            PassangerThread=new Thread(new Passengers(index, rec, this), "GTO-Passengers Thread "+index);
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
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " permitted passengers to disembark");
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
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Started CLeaning");
           try {
               CleaningThread.start();
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
    }


    void RefillSupplies() {
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Started restocking supplies");
           try {
               RestockingThread.start();
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
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
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Granted permission to Refuel");
               RefuellingThread.start();
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }finally{
               rec.RefuellingSemaphore.Release();
           }

    }

    void PassengersAssignment() {
           try {
               Thread.sleep(1000);
               System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Moves to boarding passengers");
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