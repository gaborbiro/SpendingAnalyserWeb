<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Transaction History</title>
<link href="<c:url value='/static/css/bootstrap.css' />"
	rel="stylesheet"></link>
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
<script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="/static/js/jquery.cookie.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$(function() {
			//			window.alert("1");
			//			window.alert("start " + $.cookie("top"));
			//			window.alert("2");
			var top = parseInt($.cookie("top"));
			window.alert("top:" + top);
			if (top)
				$(document).scrollTop(top);
			$(document).scroll(function() {
				var top = $(document).scrollTop();
				$.cookie("top", top);
			})
		});
	});
</script>
</head>

<body>
	<div class="generic-container">
		<div class="well">
			<a href="<c:url value='/list' />"><u>Users</u></a> <a
				href="<c:url value='/stats-${user.id}' />"><u>Stats</u></a>
		</div>
		<div class="panel panel-default">
			<!-- Default panel contents -->
			<div class="panel-heading">
				<span class="lead">List of Files</span>
			</div>
			<div class="tablecontainer">
				<a href="#table"></a>
				<table class="table table-hover">
					<thead>
						<tr>
							<th>No.</th>
							<th>File Name</th>
							<th>Type</th>
							<th>Description</th>
							<th>Period</th>
							<th width="100"></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${documents}" var="doc" varStatus="counter">
							<tr>
								<td>${counter.index + 1}</td>
								<td>${doc.name}</td>
								<td>${doc.contentType}</td>
								<td>${doc.description}</td>
								<td>${doc.period}</td>
								<td><a
									href="<c:url value='/delete-document-${user.id}-${doc.id}' />"
									class="btn btn-danger custom-width">delete</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="panel panel-default">

			<div class="panel-heading">
				<span class="lead">Upload New File</span>
			</div>
			<div class="uploadcontainer">
				<form:form method="POST" modelAttribute="fileBucket"
					enctype="multipart/form-data" class="form-horizontal">

					<div class="row">
						<div class="form-group col-md-12">
							<label class="col-md-3 control-lable" for="file">Upload a
								file</label>
							<div class="col-md-7">
								<form:input type="file" path="files" id="files"
									multiple="multiple" class="form-control input-sm" />
								<div class="has-error">
									<form:errors path="files" class="help-inline" />
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="form-group col-md-12">
							<label class="col-md-3 control-lable" for="files">Description</label>
							<div class="col-md-7">
								<form:input type="text" path="description" id="description"
									class="form-control input-sm" />
							</div>

						</div>
					</div>

					<div class="row">
						<div class="form-actions floatRight">
							<input type="submit" value="Upload"
								class="btn btn-primary btn-sm">
						</div>
					</div>

				</form:form>
			</div>
		</div>

		<div class="panel panel-default">
			<div class="panel-heading">
				<span class="lead">Transactions</span>
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
						<c:forEach items="${transactions}" var="transaction"
							varStatus="counter">
							<tr>
								<td>${transaction.description}</td>
								<td>${transaction.category}</td>
								<c:choose>
									<c:when test="${transaction.subscription}">
										<td>yes</td>
									</c:when>
									<c:otherwise>
										<td></td>
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