package Runway;

public class ATC_Countrol implements Runnable{
    @Override
    public void run(){
        Resources shared=new Resources();
        shared.setPlanesInfo();
        Thread[] PlanesThread=new Thread[6];
        for(int i=0; i< PlanesThread.length; i++){
            PlanesThread[i]=new Thread(new Airplane(i, shared), "Thread "+i);
        }
        // Confirm that all threads have started
        for (int i = 0; i < PlanesThread.length; i++) {
            System.out.println("Thread " + i + " state: " + PlanesThread[i].getState());
        }
        //Deadlock preventive measure
        // put the setPlanesInfo inside the class creation so all other threads have the same resources with
        // if the resource was shared all the threads will have the same access to every function or method which make them collide so then it becomes each thread is waiting for an action to happen and then the action never happen

        for(int i=0; i< PlanesThread.length; i++){
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            PlanesThread[i].start();
        }
    }
}
