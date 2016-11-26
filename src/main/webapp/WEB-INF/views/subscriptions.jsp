<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Transaction History</title>
<link href="<c:url value='/static/css/bootstrap.min.css' />"
	rel="stylesheet" />
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet" />
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script src="static/js/bootstrap.min.js"></script>
</head>

<body>
	<div class="generic-container">
		<div class="well">
			<a href="<c:url value='/list' />"><u>Users</u></a> <a
				href="<c:url value='/add-document-${user.id}' />"><u>History</u></a>
			<a href="<c:url value='/stats-${user.id}' />"><u>Stats</u></a> <a
				href="<c:url value='/subscriptions-${user.id}' />"><u>Subscriptions</u></a>
		</div>

		<div class="panel panel-default">
			<div class="panel-heading">
				<span class="lead">Subscriptions</span>
			</div>
			<div class="tablecontainer">
				<table class="table table-hover table-condensed table-responsive">
					<thead>
						<tr>
							<!-- <th>#</th> -->
							<th><a
								href="<c:url value='/subscriptions-${user.id}?togglesort=memoname'/>">
									Memo/Name <c:choose>
										<c:when test="${sorting.sortByNameMemo > 0}">
											${sorting.sortByNameMemo}
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByNameMemo < 0}">
											${-sorting.sortByNameMemo}
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
							<th><a
								href="<c:url value='/subscriptions-${user.id}?togglesort=category'/>">
									Category<c:choose>
										<c:when test="${sorting.sortByCategory > 0}">
											${sorting.sortByCategory}
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByCategory < 0}">
											${-sorting.sortByCategory}
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
							<th><a
								href="<c:url value='/subscriptions-${user.id}?togglesort=date'/>">
									Date<c:choose>
										<c:when test="${sorting.sortByDate > 0}">
											${sorting.sortByDate}
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByDate < 0}">
											${-sorting.sortByDate}
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
							<th style="text-align: right"><a
								href="<c:url value='/subscriptions-${user.id}?togglesort=amount'/>">
									Amount<c:choose>
										<c:when test="${sorting.sortByAmount > 0}">
											${sorting.sortByAmount}
										&uarr;	
										</c:when>
										<c:when test="${sorting.sortByAmount < 0}">
											${-sorting.sortByAmount}
										&darr;	
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
							</a></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${transactions}" var="transaction"
							varStatus="counter">
							<tr>
								<td>${transaction.description}</td>
								<td>${transaction.category}</td>
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