    package Runway;


    public class Airplane  implements Runnable{
        protected final Resources rec;
        protected final int index;
        private Thread thread;

        Airplane(int index, Resources sharedRec){
            this.rec=sharedRec;
            this.index=index;
        }
        public void run(){

            System.out.println("Thread "+Thread.currentThread().getName()+" Is: "+Thread.currentThread().getState());
            rec.SetPlaneStatus(index, "Waiting to land");
            try {
                synchronized (rec) {
                    if (rec.getRunwayStatus() == 0) {
                        rec.setRunwayStatus(1);
                    }
                }
                } catch (Exception e) {
                throw new RuntimeException(e);
            }
            thread=new Thread(new PlaneOperations(index, rec), "Thread "+index+"-Planes Operation");
            landing();
            departing();
        }

        void landing() {
            synchronized (rec.LandingObject) {
                while (!runwayfree() || GateStatus()) {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to land");
                        rec.LandingObject.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    System.out.println("Available Gates: " + rec.semaphore.availablePermits());
                    rec.setRunwayStatus(2);
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to land");
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landing........");
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Landed Successfully");
                    rec.Plainland(index);
                    rec.SetPlaneStatus(index, "Landed");
                    if (rec.semaphore.availablePermits() > 0) {
                        rec.semaphore.Acquire();
                        rec.setRunwayStatus(1);
                        thread.start();
                        rec.LandingObject.notifyAll();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally{

                    rec.LandingObject.notifyAll();
                }
            }
        }


        void departing() {
            synchronized (rec.DepartingObject) {
                while (!canDepart()) {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Waiting to leave");
                        rec.DepartingObject.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    for (int i = 0; i < rec.getGates().length; i++) {
                        if (rec.getGate(i) != null && rec.getGate(i).equals(rec.getSpecificPlane(index))) {
                            rec.setGate(i, null);
                            break;
                        }
                    }
                    System.out.println(rec.semaphore.availablePermits() + " Gates Available");
                    Thread.sleep(500);
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Obtain access to depart");
                    System.out.println(Thread.currentThread().getName() + ": " + "Plane with ID: " + rec.getSpecificPlane(index) + " Leaving");
                    synchronized (rec.LandingObject){
                        rec.semaphore.Release();
                        rec.LandingObject.notifyAll();
                    }
                    rec.Plaindepart(index);
                    rec.SetPlaneStatus(index, "Depart");
                    rec.setRunwayStatus(1);
                    rec.DepartingObject.notifyAll();

                } catch(Exception e){
                throw new RuntimeException(e);
                }

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
            return rec.getRunwayStatus()==1;
        }

        boolean GateStatus(){
            return rec.semaphore.availablePermits()==0;
        }

        boolean AtcCheck(){
            return Thread.currentThread().getName().equals("ATC-Thread");
        }

    }
