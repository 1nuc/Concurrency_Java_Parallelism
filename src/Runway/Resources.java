package Runway;

import java.util.Random;

public class Resources {

    private String[] PlanesID;
    private int[] PlanesQ;
    private int GatewayStatus;
    private int passangers;
    private int access;
    //0 for availability 1 for occupation
    Resources(){
        PlanesQ= new int[6];
        PlanesID= new String[6];
        passangers=50;
        // Setting the number of planes to  6
    }

    int getPassangers(){
        return passangers;
    }
    void setPlanesID() {
        Random rand = new Random();
        for (int i = 0; i < PlanesID.length; i++) {
            String newID;
            int newQueue;
            do {
                newID = String.format("PL%04d", rand.nextInt(10000));
                newQueue = rand.nextInt(6) + 1;
            } while (isDuplicate(newID, newQueue));

            PlanesID[i] = newID;
            PlanesQ[i] = newQueue;
        }
    }

    void pop(int index){
        for (int i=0; i<PlanesID.length; i++){
            if(i ==index){
                PlanesQ[i]=0;
                break;
            }
        }
    }

    void resetAccess(){
        Random rand=new Random();
        do{
            access=rand.nextInt(PlanesQ.length);

        }while(PlanesQ[access]==0);

    }

    String getPlane(int index){
        return PlanesID[index];
    }

    boolean isDuplicate(String id, int queue) {
        for (int i = 0; i < PlanesID.length; i++) {
            if (id.equals(PlanesID[i]) || queue == PlanesQ[i]) {
                return true;
            }
        }
        return false;
    }

    String[] getPlanesID(){
        return PlanesID;
    }

    int[] getPlanesQ(){
        return PlanesQ;
    }

    void setAccess(){
        Random rand=new Random();
            access=rand.nextInt(PlanesQ.length);
    }

    int getAccess(){
        return access;
    }

    synchronized void setGatewayStatus(int status){
        if(status==2 || status==1)
                GatewayStatus=status;
        else
            throw new IllegalArgumentException("Invalid number entered status should be between 0 and 1");
    }

    int getGatewayStatus(){
        return GatewayStatus;
    }

}

