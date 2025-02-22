package Runway;

import javax.swing.*;
import java.sql.SQLOutput;

public class Airplane  implements Runnable{
        public static Resources rec;
        boolean UnderOperation;
        public final int index;
        private Thread thread;
        Airplane(int index, Resources sharedRec){
            this.rec=sharedRec;
            this.index=index;
        }
        public void run() {
            System.out.println("Thread " + Thread.currentThread().getName() + " Is: " + Thread.currentThread().getState());
            System.out.println("Thread " + Thread.currentThread().getName() + " Plane: " + rec.getSpecificPlane(index) + " is Added to the Waiting Queue");

            rec.Add_Planes_Queue(index);
            synchronized (rec.RunwayLock) {
                rec.RunwayLock.notifyAll();
            }
            thread = new Thread(new PlaneOperations(index, rec), "Thread " + index + "-Planes Operation");
            landing();
                if(UnderOperation){
                    try {
                        rec.RunwayLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            departing();

        }

        void landing() {
                try{
                    rec.lock.lock();
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
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landing........");
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landed Successfully");
                System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " docking at Gate " + rec.getGateNum(index));
                rec.Rem_Planes_Queue(index);
                synchronized (rec.RunwayLock){
                    rec.setRunwayStatus(0);
                    rec.atomicIndex.reset(this.index);
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
                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
                synchronized (rec.RunwayLock) {
                    rec.setRunwayStatus(0);
                    rec.RunwayLock.notifyAll();
                }
        }

    }
