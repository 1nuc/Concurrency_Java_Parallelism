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
            System.out.println("Thread "+Thread.currentThread().getName()+" Plane: "+rec.getSpecificPlane(index)+" Waiting to land");
            rec.SetPlaneStatus(index, "Waiting");

            synchronized (rec.RunwayLock){
                rec.RunwayLock.notifyAll();
            }

            thread=new Thread(new PlaneOperations(index, rec), "Thread "+index+"-Planes Operation");
            landing();
            System.out.println("Reaching Passenegers");
            thread.start();
            departing();
        }

        void landing() {
                while (!rec.LandingPrem(index)) {
                    try {
                        System.out.println("not ");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landing........");
            System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landed Successfully");
            System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " docking at Gate " + rec.getGateNum(index));
            synchronized (rec.LandingObject){
                rec.LandingObject.notifyAll();
            }

        }


        void departing() {
            synchronized (rec.DepartingObject) {

                System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
            }
        }
        boolean canCoast() {
            String status = rec.getSpecificPlaneStatus(index);
            return status != null && status.equals("Landed");
        }

        boolean canDisembark() {
            String status = rec.getSpecificPlaneStatus(index);
            return status != null && status.equals("Assigned to Gate "+rec.getGateNum(index));
        }


        boolean canDepart(){
            String status = rec.getSpecificPlaneStatus(index);
            return status != null && status.equals("Passengers Disembarked");

        }


        Boolean runwayfree(){
            return rec.getRunwayStatus()==0;
        }

        boolean GateStatus(){
            return rec.semaphore.availablePermits()==0;
        }


    }
