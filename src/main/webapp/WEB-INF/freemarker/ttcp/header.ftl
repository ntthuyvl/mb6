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
	<h2>CÔNG TY DỊCH VỤ MOBIFONE KV 6</h2>
</header>
<header class="giua">
	<h1>Payment Support System</h1>
</header>
<header class="phai">
	<div id="welcome">
		<!--h2>MobiFone 6 Quick Support System</h2 -->
		<#if user_logged_name?? && user_logged_name !="anonymousUser"> Welcome${user_logged_name}! </#if>
	</div>
</header>
<SCRIPT>	
	var app = 4;
	</SCRIPT>	
<div id="menu">
	<ul id="nav">
		<li><a href="#">Hệ thống &darr;</a> 
			<#if user_logged_name?? && user_logged_name !="anonymousUser">
				<ul>
					<li><a href="/4/admin/list">Quản trị</a></li>
					<li><a href="/care">CSKH</a></li>
					<li><a href="/ttcp">TTCP</a></li>
					<li><a href="/sale">BÁN HÀNG</a></li>
					<li><a href="/khdn">KHDN</a></li>
					<li><a href="/khdt">KHĐT</a></li>
					<li><a href="/j_spring_security_logout">Thoát</a></li>
				</ul>
			</#if></li>
		<li>
			<a href="#"> Tiện ích &darr;</a>
			<ul>
				<li><a href="/ttcp/hoahongthucuoc/list/0">Kế hoạch thu cước</a></li>
				<li><a href="/ttcp/hoahongthucuoc/list/2">Tổng hợp-Kết xuất biên bản</a></li>
				<li><a href="/ttcp/hoahongthucuoc/list/1">Thông tin đại lý</a></li>
				<li><a href="/ttcp/hoahongthucuoc/list/3">Thông tin vùng thu cước</a></li>
			</ul>
		</li>
	</ul>
</div>