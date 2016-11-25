<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Statistics</title>
<link href="<c:url value='/static/css/bootstrap.min.css' />"
	rel="stylesheet"></link>
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
</head>

<body>
	<div class="generic-container">
		<div class="well">
			<a href="<c:url value='/list' />"><u>Users</u></a> <a
				href="<c:url value='/add-document-${user.id}' />"><u>History</u></a>
		</div>

		<div class="panel panel-default">
			<div class="panel-heading">
				<span class="lead">Categories</span>
			</div>
			<div class="tablecontainer">
				<table class="table table-hover" id="anchor">
					<thead>
						<tr>
							<!-- <th>#</th> -->
							<th><a
								href="<c:url value='/add-document-${user.id}?togglesort=memoname'/>">
									Memo/Name <c:choose>
										<c:when test="${sorting.sortByNameMemo > 0}">
										<%-- ${sorting.sortByNameMemo} --%>
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByNameMemo < 0}">
										<%-- ${-sorting.sortByNameMemo} --%>
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
							<th><a
								href="<c:url value='/add-document-${user.id}?togglesort=category'/>">
									Category<c:choose>
										<c:when test="${sorting.sortByCategory > 0}">
										<%-- ${sorting.sortByCategory} --%>
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByCategory < 0}">
										<%-- ${-sorting.sortByCategory} --%>
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
							<th><a
								href="<c:url value='/add-document-${user.id}?togglesort=subscription'/>">
									Subscription<c:choose>
										<c:when test="${sorting.sortByIsSubscription > 0}">
										<%-- ${sorting.sortByIsSubscription} --%>
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByIsSubscription < 0}">
										<%-- ${-sorting.sortByIsSubscription} --%>
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
							<th><a
								href="<c:url value='/add-document-${user.id}?togglesort=date'/>">
									Date<c:choose>
										<c:when test="${sorting.sortByDate > 0}">
										<%-- ${sorting.sortByDate} --%>
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByDate < 0}">
										<%-- ${-sorting.sortByDate} --%>
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
							<th style="text-align: right"><a
								href="<c:url value='/add-document-${user.id}?togglesort=amount'/>">
									Amount<c:choose>
										<c:when test="${sorting.sortByAmount > 0}">
										<%-- ${sorting.sortByAmount} --%>
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByAmount < 0}">
										<%-- ${-sorting.sortByAmount} --%>
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${transactions}" var="transaction" varStatus="counter">
							<tr>
								<td>${transaction.description}</td>
								<td>${transaction.category}</td>
								<c:choose>
									<c:when test="${transaction.subscription}">
										<td>yes</td>
									</c:when>
									<c:otherwise>
										<td>no</td>
									</c:otherwise>
								</c:choose>
								<td>${transaction.date}</td>
								<td align="right">${transaction.amount}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>