package Runway;

import javax.swing.*;

public class Airplane  implements Runnable{
        public static Resources rec;
        public final int index;
        private Thread thread;
        Airplane(int index, Resources sharedRec){
            this.rec=sharedRec;
            this.index=index;
        }
        public void run(){
            System.out.println("Thread "+Thread.currentThread().getName()+" Is: "+Thread.currentThread().getState());
            System.out.println("Thread "+Thread.currentThread().getName()+" Plane: "+rec.getSpecificPlane(index)+" is Added to the Waiting Queue");
            rec.atomicIndex.set(index);
            synchronized (rec.RunwayLock){
                rec.Add_Planes_Queue(index);
                rec.RunwayLock.notifyAll();
            }

            thread=new Thread(new PlaneOperations(index, rec), "Thread "+index+"-Planes Operation");
            while (!rec.LandingPrem(index)) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            landing();
            while (!rec.DepartingPrem(index)) {
                try {
                    System.out.println("Thread "+Thread.currentThread().getName()+" Plane: "+rec.getSpecificPlane(index)+" is Waiting for a permission to leave ");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
                departing();


        }

        void landing() {
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landing........");
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landed Successfully");
            System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " docking at Gate " + rec.getGateNum(index));
            synchronized (rec.RunwayLock){
                rec.Rem_Planes_Queue(index);
                rec.setRunwayStatus(0);
                rec.RunwayLock.notifyAll();
            }
            thread.start();
        }


        void departing() {
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
            synchronized (rec.DepartingObject) {
                rec.setRunwayStatus(0);
                rec.Rem_Planes_Departing_Queue(index);
                rec.DepartingObject.notifyAll();
            }
            synchronized (rec.RunwayLock) {
                rec.semaphore.Release();
                rec.RunwayLock.notifyAll();
            }
        }
//        boolean canCoast() {
//            String status = rec.getSpecificPlaneStatus(index);
//            return status != null && status.equals("Landed");
//        }
//
//        boolean canDisembark() {
//            String status = rec.getSpecificPlaneStatus(index);
//            return status != null && status.equals("Assigned to Gate "+rec.getGateNum(index));
//        }
//
//
//        boolean canDepart(){
//            String status = rec.getSpecificPlaneStatus(index);
//            return status != null && status.equals("Passengers Disembarked");
//
//        }


        Boolean runwayfree(){
            return rec.getRunwayStatus()==0;
        }

        boolean GateStatus(){
            return rec.semaphore.availablePermits()==0;
        }


    }
