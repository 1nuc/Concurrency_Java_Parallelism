import java.util.concurrent.locks.ReentrantLock;

import static PlanesBehavoir.PlanesArrival.counter;

public class Gateway extends Resources implements Runnable{
    private int count;
    private final Resources rec;
    Gateway(){
        this.rec=new Resources();
        rec.setPlanesID();
        rec.setAccess();
    }
    public void run(){
        System.out.println("Thread "+Thread.currentThread().getName()+" Is Running");
        System.out.println("Plane with ID: "+rec.getPlane(rec.getAccess())+" want to land");
        try{
            if(count==0){
                count++;
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
                    System.out.println("Plane with ID: " + rec.getPlane(rec.getAccess()) + " Waiting to land");
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
                System.out.println("Plane with ID: " + rec.getPlane(rec.getAccess()) + " Obtain access to land");
                System.out.println("Plane with ID: " + rec.getPlane(rec.getAccess()) + " Landing");
                count ++;
                rec.notify();
            }
        }
    }

    void departing(){
        synchronized (rec) {
            if (runwayfree()) {
                try {
                    System.out.println("Plane with ID: " + rec.getPlane(rec.getAccess()) + " Waiting to leave");
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
                System.out.println("Plane with ID: " + rec.getPlane(rec.getAccess()) + " Obtain access to depart");
                System.out.println("Plane with ID: " + rec.getPlane(rec.getAccess()) + " Leaving");
                count--;
                rec.notify();
                System.out.println(count);
                rec.resetAccess();
                System.out.println(count);
            }
        }
    }

    Boolean runwayfree(){
        return count==1;
    }
}
