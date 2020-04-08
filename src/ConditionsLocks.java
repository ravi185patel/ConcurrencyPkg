
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/*
ReentrantLock behaves like synchronized and you might wonder when it's appropriate to use one or the other.
Use ReentrantLock when you need timed or interruptible lock waits, non-block-structured locks
(obtain a lock in one method; return the lock in another),  multiple condition variables, or lock polling.
Furthermore, ReentrantLock supports scalability and is useful where there is high contention among threads.
If none of these factors come into play, use synchronized.
 */
class DataQueue{
    private final Queue<Integer> task= new LinkedList<>();
    private ReentrantLock lock;
    private Condition added,removed;

    public DataQueue(){
        /*
         ReentrantLock(true) /fair -> Equals chance for all threads (slower)
         ReentrantLock() /Unfair -> get chance in which order thread comes (Possible thread starvation th2-long th3-2ms then th3- need to
         wait long time).
         */
        this.lock=new ReentrantLock(); // new ReentrantLock(true)
        this.added=lock.newCondition();
        this.removed=lock.newCondition();

    }

    public void addData(int no){
        task.add(no);
    }

    public int getData(){
        return task.remove();
    }
    public int size(){
        return task.size();
    }

    public Integer consume() throws InterruptedException{
        lock.lock();
        /*lock.lock();
        lock.lock();
        /*
            ReentrantLock allow threads to enter into lock on a resource more than once.
            When the thread first enters into lock, a hold count is set to one.
            Before unlocking the thread can re-enter into lock again and every time hold count is incremented by one.
            For every unlock request, hold count is decremented by one and when hold count is 0, the resource is unlocked.
         */

        /*
        acquires a lock only when it's free at the time of invocation.
        This method returns true when the lock is acquired; otherwise, it returns false.
        Scenario -> already we acquired lock;
        */
//        lock.tryLock(5, TimeUnit.MILLISECONDS);
        /*
        is similar to boolean tryLock();
        however, it lets you specify an amount of time to wait for the lock to become available.
        Pass the magnitude of the delay to time and the units represented by this delay to unit.
        Scenario -> Acquired lock takes more time to execute;
         */
        try{
            System.out.println(lock.isHeldByCurrentThread()+" <consume-fair> "+lock.isFair());
            while(size()==0)
                added.await();
            Integer data=getData();
            removed.signalAll();
            return data;
        }
        finally {
            lock.unlock();
        }
    }
    public void produce() throws InterruptedException{
        lock.lock();
        try{
            System.out.println(lock.isHeldByCurrentThread()+" <producer-fair> "+lock.isFair());
            while(size()==100)
                removed.await();
            addData(size());
            added.signalAll();
        }
        finally {
            lock.unlock();
        }
    }
}
class Producer implements Runnable{
    private  DataQueue dataQueue;
    public Producer(DataQueue dataQueue){
        this.dataQueue=dataQueue;
    }

    @Override
    public void run(){
        try {
            for(int i=0;i<100;i++)
                this.dataQueue.produce();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable{
    private  DataQueue dataQueue;
    public Consumer(DataQueue dataQueue){
        this.dataQueue=dataQueue;
    }

    @Override
    public void run(){
        try {
            for(int i=0;i<100;i++)
                System.out.println(this.dataQueue.consume());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public class ConditionsLocks {
    public static void main(String[] args){
        DataQueue dataQueue=new DataQueue();
        Producer p=new Producer(dataQueue);
        Consumer c=new Consumer(dataQueue);
        Thread t1=new Thread(p);
        Thread t2=new Thread(c);
        t1.start();
        t2.start();
    }
}
