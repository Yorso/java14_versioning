package com.jorge.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

// MANY SIDE entity => OWNER of this bidirectional relationship
// The owner of the relationship is responsible for the association column(s) update

@Entity
//@Table(name="student") // If we comment @Table annotation, Hibernate will create a table with the name of this calss => "Student" (CASE SENSITIVE!!!)
public class Student {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@Column(name="enrollment_id", nullable=false)
	private String enrollmentId;
	
	//@Column(name="name") // If we comment @Column annotation, Hibernate will create this column with the name of this attribute => "name" (CASE SENSITIVE!!!)
	private String name;
	
	// By default, single point associations (@OneToOne and @ManyToOne) are eagerly fetched
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.REMOVE}) // Many students to one guide. 
																  // CascadeType.PERSIST: Everything you change in student row is save in its linked guide row automatically
																  // CascadeType.REMOVE: If you delete a student row, it will delete its linked guide row automatically
	@JoinColumn(name="guide_id") // guide_id is the name of the field in student table, linked to id field in guide table
							     // You say with @JoinColumn annotation that this class is the owner of the relationship
	private Guide guide;
	
	public Student() {}
	
	public Student(String enrollmentId, String name, Guide guide) {
		this.enrollmentId = enrollmentId;
		this.name = name;
		this.guide = guide;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(String enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Guide getGuide() {
		return guide;
	}

	public void setGuide(Guide guide) {
		this.guide = guide;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", enrollmentId=" + enrollmentId + ", name=" + name + ", guide=" + guide + "]";
	}
	
	
	/**
	 * Equals and hascode: to compare objects
	 * 
	 * If your entity (Student in this case) is part of a Set collection, you must override its equal and hashcode methods
	 * 
	 * Student is Set collection in Guide.java class (private Set<Student> students = new HashSet<Student>();), so we override these methods here
	 * 
	 * If two objects are equal, their hashcode must also be equal. So if you implement equals(Object obj), you must also implement hashCode()
	 * 
	 * We have two equal objects:
	 * 		Student student1 = new Student("22", "Homer");
	 * 		Student student2 = new Student("22", "Homer");
	 * 
	 * We could make things like:
	 * 		student1.equals(student2)) => true
	 * 
	 * 		First: Set<Student> students = new HashSet<Student>(); Then: students.add(student1); And then: students.contains(student2)); => true
	 * 	
	 * 		student1.hashcode() == student2.hashcode() => true
	 * 
	 * If we don't override these methods, all results above would be false. Comment these methods to see what happens
	 * 
	 * We need commons-langX-X.X.X.jar
	 * 
	 */
	@Override
	public int hashCode(){
		return new HashCodeBuilder().append(enrollmentId).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Student)) return false;
		Student other = (Student) obj;
		return new EqualsBuilder().append(enrollmentId, other.enrollmentId).isEquals();
	}

}
