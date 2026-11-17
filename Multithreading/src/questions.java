
public class questions {
	/*
	 * (1a) What does the printPrimes method do?
	 * The printPrimes method starts at a given BigInteger value and
	 * checks a range of numbers to see if each one is a prime using
	 * n.isProbablePrime(100). Whenever a number is prime, it prints
	 * it to the console. After checking the given range, the method
	 * prints a message showing that the current thread is finished.
	 */

	/*
	 * (1b) What happens when the program executes two threads?
	 * When the program runs, both threads (r1 and r2) start executing
	 * at roughly the same time. Each thread independently checks and
	 * prints prime numbers in its assigned range.
	 */

	/*
	 * (1d) What does the program print, and does it print the same thing
	 * each time?
	 * The program prints all prime numbers within the specified ranges
	 * for both threads, followed by messages that each thread has exited.
	 */

	/*
	 * (1e) Comparing execution with different thread pool sizes:
	 * When using Executors.newFixedThreadPool(2):
	 *   - Both threads run in parallel, so the total runtime is shorter.
	 *   - The counts for both ranges are printed almost simultaneously.
	 *
	 * When using Executors.newFixedThreadPool(1):
	 *   - Only one thread can run at a time, so the second task must wait
	 *     until the first finishes.
	 *   - This causes a noticeable delay between the two printouts, and
	 *     the overall runtime roughly doubles.
	 *
	 * When using Executors.newFixedThreadPool(3):
	 *   - There are enough threads to handle both tasks concurrently, but
	 *     the third thread doesn’t improve performance much since there
	 *     are still only two major tasks.
	 */

	/*
	 * (2a) What happens when incrementing a shared counter (nonprime)
	 * without synchronization?
	 * When both threads increment the shared variable 'nonprime' at the
	 * same time, race conditions occur. This causes inconsistent results:
	 * the value of 'nonprime' changes unpredictably between runs.
	 */

	/*
	 * (2b) Using a ReentrantLock to fix the problem:
	 * By using a ReentrantLock around the increment operation, only one
	 * thread can modify 'nonprime' at a time. This ensures that every
	 * increment is counted correctly.
	 */
	
	/*
	 * (2c) Using a synchronized Counter class:
	 * Instead of manually using a lock, we can make a Counter class with
	 * synchronized methods. The object’s built-in lock ensures that only
	 * one thread can access increment() or get() at a time.

	 * (3a) Producer/Consumer problem explanation:
	 * The producer threads (producePrimes) generate prime numbers and
	 * place them into a shared BlockingQueue using queue.put(). 
	 *
	 * The consumer thread (consumePrimes) removes numbers from the queue
	 * using queue.take() and prints those primes that have at most three
	 * distinct digits. 
	/*

	 * (3b) How does the consumer know when it is done?
	 *The producers can insert a special "poison pill" value (like null
	 * or BigInteger.ZERO) into the queue to signal the consumer to stop.
	 */
}
