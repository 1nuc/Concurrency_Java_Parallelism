package Runway;

public class SanityChecks{
    private Resources Res;
    SanityChecks(Resources shared){
        Res=shared;
    }

    public void output(long totalTime){

        calculateTime(totalTime);
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
            System.out.println("Planes Served: "+Res.getPlanesID().length);
        }else{
            System.out.println("Some planes have not departed yet");
        }
    }

    private void calculateTime(long totalTime) {
        if (Res.WaitingTime.isEmpty()) {
            System.out.println("No planes recorded.");
            return;
        }

        long minTime = Res.WaitingTime.stream().min(Long::compare).orElse(0L) / 1000; // Convert to sec
        long maxTime = Res.WaitingTime.stream().max(Long::compare).orElse(0L) / 1000; // Convert to sec
        double averageTime = Res.WaitingTime.stream().mapToLong(Long::longValue).average().orElse(0.0) / 1000; // Convert to sec
        long totalWaitingTime = Res.WaitingTime.stream().mapToLong(Long::longValue).sum() / 1000;

        System.out.println("\n\n============STATISTICS==============");
        System.out.println("ATC: All planes have departed. ATC shutting down.\n");
        ALlGateEmpty();
        ALlPlanesServed();
        System.out.println("Total Passengers Boarded: " + Res.PassengersBoarded);
        System.out.println("Total Simulation Time: " + totalTime + " sec");
        System.out.println("Total Waiting Time (All Planes): " + totalWaitingTime + " sec");
        System.out.println("Maximum Plane Waiting Time: " + maxTime + " sec");
        System.out.println("Minimum Plane Waiting Time: " + minTime + " sec");
        System.out.println("Average Plane Waiting Time: " + String.format("%.2f", averageTime) + " sec");
    }


}
