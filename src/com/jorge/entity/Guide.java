package com.jorge.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

// ONE SIDE entity => INVERSE END

@Entity
@Table(name="guide")
public class Guide {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@Column(name="staff_id", nullable=false)
	private String staffId;
	
	@Column // Hibernate will create this column with the name of this attribute => "name" (CASE SENSITIVE!!!)
	private String name;
	
	@Column(name="salary")
	private Integer salary;
	
	@Version // Used to take a control about changes in Guide DB
	private Integer version;
	
	// By default, @OneToMany and @ManyToMany associations are lazy
	@OneToMany(mappedBy="guide", cascade={CascadeType.MERGE}) // CascadeType.MERGE is necessary to solve detached objects merging (check main classes)
	private Set<Student> students = new HashSet<Student>();
	
	public Guide() {}
	
	public Guide(String staffId, String name, Integer salary) {
		this.staffId = staffId;
		this.name = name;
		this.salary = salary;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	// This makes Guide responsible about the relationship to update data in both entities
	// We can update data from One side (not owner)
	public void addStudent(Student student){
		students.add(student);
		student.setGuide(this);;
	}

	@Override
	public String toString() {
		return "Guide [id=" + id + ", staffId=" + staffId + ", name=" + name + ", salary=" + salary +  "]";
	}
	
}
