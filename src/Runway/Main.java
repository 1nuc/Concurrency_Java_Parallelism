package Runway;

public class Main {
        public static int count=0;

        public static void main(String[] args) {
            Gateway Plane1=new Gateway();
            //Creating six objects for each plane


            Thread[] PlanesThread=new Thread[6];

            for(int i=0; i< PlanesThread.length; i++){
                PlanesThread[i]=new Thread(Plane1);
            }

            for(int i=0; i< PlanesThread.length; i++){

                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                PlanesThread[i].start();
                count=i;
            }

        }
    }