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
@Table(name = "transaction")
public class TransactionBE implements Comparable<TransactionBE> {

	public static final String DESCRIPTION = "description";
	public static final String DATE = "date_";
	public static final String AMOUNT = "amount";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@Column(name = DESCRIPTION)
	private String description;

	@Column(name = DATE)
	private long date_;

	@Column(name = AMOUNT)
	private double amount;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private UserBE user;

	// @ManyToOne(optional = false)
	// @JoinColumn(name = "document_id")
	// private UserDocumentBE userDocument;

	@Column(name = "document_id")
	private Integer documentId;

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDate() {
		return date_;
	}

	public void setDate(long date) {
		this.date_ = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public UserBE getUser() {
		return user;
	}

	public void setUser(UserBE user) {
		this.user = user;
	}

	// public UserDocumentBE getUserDocument() {
	// return userDocument;
	// }
	//
	// public void setUserDocument(UserDocumentBE userDocument) {
	// this.userDocument = userDocument;
	// }

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (date_ ^ (date_ >>> 32));
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionBE other = (TransactionBE) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (date_ != other.date_)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TransactionBE [id=" + id + ", description=" + description + ", date_=" + new Date(date_) + ", amount="
				+ amount + ", user=" + user + "]";
	}

	@Override
	public int compareTo(TransactionBE o) {
		return o.hashCode() - hashCode();
	}
}
