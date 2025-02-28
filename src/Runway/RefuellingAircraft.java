package Runway;

public class RefuellingAircraft implements Runnable{
    private int index;
    private Resources rec;
    private PlaneOperations planeOps;

    RefuellingAircraft(int index, Resources shared, PlaneOperations planeOps) {
        this.index = index;
        this.rec = shared;
        this.planeOps = planeOps;
    }

    public void run(){
        System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index) + " Refuelling ....");
        System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" refuelled successfully");
    }
}
