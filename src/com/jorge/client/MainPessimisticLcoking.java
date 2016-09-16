package com.jorge.client;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Versioning strategy:
 * 
 * Implementing a business process that spans through multiple transactions should be done using the versioning strategy
 * to prevent lost updates
 * 
 * What about if two users are trying to update the same data in DB?
 * The last update is what keeps in DB (i.e., data updated by User2) => Last commit wins!!!
 * We will not know if there have been another updates before (i.e., data updated by User1)
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
 * Optimistic locking is the official name of the versioning strategy to prevent lost updates
 * 		Optimistic locking = No DataBase locking
 * 		Pessimistic locking = DataBase locking = could be used only within a single transaction
 * 
 * 		You must use versioning strategy (optimistic locking) to prevent lost updates when implementing a conversation (multiple transactions/[request/response cycles])
 * 		You must use pessimistic locking (database locking) only when you've got multiple database queries being executed on the same data, within a single transactions
 * 
 * 
 */
public class MainPessimisticLcoking {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		BasicConfigurator.configure(); // Necessary for configure log4j. It must be the first line in main method
	       					           // log4j.properties must be in /src directory

		Logger  logger = Logger.getLogger(MainPessimisticLcoking.class.getName());
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
		// setLockMode(LockModeType.PESSIMISTIC_READ) => Between this query above and the next query below, data can be updated by other users, displayin wrong data. To avoid that,
		// we must set a lock mode as PESSIMISTIC_READ in createQuery() method => placing a READ lock on the data being read
		List<Object[]> resultList = em1.createQuery("select guide.name, guide.salary from Guide as guide").setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
		
		for(Object[] objects : resultList){
			System.out.println("Name: " + objects[0] + ", Salary: " + objects[1]);
		}
		
		long sumOfSalaries = (long)em1.createQuery("select sum(guide.salary) from Guide as guide").getSingleResult();
		System.out.println("The total salary of all the guides is " + sumOfSalaries);
		
		logger.debug("making em1 commit");
		txn.commit();
		
		logger.debug("close em1 entity manager");
		em1.close();
		
		//********************************************************
		
		logger.debug("creating entity manager em2");
		EntityManager em2 = emf.createEntityManager(); // => Session session = sf.openSession();
		
		logger.debug("getting em2 transaction");
		EntityTransaction txn2 = em2.getTransaction(); // => Transaction txn = session.getTransaction();
		
		logger.debug("beginning em2 transaction");
		txn2.begin();
		
		logger.debug("getting guide info");
		// setLockMode(LockModeType.PESSIMISTIC_WRITE) => placing a WRITE lock, to modify (in query 'update Guide as guide...' below) the data being read
		resultList = em2.createQuery("select guide.name, guide.salary from Guide as guide").setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
		
		for(Object[] objects : resultList){
			System.out.println("Name: " + objects[0] + ", Salary: " + objects[1]);
		}
		
		sumOfSalaries = (long)em2.createQuery("select sum(guide.salary) from Guide as guide").getSingleResult();
		System.out.println("The total salary of all the guides is " + sumOfSalaries);
		
		em2.createQuery("update Guide as guide set guide.salary = guide.salary*4").executeUpdate(); // Updating all guides by raising their salaries 4 times
		
		logger.debug("making em2 commit");
		txn2.commit();
		
		logger.debug("close em2 entity manager");
		em2.close();
	}

}
  