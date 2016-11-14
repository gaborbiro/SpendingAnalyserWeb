package com.gb.ofxanalyser.model.be;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_document")
public class UserDocumentBE {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "content_type", length = 100, nullable = false)
	private String contentType;

	// @OneToMany(mappedBy = "userDocument", cascade = CascadeType.ALL)
	// private Set<TransactionBE> transactions = new HashSet<TransactionBE>();

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private UserBE user;

	@Column(name = "start_date")
	private long startDate;

	@Column(name = "end_date")
	private long endDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	// public Set<TransactionBE> getTransactions() {
	// return transactions;
	// }
	//
	// public void setTransactions(Set<TransactionBE> transactions) {
	// this.transactions = transactions;
	// }

	public UserBE getUser() {
		return user;
	}

	public void setUser(UserBE user) {
		this.user = user;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserDocumentBE))
			return false;
		UserDocumentBE other = (UserDocumentBE) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserDocumentBE [id=" + id + ", name=" + name + ", description=" + description + ", contentType="
				+ contentType + ", user=" + user + ", startDate=" + new Date(startDate) + ", endDate="
				+ new Date(endDate) + "]";
	}
}
