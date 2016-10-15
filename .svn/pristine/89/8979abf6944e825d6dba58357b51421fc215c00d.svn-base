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
<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA6MGtHPG1GTklAvaTJrgONuqu4TDPp3MU&sensor=true"></script>
<title>Qss</title>
<style>
@import 'https://fonts.googleapis.com/css?family=Noto+Serif';
</style>
</head>
<header class="trai">
	<img id="logo" src="/images/vmslogo.jpg">
	<h2>CÔNG TY DỊCH VỤ MOBIFONE KV 6</h2>
</header>
<header class="giua">
	<h1>Sale Support System</h1>
</header>
<SCRIPT>	
	var app = 2;
	</SCRIPT>
<header class="phai">
	<div id="welcome">
		<!--h2>MobiFone 6 Quick Support System</h2 -->
		<#if user_logged_name?? && user_logged_name !="anonymousUser"> Welcome
		${user_logged_name}! </#if> <#if employee??>
		<h3>
			Xin Chào <a href=/admbase/user/${employee.user.name} />${employee.user.label}</a>
		</h3>
		<h3>${employee.department.label}</h3>
		<#if employee.department.careEmp?? &&
		employee.department.careEmp.user.label??>
		<h4>CB Chăm sóc
			${(employee.department.careEmp.user.label)!""}-${(employee.department.careEmp.telnum)!""}</h4>
		</#if> </#if>
	</div>
</header>
<div id="menu">
	<ul id="nav">
		<li><a href="#">Hệ thống &darr;</a> <#if user_logged_name?? &&
			user_logged_name !="anonymousUser">
			<ul>
				<li><a href="/2/admin/list">Quản trị</a></li>
				<li><a href="/care">CSKH</a></li>
				<li><a href="/ttcp">TTCP</a></li>
				<li><a href="/sale">BÁN HÀNG</a></li>
				<li><a href="/khdn">KHDN</a></li>
				<li><a href="/khdt">KHĐT</a></li>
				<li><a href="/j_spring_security_logout">Thoát</a></li>
			</ul> </#if></li>
		<li><a href="#"> Tiện ích &darr;</a>
			<ul>
				<li><a href="/sale/program/list">Chương trình</a></li>
				<li><a href="/sale/chatluongdonhang/list">Chất lượng đơn
						hàng</a></li>
				<li><a href="/sale/chatluongthuebao/list">Chất lượng thuê
						bao</a></li>
				<li><a href="/sale/thongtintrathuong/list">Thông tin trả thưởng</a></li>						
				<!-- <li><a href="/sale/msale/list">Điểm bán lẻ Msale </a></li>  -->
				<li><a href="/sale/msale/gmap">Điểm bán lẻ Gmap </a></li>
				<!-- <li><a href="/sale/trathuong/list">Hỗ trợ trả thưởng</a></li>  -->
			</ul></li>
	</ul>
</div>