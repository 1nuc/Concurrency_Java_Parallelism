package Runway;

public class SanityChecks{
    private Resources Res;
    SanityChecks(Resources shared){
        Res=shared;
    }

    public void output(){
        System.out.println("\n\nATC: All planes have departed. ATC shutting down.\n\n\n");
        System.out.println("Total Passengers Boarded: "+ Res.PassengersBoarded);

        calculateTime();
    }

    int CheckGate(){
        int count=0;
        for(int i=0; i<6; i++){
            if(Res.getGateNum(i)==0){
                count++;
            }
        }
        return count;
    }


    void ALlGateEmpty(){
        int number=CheckGate();
        if(number==6){
            System.out.println("All gates are empty.");
        }else{
            System.out.println("Some gates are not empty");
        }
    }


    void ALlPlanesServed(){
        if(Res.AllPlainDepart()){
            System.out.println("Planes Served: 6");
        }else{
            System.out.println("Some planes have not departed yet");
        }
    }

    private void calculateTime() {
        if (Res.WaitingTime.isEmpty()) {
            System.out.println("No planes recorded.");
            return;
        }

        long minTime = Res.WaitingTime.stream().min(Long::compare).orElse(0L);
        long maxTime = Res.WaitingTime.stream().max(Long::compare).orElse(0L);
        double avgerageTime = Res.WaitingTime.stream().mapToLong(Long::longValue).average().orElse(0.0);

        System.out.println("============STATISTICS==============");
        ALlGateEmpty();
        ALlPlanesServed();
        System.out.println("Maximum Plane Waiting Time: " + maxTime + " ms");
        System.out.println("Minimum Plane Waiting Time: " + minTime + " ms");
        System.out.println("Average Plane Waiting Time: " + avgerageTime + " ms");
    }


}
