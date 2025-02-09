package Runway;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Resources {

    private final String[] PlanesID;
    private int[] PlanesQ;
    private int GatewayStatus;
    private final String[] Gates;
    ArrayList<Integer> WaitingQueue;
    ArrayList<Integer> DepartingQueue;
    protected final Object LandingObject=new Object();
    protected final Object DepartLock=new Object();
    protected final Object RunwayLock=new Object();
    protected final Object DepartingObject=new Object();
    CustomSemaphore semaphore=new CustomSemaphore(3);
    AtomicIndex atomicIndex=new AtomicIndex(-1);
    Resources(){
        PlanesQ= new int[6];
        PlanesID= new String[6];
        Gates=new String[3];

        WaitingQueue=new ArrayList<>();
        DepartingQueue=new ArrayList<>();

        // Setting the number of planes to  6
    }

    // Queues functionalities
    void Add_Planes_Queue(int index){
        WaitingQueue.add(index);
    }
    void Rem_Planes_Queue(int index){
        WaitingQueue.remove(Integer.valueOf(index));
    }

    void Add_Planes_Departing_Queue(int index){
        DepartingQueue.add(index);
    }
    void Rem_Planes_Departing_Queue(int index){
        DepartingQueue.remove(Integer.valueOf(index));
    }

    //Passenger functions
        //Boarding--


    //A function to make ensure IDs and queues uniqueness
    boolean isDuplicate(String id, int queue) {
        for (int i = 0; i < PlanesID.length; i++) {
            if (id.equals(PlanesID[i]) || queue == PlanesQ[i]) {
                return true;
            }
        }
        return false;
    }

    //Planes Operations

    synchronized void setPlanesInfo() {

        Random rand = new Random();
        for (int i = 0; i < PlanesID.length; i++) {
            String newID;
            int newQueue;
            do {
                newID = String.format("PL%04d", rand.nextInt(10000));
                newQueue = rand.nextInt(6) + 1;
            } while (isDuplicate(newID, newQueue));
            //the point of planesQ is to display the number or the order if the emergency plain has a fuel shortage to land
            // first if the two gates are already occupied

            PlanesID[i] = newID;
            PlanesQ[i] = newQueue;
        }
    }

    String[] getPlanesID(){return PlanesID;}
    synchronized String getSpecificPlane(int index){return PlanesID[index];}

    int[] getPlanesQ(){return PlanesQ;}


//functions created to keep track of the planes coming and departing for emergency once
   synchronized void Plainland(int index){
        for (int i=0; i<PlanesID.length; i++){
            if(i ==index){
                PlanesQ[i]=0;
                break;
            }
        }

    }

    boolean LandingPrem(int index){
        if(PlanesQ[index]==0)return true;
        else return false;
    }

    boolean DepartingPrem(int index){
        if(PlanesQ[index]==-1)return true;
        else return false;
    }


    void Plaindepart(int index){
        for (int i=0; i<PlanesID.length; i++){
            if(i ==index){
                PlanesQ[i]=-1;
                break;
            }
        }
    }

    boolean AllPlainDepart(){
        int count=0;
        for(int i=0; i<PlanesQ.length; i++){
            if(PlanesQ[i]==-1){
                count++;
            }
        }
        if(count==6){
            return true;
        }else{
            return false;
        }
    }

    //Plane Status functionalities



    //Gates Functions

    int getGateNum(int index){
        for(int i=0; i<getGates().length; i++){
            if((getGate(i) != null && getGate(i).equals(getSpecificPlane(index)))){
                return i+1;
            }
        }
        return 0;
    }

    String getGate(int index){return Gates[index];}

    void setGate(int index, String gate){this.Gates[index]=gate;}


    String [] getGates(){return Gates;}
//Runway functions

    synchronized void setRunwayStatus(int status){
        if(status==0 || status==1)
                GatewayStatus=status;
        else
            throw new IllegalArgumentException("Invalid number entered status should be between 0 and 1");
    }

    int getRunwayStatus(){
        return GatewayStatus;
    }

}

