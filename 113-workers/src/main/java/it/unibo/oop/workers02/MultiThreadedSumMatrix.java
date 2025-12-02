package it.unibo.oop.workers02;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * sums elements of a matrix using multithreading.
 */
public class MultiThreadedSumMatrix implements SumMatrix {
    private final int nthread;

    /**
     * basic constructor.
     * 
     * @param n the no. of threads to use
     */
    public MultiThreadedSumMatrix(final int n) {
        nthread = n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        if (matrix.length == 0) {
            return 0;
        }
        final int len = matrix.length * matrix[1].length;
        // make matrix into a list so its easier to manage
        final double[] list = Arrays.stream(matrix)
            .flatMapToDouble(Arrays::stream)
            .toArray();
        final int step = Math.ceilDiv(len, nthread);
        return IntStream.range(0, nthread)
            .mapToObj(index -> new Worker(list, index * step, step))
            .peek(Thread::start)
            .peek(MultiThreadedSumMatrix::forceJoin)
            .mapToDouble(Worker::getResult)
            .sum();
    }

    /**
     * force main thread to wait for other threads.
     * 
     * @param target the thread to force to join
     */
    private static void forceJoin(final Thread target) {
        boolean joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (final InterruptedException ex) {
                ex.printStackTrace(); // NOPMD
            }
        }
    }

    /**
     * simple worker iterates over a portion of the list.
     * making it {@code static} yields marginally better performance.
     */
    private static class Worker extends Thread {
        private final double[] list;
        private final int start;
        private final int len;
        private double result;

        /**
         * builds a worker.
         * 
         * @param l the list to operate on
         * @param s the start index
         * @param nelem the number of elems to operate on
         */
        Worker(final double[] l, final int s, final int nelem) {
            list = l; // NOPMD: i am intentionally not copying the array
            //list = Arrays.copyOf(l, l.length); <- this is close to 10 times slower..
            start = s;
            len = nelem;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void run() {
            System.out.println("working from " + start + " to " + (start + len - 1)); // NOPMD
            for (int i = start; i < list.length && i < start + len; i++) {
                result += list[i];
            }
        }

        /**
         * returns result.
         * 
         * @return the result
         */
        public synchronized double getResult() {
            return this.result;
        }
    }
}
