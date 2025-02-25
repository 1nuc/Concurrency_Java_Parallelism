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
            thread = new Thread(new PlaneOperations(index, rec), "Thread " + index + "-Planes Operation");
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
                for (int i = 0; i < rec.getGates().length; i++) {
                    if (rec.getGate(i) == null) {
                        rec.setGate(i, rec.getSpecificPlane(index));
                        System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " Assined Gate " + rec.getGateNum(index));
                        break;
                    }
                }
                rec.Rem_Planes_Queue(index);
                rec.atomicIndex.reset(index);
                rec.changeStatus(index, "Landed");
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landing........");
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landed Successfully");
                System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " docking at Gate " + rec.getGateNum(index));
                synchronized (rec.RunwayLock){
                    rec.setRunwayStatus(0);
                    rec.RunwayLock.notifyAll();
                }
                thread.start();
        }


        void departing() {
            try{
                rec.lock2.lock();
                while (!rec.DepartingPrem(index)) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " Plane: " + rec.getSpecificPlane(index) + " is Waiting for a permission to leave ");
                        rec.condition2.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }finally{
                    rec.condition2.signalAll();
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
                rec.semaphore.Release();
                rec.atomicIndex.reset(index);
                rec.changeStatus(index, "Departed");
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
                synchronized (rec.RunwayLock) {
                    rec.setRunwayStatus(0);
                    rec.DepartureTime.set(index, System.currentTimeMillis());
                    rec.WaitingTime.set(index, rec.DepartureTime.get(index) - rec.ArrivalTime.get(index));
                    rec.RunwayLock.notifyAll();
                }
        }

    }
