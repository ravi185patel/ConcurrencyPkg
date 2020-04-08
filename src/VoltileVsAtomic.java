import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

class Voletile{
    boolean flag;
    volatile int counter; // solve visibility problem but create synchronized problem ( solution make method synchronized)
    int noCounter; // visibility problem during threading
    AtomicInteger atomicInteger=new AtomicInteger(1); // alternative for volatile with synchronized method

    public int getNoCounter() {
        return noCounter;
    }

    public void setNoCounter() {
        ++this.noCounter;
    }

    public int getAtomicInteger(){
        return atomicInteger.incrementAndGet();
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter() {
        ++this.counter;
    }

    public Voletile(){
        flag=false;
    }
    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
class Example implements Runnable{
    private Voletile voletile;

    public Example(Voletile voletile){
        this.voletile=voletile;
    }
    public void run(){
//        System.out.println(Thread.currentThread().getName());
        setFlag(Thread.currentThread().getName());
        System.out.println("end");
    }

//    synchronized
     public synchronized void setFlag(String name){
         /* normal counter*/
         System.out.println(voletile.getNoCounter()+" "+name);
         voletile.setNoCounter();
         System.out.println(voletile.getNoCounter()+" "+name);
         /**/
         System.out.println(voletile.isFlag()+" "+voletile.getCounter()+" "+name);
         voletile.setCounter();
         System.out.println(voletile.isFlag()+" "+voletile.getCounter()+" "+name);
         /**/
         System.out.println(voletile.isFlag()+" "+voletile.getAtomicInteger()+" "+name);
         System.out.println(voletile.isFlag()+" "+voletile.getAtomicInteger()+" "+name);
    }
}

public class VoltileVsAtomic {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor=(ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        Voletile voletile=new Voletile();
        Thread t1=new Thread(new Example(voletile),"t1");
        Thread t2=new Thread(new Example(voletile),"t2");
//        threadPoolExecutor.submit(t1); // submit for callable interface
//        threadPoolExecutor.submit(t2);
//        for(int i=0;i<10;i++) {
            threadPoolExecutor.execute(t1); // for runnable
            threadPoolExecutor.execute(t2);
//        }
        threadPoolExecutor.shutdown();

    }
}
