package com.jorge.client;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.jorge.entity.Guide;

/**
 * Versioning strategy:
 * 
 * Implementing a business process that spans through multiple transactions should be done using the versioning strategy
 * to prevent lost updates
 * 
 * What is about if two users are trying to update the same data in DB?
 * The last update is who keeps in DB (i.e., data updated by User2) => Last commit wins!!!
 * We will not know if there had be another update before (i.e., data updated by User1)
 *
 * The solution to that is using @Version annotation in Guide entity:
 * 		Data updated by User1 => salary = 3000   version 1
 *      Data updated by User2 => Salary = 4000   version 2
 *      
 * This solution gets:
 * 		Last commit doesn't win
 * 		No lost updates
 *      
 * Hibernate is going to check for the version number at each update
 * 
 * An exception will be thrown, to prevent a lost update, if Hibernate doesn't find the in-memory version of an
 * entity to be same as the database version (current version): javax.persistence.OptimisticLockException (Check catch block)
 *
 * Optimistic locking is the official name of the Versioning strategy to prevent lost updates
 * 		Optimistic locking = No DataBase locking
 * 		Pessimistic locking = DataBase locking = could be used only within a single transaction
 * 
 */
public class User1 {

	public static void main(String[] args) {
		BasicConfigurator.configure(); // Necessary for configure log4j. It must be the first line in main method
	       					           // log4j.properties must be in /src directory

		Logger  logger = Logger.getLogger(User1.class.getName());
		logger.debug("log4j configured correctly and logger set");

		// How make the same things with JPA and Hibernate (commented)
		logger.debug("creating entity manager factory");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("helloworld"); // => SessionFactory sf = HibernateUtil.getSessionFactory(); HibernateUtil is a class created by us.
																						 // Persistence is imported from javax.persistence.Persistence package, it is not a class created by us
																						 // "helloworld" persistence unit name is the same name than "<persistence-unit name="helloworld"...>" element in persistence.xml file 
		
		logger.debug("creating entity manager em1");
		EntityManager em1 = emf.createEntityManager(); // => Session session = sf.openSession();
		
		logger.debug("getting em1 transaction");
		EntityTransaction txn = em1.getTransaction(); // => Transaction txn = session.getTransaction();
		
		logger.debug("beginning em1 transaction");
		txn.begin();
		
		logger.debug("getting guide info");
		Guide guide = em1.find(Guide.class, 1L);
		
		logger.debug("making em1 commit");
		txn.commit();
		
		logger.debug("close em1 entity manager");
		em1.close();
		
		logger.debug("setting guide salary with em1 closed");
		guide.setSalary(3000); // We need to merge this detached object
							   //
							   // A solution is extend the persistence context. It means: close entityManager at 
		                       // the end of the class after all instructions or in a finally block of a try/catch block => We dont' have to make a merge nor set CascadeType.MERGE in Guide.java class
							   //
							   // Another solution is doing what we are doing in this class: 
							   // merge (Guide mergedGuide = em2.merge(guide);) using another Entity Manager (em2) => We have to make a merge and set CascadeType.MERGE in Guide.java class
		
		//************************************
			
		logger.debug("creating entity manager em2");
		EntityManager em2 = emf.createEntityManager(); // => Session session = sf.openSession();
		
		logger.debug("getting em2 transaction");
		EntityTransaction txn2 = em2.getTransaction(); // => Transaction txn = session.getTransaction();
		
		try{
			logger.debug("beginning em2 transaction");
			txn2.begin();
			
			logger.debug("merging salary guide set above, with em2 open");
			Guide mergedGuide = em2.merge(guide); // Merging detached object. 'guide' is the detached object
			
			logger.debug("making em2 commit");
			txn2.commit();
			
		}
		catch (OptimisticLockException ole) {
			if (txn2 != null) {
				logger.error("something was wrong, making rollback of transactions");
				txn2.rollback(); // If something was wrong, we make rollback
				System.err.println("The guide was updated by some other user while you were doing interesting things.");
			}
		}
		finally{
			if(em2 != null){
				logger.debug("close em2 entity manager");
				em2.close();
			}
		}
	}

}
  