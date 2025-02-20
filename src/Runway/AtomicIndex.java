package Runway;

import java.util.concurrent.locks.ReentrantLock;

public class AtomicIndex {
    private int value;
    private ReentrantLock lock=new ReentrantLock();
    AtomicIndex(int value){
        this.value=value;
    }
    public void set(int NewValue){
        lock.lock();
        try{
            this.value=NewValue;
        }finally {
            lock.unlock();
        }
    }

     public int get(){
        lock.lock();
        try{
            return value;
        }finally{
            lock.unlock();
        }
     }
     public void reset(int value){
        lock.lock();
         try{
             if(this.value==value){
                 this.value=-1;
             }else{
                 System.out.println("busy");
             }
         }finally{
             lock.unlock();
         }
     }
}
