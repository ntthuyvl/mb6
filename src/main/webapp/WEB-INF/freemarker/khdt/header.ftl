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
	<h2>KHĐT SUPPORT SYSTEM</h2>
</header>
<SCRIPT>	
	var app = 5;
	</SCRIPT>	
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
		<li><a href="#">Hệ thống &darr;</a> <#if user_logged_name?? &&
			user_logged_name != "anonymousUser">
			<ul>
				<li><a href="/5/admin/list">Quản trị</a></li>
				<li><a href="/care">CSKH</a></li>
				<li><a href="/ttcp">TTCP</a></li>
				<li><a href="/sale">BÁN HÀNG</a></li>
				<li><a href="/khdn">KHDN</a></li>
				<li><a href="/khdt">KHĐT</a></li>
				<li><a href="/j_spring_security_logout">Thoát</a></li>
			</ul> </#if></li>
		<li><a href="#"> Tiện ích &darr;</a>
			<ul>
				<!-- <li><a href="/khdt/khsxkd/list">Nhập kế hoạch SXKD</a></li>  -->
				<li><a href="/khdt/danhgiachiphi/DanhGiaChiPhiTrucTiep">Đánh
						giá chi phí trực tiếp</a></li>
				<li><a href="/khdt/danhgiachiphi/update_cpgt">Đánh giá
						chi phí gián tiếp</a></li>
				<li><a href="/khdt/danhgiachiphi/update_cptt">Cập nhật
						chi phí trực tiếp</a></li>

			</ul></li>
	</ul>
</div>