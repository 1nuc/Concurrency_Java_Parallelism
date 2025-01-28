    public class Main {
        public static void main(String[] args) {
            Gateway Plane1=new Gateway();
            //Creating six objects for each plane

            Thread thread1=new Thread(Plane1);
            Thread thread2=new Thread(Plane1);
            Thread thread3=new Thread(Plane1);
            Thread thread4=new Thread(Plane1);
            Thread thread5=new Thread(Plane1);
            Thread thread6=new Thread(Plane1);

            try{
                Thread.sleep(500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();
            thread5.start();
            thread6.start();
        }
    }