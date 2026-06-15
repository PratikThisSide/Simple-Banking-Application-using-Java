<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>This is a Dynamic Web Page</title>
</head>

<body>

	<!-- This is an HTML Comment -->

	<%-- This is a JSP (Server-Side) Comment --%>

	<%
	int a = 10;
	int b = 20;
	%>

	<h2>JSP Demonstration</h2>

	No1 =
	<%=a%>
	<br>
	<br> No2 =
	<%=b%>
	<br>
	<br> Addition =
	<%=a + b%>
	<br>
	<br>

	<%="This is Java here with HTML"%>

	<br>
	<br>

	<h3>Array Elements</h3>

	<%
	int[] arr = { 4, 5, 6, 7, 8, 4, 0 };

	for (int i : arr) {
		System.out.println(i); // Output on Server Console
		out.print(i + " "); // Output on Browser
	}
	%>

	<br>
	<br>

	<h3>Multiplication Table of 5</h3>

	<%
	for (int i = 1; i <= 10; i++) {
		out.print("5 x " + i + " = " + (5 * i) + "<br>");
	}
	%>

	<br>

	<h3>Current Date and Time</h3>

	<%=new java.util.Date()%>

</body>
</html>