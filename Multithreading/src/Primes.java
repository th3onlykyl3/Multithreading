import java.math.BigInteger;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;


public class Primes {

    private long nonprime = 0; 
    private final ReentrantLock lock = new ReentrantLock(); 
    private final Counter counter = new Counter(); 

    public Primes() throws InterruptedException, ExecutionException {

        System.out.println("\n STEP 1: Multiple Threads ");

        ExecutorService service = Executors.newFixedThreadPool(2);

       
        Runnable r1 = printPrimes(0, new BigInteger("1000000000000000"), 10000);
        Runnable r2 = printPrimes(1, new BigInteger("2000000000000000"), 10000);

        service.execute(r1);
        service.execute(r2);

        
        Callable<Long> c1 = countPrimes(new BigInteger("1000000000000000"), 500_000);
        Callable<Long> c2 = countPrimes(new BigInteger("2000000000000000"), 500_000);

        
        long start = System.currentTimeMillis();
        Future<Long> f1 = service.submit(c1);
        Future<Long> f2 = service.submit(c2);
        long result1 = f1.get();
        long result2 = f2.get();
        long end = System.currentTimeMillis();

        System.out.println("Prime count 1: " + result1);
        System.out.println("Prime count 2: " + result2);
        System.out.println("Run time in ms: " + (end - start));

        System.out.println("\n  STEP 2: Shared Counter and Synchronization ");

        nonprime = 0;
        service = Executors.newFixedThreadPool(2);
        Runnable countTask = () -> {
            BigInteger n = new BigInteger("1000000000000000");
            for (long i = 0; i < 500_000; i++) {
                if (!n.isProbablePrime(100)) {
                    lock.lock();
                    try {
                        nonprime++;
                    } finally {
                        lock.unlock();
                    }

                   
                    counter.increment();
                }
                n = n.add(BigInteger.ONE);
            }
        };

        service.execute(countTask);
        service.execute(countTask);
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Nonprimes counted (with lock): " + nonprime);
        System.out.println("Nonprimes counted (with Counter object): " + counter.get());


        System.out.println("\n STEP 3: Producer / Consumer ");

        BlockingQueue<BigInteger> queue = new ArrayBlockingQueue<>(100);
        ExecutorService pcService = Executors.newFixedThreadPool(3);

        Runnable producer1 = producePrimes(new BigInteger("1000000000000000"), 500_000, queue);
        Runnable producer2 = producePrimes(new BigInteger("2000000000000000"), 500_000, queue);
        Runnable consumer = consumePrimes(queue);

        pcService.execute(producer1);
        pcService.execute(producer2);
        pcService.execute(consumer);

        pcService.shutdown();
        pcService.awaitTermination(3, TimeUnit.MINUTES);

        System.out.println("Producer/Consumer complete.\n");
    }

    private Runnable printPrimes(int instance, BigInteger start, long length) {
        return () -> {
            BigInteger n = start;
            for (long i = 0; i < length; i++) {
                if (n.isProbablePrime(100))
                    System.out.println("Instance " + instance + ": " + n);
                n = n.add(BigInteger.ONE);
            }
            System.out.println("Instance " + instance + " thread exiting.");
        };
    }

    private Callable<Long> countPrimes(BigInteger start, long length) {
        return () -> {
            long count = 0;
            BigInteger n = start;
            for (long i = 0; i < length; i++) {
                if (n.isProbablePrime(100)) count++;
                n = n.add(BigInteger.ONE);
            }
            return count;
        };
    }

    private Runnable producePrimes(BigInteger start, long length, BlockingQueue<BigInteger> queue) {
        return () -> {
            try {
                BigInteger n = start;
                for (long i = 0; i < length; i++) {
                    if (n.isProbablePrime(100)) queue.put(n);
                    n = n.add(BigInteger.ONE);
                }
              
                queue.put(BigInteger.valueOf(-1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    private Runnable consumePrimes(BlockingQueue<BigInteger> queue) {
        return () -> {
            try {
                while (true) {
                    BigInteger prime = queue.take();
                    if (prime.equals(BigInteger.valueOf(-1))) break; 
                    String digits = prime.toString();
                    String distinctDigits = distinct(digits);
                    if (distinctDigits.length() <= 3)
                        System.out.println("Consumed prime with ≤3 distinct digits: " + prime);
                }
                System.out.println("Consumer done.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    private String distinct(String s) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            int cp = s.codePointAt(i);
            int cc = Character.charCount(cp);
            if (result.indexOf(s.substring(i, i + cc)) == -1)
                result.appendCodePoint(cp);
            i += cc;
        }
        return result.toString();
    }

    private static class Counter {
        private long value = 0;
        public synchronized void increment() { value++; }
        public synchronized long get() { return value; }
    }

    public static void main(String[] args) {
        try {
            new Primes();
            System.out.println("Main thread exiting.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

