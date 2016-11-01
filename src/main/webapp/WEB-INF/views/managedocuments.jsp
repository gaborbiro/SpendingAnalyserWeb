<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload/Download/Delete Files</title>
<link href="<c:url value='/static/css/bootstrap.css' />"
	rel="stylesheet"></link>
<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
</head>

<body>
	<div class="generic-container">
		<div class="well">
			Go to <a href="<c:url value='/list' />">Users List</a>
		</div>
		<div class="panel panel-default">
			<!-- Default panel contents -->
			<div class="panel-heading">
				<span class="lead">List of Files</span>
			</div>
			<div class="tablecontainer">
				<table class="table table-hover">
					<thead>
						<tr>
							<th>No.</th>
							<th>File Name</th>
							<th>Type</th>
							<th>Description</th>
							<th width="100"></th>
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
								<td><a
									href="<c:url value='/download-document-${user.id}-${doc.id}' />"
									class="btn btn-success custom-width">download</a></td>
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
				<table class="table table-hover">
					<thead>
						<tr>
							<!-- <th>#</th> -->
							<th><a href="<c:url value='/add-document-${user.id}?togglesort=memoname'/>">
								Memo/Name
								<c:choose>
									<c:when test="${sorting.sortByNameMemo == 1}">
										&uarr;	
									</c:when>
									<c:when test="${sorting.sortByNameMemo == -1}">
										&darr;	
									</c:when>
									<c:otherwise>
									</c:otherwise>
								</c:choose>
							</a></th>
							<th><a href="<c:url value='/add-document-${user.id}?togglesort=category'/>">
								Category
								<c:choose>
									<c:when test="${sorting.sortByCategory == 1}">
										&uarr;	
									</c:when>
									<c:when test="${sorting.sortByCategory == -1}">
										&darr;	
									</c:when>
									<c:otherwise>
									</c:otherwise>
								</c:choose>
							</a></th>
							<th><a href="<c:url value='/add-document-${user.id}?togglesort=subscription'/>">
								Subscription
								<c:choose>
									<c:when test="${sorting.sortByIsSubscription == 1}">
										&uarr;	
									</c:when>
									<c:when test="${sorting.sortByIsSubscription == -1}">
										&darr;	
									</c:when>
									<c:otherwise>
									</c:otherwise>
								</c:choose>
							</a></th>
							<th><a href="<c:url value='/add-document-${user.id}?togglesort=date'/>">
								Date
								<c:choose>
									<c:when test="${sorting.sortByDate == 1}">
										&uarr;	
									</c:when>
									<c:when test="${sorting.sortByDate == -1}">
										&darr;	
									</c:when>
									<c:otherwise>
									</c:otherwise>
								</c:choose>
							</a></th>
							<th style="text-align: right"><a href="<c:url value='/add-document-${user.id}?togglesort=amount'/>">
								Amount
								<c:choose>
									<c:when test="${sorting.sortByAmount == 1}">
										&uarr;	
									</c:when>
									<c:when test="${sorting.sortByAmount == -1}">
										&darr;	
									</c:when>
									<c:otherwise>
									</c:otherwise>
								</c:choose>
							</a></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${spendings}" var="spending" varStatus="counter">
							<tr>
								<%-- <td>${counter.index + 1}</td> --%>
								<td>${spending.description}</td>
								<td>${spending.category}</td>
								<c:choose>
									<c:when test="${spending.subscription}">
										<td>yes</td>	
									</c:when>
									<c:otherwise>
										<td>no</td>
									</c:otherwise>
								</c:choose>
								<td>${spending.date}</td>
								<td align="right">${spending.amount}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>