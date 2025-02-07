package Runway;

public class Main {

        public static void main(String[] args) {
            //Creating six objects for each plane
            ATC_Countrol atc= new ATC_Countrol();
            Thread ATC_Thread=new Thread(atc, "ATC-Thread");

            ATC_Thread.start();


        }
    }