package Runway;

import javax.swing.*;
import java.sql.SQLOutput;

public class Airplane  implements Runnable{
        public static Resources rec;
        public final int index;
        private Thread thread;
        Airplane(int index, Resources sharedRec){
            this.rec=sharedRec;
            this.index=index;
        }
        public void run() {
            System.out.println("Thread " + Thread.currentThread().getName() + " Is: " + Thread.currentThread().getState());
            System.out.println("Thread " + Thread.currentThread().getName() + " Plane: " + rec.getSpecificPlane(index) + " is Added to the Waiting Queue");

            rec.atomicIndex.set(index);
            synchronized (rec.RunwayLock) {
                rec.Add_Planes_Queue(index);
                rec.setStatus(index);
                rec.ArrivalTime.set(index, System.currentTimeMillis());
                rec.RunwayLock.notifyAll();
            }
            thread = new Thread(new PlaneOperations(index, rec), "Thread " + "GTO-Operations " +index );
            landing();
            rec.lock2.lock();
            try {
                while(!rec.getStatus(index).equals("PassengerEmbarked")){
                        rec.condition2.await();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally{
                rec.condition2.signalAll();
                rec.lock2.unlock();
            }
                departing();

        }

        void landing() {
                try{
                    rec.lock.lock();
                    rec.changeStatus(index, "WaitingToLand");
                    while (!rec.LandingPrem(index)) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " Plane: " + rec.getSpecificPlane(index) + " is Waiting for a permission to land ");
                            rec.condition.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }finally {
                    rec.condition.signalAll();
                    rec.lock.unlock();
                }
                try {
                    rec.semaphore.Acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                rec.Rem_Planes_Queue(index);
                rec.changeStatus(index, "Landed");
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landing........");
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landed Successfully");
                System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " docking at Gate " + rec.getGateNum(index));
                rec.atomicIndex.reset(index);
                synchronized (rec.RunwayLock){
                    rec.setRunwayStatus(0);
                    rec.RunwayLock.notifyAll();
                }
                thread.start();


        }


        void departing() {
            try{
                rec.lock2.lock();
                rec.changeStatus(index,"WaitingToDepart");
                System.out.println(Thread.currentThread().getName() + " Plane: " + rec.getSpecificPlane(index) + " is Waiting for a permission to leave ");
                synchronized (rec.RunwayLock){
                    rec.RunwayLock.notifyAll();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }finally{
                rec.lock2.unlock();
            }
            rec.Rem_Planes_Departing_Queue(index);
            for (int i = 0; i < rec.getGates().length; i++) {
                if (rec.getGate(i) != null && rec.getGate(i).equals(rec.getSpecificPlane(index))) {
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Undock from gate "+rec.getGateNum(index));
                    rec.setGate(i, null);
                    break;
                }
            }
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
            rec.semaphore.Release();
            rec.atomicIndex.reset(index);
            rec.changeStatus(index, "Departed");
            synchronized (rec.RunwayLock) {
                rec.setRunwayStatus(0);
                rec.DepartureTime.set(index, System.currentTimeMillis());
                rec.WaitingTime.set(index, rec.DepartureTime.get(index) - rec.ArrivalTime.get(index));
                rec.RunwayLock.notifyAll();
            }
        }

    }
