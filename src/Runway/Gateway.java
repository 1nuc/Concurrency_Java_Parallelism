package Runway;

import static Runway.Main.count;


public class Gateway extends Resources implements Runnable{
    private final Resources rec;
    Gateway(){
        this.rec=new Resources();
        rec.setPlanesID();
        rec.setAccess();
    }
    public void run(){
        System.out.println("Thread "+Thread.currentThread().getName()+" Is Running");
        System.out.println("Plane with ID: "+rec.getPlane(count)+" want to land");
        System.out.println(count);
        try{
            if(rec.getGatewayStatus()==0){
                rec.setGatewayStatus(1);
            }
                landing();
                departing();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void landing(){
        synchronized (rec) {
            if (!runwayfree()) {
                try {
                    System.out.println("Plane with ID: " + rec.getPlane(count) + " Waiting to land");
                    rec.wait(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rec.pop(rec.getAccess());
                System.out.println("Plane with ID: " + rec.getPlane(count) + " Obtain access to land");
                System.out.println("Plane with ID: " + rec.getPlane(count) + " Landing");
                rec.setGatewayStatus(2);
                rec.notify();
            }
        }
    }

    void departing(){
        synchronized (rec) {
            if (runwayfree()) {
                try {
                    System.out.println("Plane with ID: " + rec.getPlane(count) + " Waiting to leave");
                    rec.wait(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Plane with ID: " + rec.getPlane(count) + " Obtain access to depart");
                System.out.println("Plane with ID: " + rec.getPlane(count) + " Leaving");
                rec.setGatewayStatus(1);
                rec.notify();
                System.out.println(count +" current index");
                rec.resetAccess();
                System.out.println(count+ " current index");
            }
        }
    }

    Boolean runwayfree(){
        return rec.getGatewayStatus()==1;
    }
}
