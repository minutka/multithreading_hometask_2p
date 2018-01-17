import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Solver {
    private int[] array;
    private ArrayList<Integer> smallArray = new ArrayList<Integer>();
    ArrayList<Integer> bigArray = new ArrayList<Integer>();
    ArrayList<Integer> bigArrayCopy;
    ArrayList<Integer> result;
    int smallArrayCounter = 0;

    Solver(int[] array) {
        this.array = array;
    }

    void fill(){
        for (int i = 0; i < array.length; i++){
            array[i] = i+2;
        }
        for (int i = 0; i < Math.sqrt(array.length); i++) {
            smallArray.add(i+2);
        }
        for (int i = (int)Math.floor(Math.sqrt(array.length)); i < array.length; i++) {
            bigArray.add(i+2);
        }
        bigArrayCopy = new ArrayList<Integer>(bigArray);
    }

    void solve(){
        for (int i = 0; i < smallArray.size(); i++){
            for (int j = i+1 ; j < smallArray.size() ; j++){
                if ((smallArray.get(j) % smallArray.get(i)) == 0){
                    smallArray.remove(smallArray.get(j));
                }
            }
        }
    }

    void poolConfigurator(int M) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(M);
        for (int i = 0; i < M; i++) {
            es.execute(new oddThread(i,M));
        }
        es.shutdown();
        while(!es.awaitTermination(2L, TimeUnit.MINUTES)) {
            System.out.println("not yet...");
        }
    }


    void divideTo(int M) throws InterruptedException {
        int start = 0;
        int end = (int)Math.floor(bigArray.size() / M);
        Thread t;
        for(int i = 0; i < M; i++){
            t = new Thread(new newThread(start,end));
            t.start();
            t.join();
            start = end;
            end += end;
            end = (end < bigArray.size()) ? end : bigArray.size();
        }
        result = new ArrayList<Integer>();
        result.addAll(smallArray);
        result.addAll(bigArrayCopy);
    }

    class newThread implements Runnable {
        int start;
        int end;

        newThread(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void run() {
            System.out.println("Thread runs...");
            for (int i = 0 ; i < smallArray.size(); i++){
                for (int j = start; j < end; j++){
                    if ((bigArray.get(j) % smallArray.get(i)) == 0){
                        bigArrayCopy.remove(bigArray.get(j));
                    }
                }
            }
        }
    }

    void oddDivideTo(int M) throws InterruptedException {
        Thread t;
        for(int i = 0; i < M; i++) {
            t = new Thread(new oddThread(i,M));
            t.start();
            t.join();
        }
        result = new ArrayList<Integer>();
        result.addAll(smallArray);
        result.addAll(bigArrayCopy);
    }


    class oddThread implements Runnable {
        int tn;
        int ths;

        public oddThread(int tn, int ths) {
            super();
            this.tn = tn;
            this.ths = ths;
        }

        public void run() {
            System.out.println((tn + 1) + "nd/th thread starts...");
            for (int i = 0 ; i < smallArray.size(); i++){
                for (int j = tn; j < bigArray.size(); j += ths){
                    if ((bigArray.get(j) % smallArray.get(i)) == 0){
                        bigArrayCopy.remove(bigArray.get(j));
                    }
                }
            }
        }
    }

    void syncDivide(int M) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(M);
        for (int i = 0; i < M; i++) {
            es.execute(new syncThread(i));
        }
        es.shutdown();
        while(!es.awaitTermination(2L, TimeUnit.MINUTES)) {
            System.out.println("not yet...");
        }
    }

    int getSmallArrayCounter(){
        synchronized (this) {
            return smallArrayCounter;
        }
    }

    class syncThread implements Runnable {
        int tn;

        public syncThread(int tn) {
            super();
            this.tn = tn;
        }

        public void run() {
            System.out.println((tn + 1) + "nd/th thread starts...");
            while(getSmallArrayCounter() < smallArray.size()) {
                for (int i = 0; i < bigArray.size(); i++){                                      //using foreach thrown ConcurrentModificationException
                    if ((bigArray.get(i) % smallArray.get(getSmallArrayCounter())) == 0){       // << using element for foreach instead of bigArray.get(i)
                        bigArray.remove(bigArray.get(i));                                       // https://habrahabr.ru/post/325426/
                    }
                }
                smallArrayCounter++;
            }
        }
    }
}
