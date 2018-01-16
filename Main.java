public class Main {
    public static void main(String[] args) throws InterruptedException {
        int N = 1000;
        int M = 4;
        int[] array = new int[N];
        Solver solver = new Solver(array);
        double start = System.nanoTime();
        solver.fill();
        solver.solve();
        //solver.divideTo(M);
        solver.oddDivideTo(M);
        //solver.poolConfigurator(M);
        for (int i : solver.bigArrayCopy){
            System.out.println(i);
        }
        double end = System.nanoTime();
        System.out.println("Time spent: " + ((end - start) / 1000000000));
    }
}
