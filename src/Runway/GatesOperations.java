package Runway;

public class  GatesOperations extends Gateway implements Runnable {

    GatesOperations(int index, Resources shared) {
        super(index, shared);
    }

    public void run() {
        AssignedToGate();
    }

    void AssignedToGate() {
        synchronized (rec) {
            while (!canCoast() || GateStatus()) {
                try {
                    rec.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                rec.setRunwayStatus(1);
                System.out.println("Available Gates: " + rec.semaphore.availablePermits());
                rec.semaphore.acquire();
                for (int i = 0; i < rec.getGates().length; i++) {
                    if (rec.getGate(i) == null) {
                        rec.setGate(i, rec.getSpecificPlane(index));
                        System.out.println("Gate: " + (i + 1) + " is open");
                        System.out.println("Current Index " + index);
                        System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " Coasting to Gate " + (i + 1));
                        rec.SetPlaneStatus(index, "Assigned to Gate " + (i + 1));
                        break;
                    }
                }
                System.out.println(Thread.currentThread().getName() + ": Plane- " + rec.getSpecificPlane(index) + " docking at Gate " + rec.getGateNum(index));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                rec.notifyAll();
            }
        }
    }
}