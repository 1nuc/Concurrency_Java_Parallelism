package Runway;

public class GatesOperations extends Gateway implements Runnable{

    GatesOperations(int index, Resources shared) {
        super(index, shared);
    }

    public void run(){
        AssignedToGate();
    }
    void AssignedToGate(){
        synchronized(rec){
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Gate: "+rec.getGateNum(index)+" is open");
                System.out.println(Thread.currentThread().getName()+": Plane- "+rec.getSpecificPlane(index)+" Coasting to Gate "+rec.getGateNum(index));
                rec.SetPlaneStatus(index, "Assigned to Gate "+rec.getGateNum(index));
                System.out.println(Thread.currentThread().getName()+": Plane- "+rec.getSpecificPlane(index)+" docking at Gate "+rec.getGateNum(index));
                rec.setRunwayStatus(1);
            }
    }



}
