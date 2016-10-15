<#import "/spring.ftl" as spring /> <#assign datetimeformat="dd/MM/yy">
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="/styles/style.css" />
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/jquery-1.10.2.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/dropdown.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/jquery.calculation.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/jquery.tablesorter.min.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/table2excel.js"></script>
<script type="text/javascript" charset="utf-8" src="/scripts/js/mb6.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/jquery.freezeheader.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/jquery.tabletoCSV.js"></script>
<title>Qss</title>
</head>
<header class="trai">
	<img id="logo" src="/images/vmslogo.jpg">
</header>
<header class="giua">
	<h2>CÔNG TY DỊCH VỤ MOBIFONE KV 6</h2>
	<h2>Phòng Công Nghệ Thông Tin</h2>
</header>
<header class="phai">
	<div id="welcome">
		<!--h2>MobiFone 6 Quick Support System</h2 -->
		<#if user_logged_name?? && user_logged_name != "anonymousUser">
		<h3>Welcome ${user_logged_name}!</h3>
		</#if>
	</div>
</header>
<div id="menu">
	<ul id="nav">
		<#if user_logged_name?? && user_logged_name != "anonymousUser">
		<li><a href="/j_spring_security_logout">Thoát</a></li> </#if>
		<li><a href="/care">CSKH</a></li>
		<li><a href="/ttcp">TTCP</a></li>
		<li><a href="/sale">BÁN HÀNG</a></li>
		<li><a href="/khdn">KHDN</a></li>
		<li><a href="/khdt">KHĐT</a></li>
	</ul>
</div>