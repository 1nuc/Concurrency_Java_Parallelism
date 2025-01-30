package Runway;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Resources {

    private final String[] PlanesID;
    private int[] PlanesQ;
    private String[] PlanesStatus;
    private int GatewayStatus;
    //0 for availability 1 for occupation
    private final int passangers;
    private final String[] Gates;
    Semaphore semaphore=new Semaphore(3, true);

    Resources(){
        PlanesQ= new int[6];
        PlanesID= new String[6];
        passangers=50;
        Gates=new String[3];
        PlanesStatus=new String[6];
        // Setting the number of planes to  6
    }

    //Passenger functions
    int getPassangers(){
        return passangers;
    }


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
                PlanesStatus[i]="Waiting";
            } while (isDuplicate(newID, newQueue));
            //the point of planesQ is to display the number or the order if the emergency plain has a fuel shortage to land
            // first if the two gates are already occupied

            PlanesID[i] = newID;
            PlanesQ[i] = newQueue;
        }
    }

    String[] getPlanesID(){return PlanesID;}
    String getSpecificPlane(int index){return PlanesID[index];}

    int[] getPlanesQ(){return PlanesQ;}


//functions created to keep track of the planes coming and departing for emergency once
    void Plainland(int index){
        for (int i=0; i<PlanesID.length; i++){
            if(i ==index){
                PlanesQ[i]=0;
                break;
            }
        }

    }

    void Plaindepart(int index){
        for (int i=0; i<PlanesID.length; i++){
            if(i ==index){
                PlanesQ[i]=-1;
                break;
            }
        }
    }

    //Plane Status functionalities

    synchronized void SetPlaneStatus(int index,String Status){this.PlanesStatus[index]=Status;}

    synchronized String getSpecificPlaneStatus(int index){return PlanesStatus[index];}
    String[] getPlanesStatus(){return PlanesStatus;}


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
        if(status==2 || status==1)
                GatewayStatus=status;
        else
            throw new IllegalArgumentException("Invalid number entered status should be between 0 and 1");
    }

    int getRunwayStatus(){
        return GatewayStatus;
    }

}

