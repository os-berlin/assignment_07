package com.coderscampus.assignment;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadingApplication {

	public static void main(String[] args) {		
		
// step 1 -> fetch the data
		ExecutorService ioBoundTask = Executors.newCachedThreadPool();		
		
		List<Integer> syncList = Collections.synchronizedList(new ArrayList<>(1000000));
		Assignment8 assignment = new Assignment8();
		
		System.out.println("processing data: \n");
		
		for (int i=0; i<1000; i++) {
			CompletableFuture.supplyAsync(assignment::getNumbers, ioBoundTask)
																 	
								/* all "starting to fetch records" threads seem to happen in perfect order
								 * 	(when using a cachedThreadPool) while the "done fetching" threads are 
								 *	all over the place every time.
								 *  Any idea why that might be the case? Because of the "synchronized" tag
								 *	in the "getNumbers()" method?
								 */	
			 				 .thenAcceptAsync(list -> syncList.addAll(list), ioBoundTask);
								// I can't use the object::method syntax with parameterized methods, can I?			
		}
		
		while (syncList.size() != 1000000) {
			//wait for threads to finish
		}
		
		System.out.print("\nitems in array: " + syncList.size());   
		if(syncList.size() == 1000000) {
			System.out.println(" -> job done!");
		} else System.out.println(" -> something is not right..");
		
		 
		/* 	for verifying the array's content, I created this little for-loop and I realized that the last 10
		 *	items (and all the others as well I assume) keep changing around.. I guess because of the 
		 *	multi-threaded mayhem going on. Is that OK?
		 */	
//		for (int i = 999990; i < 1000000; i++ ) {
//			System.out.print(syncList.get(i) + ", ");			
//		}
		
		System.out.println("\n-----------\n");
   
// step 2 -> count the numbers

		Long sum = 0L;
		System.out.println("count of individual numbers: \n");
		for (int i = 0; i <= Collections.max(syncList); i++) {
			System.out.print(i + ": " + countIndividualNumbers(syncList, i) + ", ");
			sum += countIndividualNumbers(syncList, i);
		}
		System.out.println("sum: " + sum);
						
	}

	private static long countIndividualNumbers(List<Integer> syncList, Integer number) {
		return syncList.stream()
					   .filter(x -> x.equals(number))
				   	   .count();
	}

}
