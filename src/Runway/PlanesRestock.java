package Runway;

public class PlanesRestock implements Runnable{
    private int index;
    private Resources rec;
    private PlaneOperations planeOps;

    PlanesRestock(int index, Resources shared, PlaneOperations planeOps) {
        this.index = index;
        this.rec = shared;
        this.planeOps = planeOps;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": Plane-" + rec.getSpecificPlane(index)+" refills supplies successfully");

    }
}
