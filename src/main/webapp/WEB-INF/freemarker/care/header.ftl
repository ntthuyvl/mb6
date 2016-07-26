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
<title>Qss</title>
</head>
<header class="trai">
	<img id="logo" src="/images/vmslogo.jpg">
</header>
<header class="giua">
	<h2>CÔNG TY DỊCH VỤ MOBIFONE KV 6</h2>
	<h2>CUSTOMER CARE SUPPORT SYSTEM</h2>
</header>
<header class="phai">
	<div id="welcome">
		<!--h2>MobiFone 6 Quick Support System</h2 -->
		<#if user_logged_name?? && user_logged_name != "anonymousUser">
		<h3>Welcome ${user_logged_name}!</h3>
		</#if> <#if employee??>
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
			user_logged_name != "anonymousUser">
			<ul>
				<li><a href="/3/admin/list">Quản trị</a></li>
				<li><a href="/care">CSKH</a></li>
				<li><a href="/ttcp">TTCP</a></li>
				<li><a href="/sale">BÁN HÀNG</a></li>
				<li><a href="/khdn">KHDN</a></li>
				<li><a href="/khdt">KHĐT</a></li>
				<li><a href="/j_spring_security_logout">Thoát</a></li>
			</ul> </#if></li>
		<li><a href="#"> Tiện ích &darr;</a>
			<ul>
				<li><a href="/care/program/list">Chương trình</a></li>
				<li><a href="/care/hsrm/list">Chăm sóc Hệ số rời mạng</a></li>
				<li><a href="/care/tttb/list">Tình trạng thuê bao</a></li>
				<li><a href="/care/chatluongthuebao/list">Chất lượng thuê bao</a></li>
				<li><a href="/care/chanmo/list">Chặn mở theo tháng</a></li>
				<li><a href="/care/khoso/donle">Tra cứu kho số</a></li>
			<!--	<li><a href="/care/lncc_roimang/list">CSKH lâu năm cước cao</a></li> -->				
			</ul></li>
	</ul>
</div>