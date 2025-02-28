package Runway;

public class PlaneCleaning implements Runnable{
    private int index;
    private Resources rec;
    private PlaneOperations planeOps;

    PlaneCleaning(int index, Resources shared, PlaneOperations planeOps) {
        this.index = index;
        this.rec = shared;
        this.planeOps = planeOps;
    }

    @Override
    public void run(){
        System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" Successfully cleaned");

    }
}
