<#import "/spring.ftl" as spring /> <#assign datetimeformat="dd/MM/yy">
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="/styles/bootstrap-3.3.7-dist/css/bootstrap.css" />
<link rel="stylesheet" type="text/css" href="/styles/bootstrap-3.3.7-dist/css/docs.min.css" />
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
<script type="text/javascript" charset="utf-8" src="/scripts/js/mb6_bt.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/jquery.freezeheader.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/jquery.tabletoCSV.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/scripts/js/jquery.floatThead.min.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/styles/bootstrap-3.3.7-dist/js/bootstrap.js"></script>	
<script type="text/javascript" charset="utf-8"
	src="/styles/bootstrap-3.3.7-dist/js/docs.min.js"></script>
	
<style>

.navbar .navbar-nav {
  display: inline-block;
  float: none;
  vertical-align: top;
}

.navbar .container-fluid {
  text-align: center;  
}
.container-fluid {
  text-align: center;  
}
.dropdown-menu {
  text-align: left;  
}

.nav a{    
    color: #1245C2;  
}

ul.nav>li>a{    
	font-family: 'VIE-HandelGothic';
}
    
    
</style>	
	
<title>Qss</title>
</head>
<div style="width: 100%;margin: 0px 0 0 0 ;">

<nav class="navbar">
  
  <div class="container-fluid">  
    <ul class="nav navbar-nav navbar-collapse">
<!--    	<li><a href="/" style="color: #F90606;">mobifone6 <span class="glyphicon glyphicon-paperclip"></span>qss</a></li>-->
		<li><a href="/" style ="padding-top: 8px;"><img id="logo" width="75px" src="/images/qsslogo2.png"></a></li>   	
    	    	 
		<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">KHDN<span class="caret"></span></a>
			<ul class="dropdown-menu">
				<li><a href="/khdn/program/list">Chương trình</a></li>
				<!-- <li><a href="/khdn/banhang/list">TB phát triển mới old</a></li> -->
				<li><a href="/khdn/tttb/list">TB phát triển mới</a></li>				
				<li><a href="/khdn/chatluongthuebao/list">Chất lượng thuê bao</a></li>
				<li role="separator" class="divider"></li>
				<li><a href="/1/admin/list">Quản trị</a></li>								
			</ul>
		</li>   
		<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">SALE<span class="caret"></span></a>
			<ul class="dropdown-menu">
				<li><a href="/sale/program/list">Chương trình</a></li>
				<li><a href="/sale/chatluongdonhang/list">Chất lượng đơn
						hàng</a></li>
				<li><a href="/sale/chatluongthuebao/list">Chất lượng thuê
						bao</a></li>
				<li><a href="/sale/thongtintrathuong/list">Thông tin trả thưởng</a></li>
				<li><a href="/sale/cell/list">Báo cáo theo cell</a></li>										
				<!-- <li><a href="/sale/msale/list">Điểm bán lẻ Msale </a></li>  -->
				<li><a href="/sale/msale/gmap">Điểm bán lẻ trên Gmap </a></li>
				<li><a href="/sale/cell/gmap">Trạm phát sóng trên Gmap </a></li>				
				<!-- <li><a href="/sale/trathuong/list">Hỗ trợ trả thưởng</a></li>  -->
				<li role="separator" class="divider"></li>
				<li><a href="/2/admin/list">Quản trị</a></li>
			</ul>
		</li>      
		
		<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">TTCP<span class="caret"></span></a>			
			<ul class="dropdown-menu">
				<li><a href="/ttcp/hoahongthucuoc/list/0">Kế hoạch thu cước</a></li>
				<li><a href="/ttcp/hoahongthucuoc/list/2">Tổng hợp-Kết xuất biên bản</a></li>
				<li><a href="/ttcp/hoahongthucuoc/list/1">Thông tin đại lý</a></li>
				<li><a href="/ttcp/hoahongthucuoc/list/3">Thông tin vùng thu cước</a></li>
				<li role="separator" class="divider"></li>
				<li><a href="/4/admin/list">Quản trị</a></li>
			</ul>
		</li>
		<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">CSKH<span class="caret"></span></a>		
			<ul class="dropdown-menu">
				<li><a href="/care/program/list">Chương trình</a></li>
				<li><a href="/care/hsrm/list">Hệ số rời mạng</a></li>
				<li><a href="/care/tttb/list">Tình trạng thuê bao</a></li>
				<li><a href="/care/chatluongthuebao/list">Chất lượng thuê bao</a></li>
				<li><a href="/care/chanmo/list">Chặn mở theo tháng</a></li>
				<li><a href="/care/khoso/donle">Tra cứu kho số</a></li>
				<li><a href="/care/lncc_roimang/list">Lâu năm cước cao</a></li>
				<li><a href="/care/mobitv/list">MobiTV</a></li>				
			    <li role="separator" class="divider"></li>
				<li><a href="/3/admin/list">Quản trị</a></li>				
			</ul>
		</li>	
		<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">CNTT<span class="caret"></span></a>		
			<ul class="dropdown-menu">
				<li><a href="/cntt/auto_report/list">Báo cáo tự động</a></li>								
			    <li role="separator" class="divider"></li>
				<li><a href="/6/admin/list">Quản trị</a></li>				
			</ul>
		</li>		
		
		
		<!--		
		<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">KHĐT<span class="caret"></span></a>      
			<ul class="dropdown-menu">
				<!-- <li><a href="/khdt/khsxkd/list">Nhập kế hoạch SXKD</a></li>  
				<li><a href="/khdt/danhgiachiphi/DanhGiaChiPhiTrucTiep">Đánh
						giá chi phí trực tiếp</a></li>
				<li><a href="/khdt/danhgiachiphi/update_cpgt">Đánh giá
						chi phí gián tiếp</a></li>
				<li><a href="/khdt/danhgiachiphi/update_cptt">Cập nhật
						chi phí trực tiếp</a></li>
			    <li role="separator" class="divider"></li>
				<li><a href="/5/admin/list">Quản trị</a></li>
			</ul>			
		</li>
		-->
		<li><a href="/j_spring_security_logout">welcome ${user_logged_name!""} <span class="glyphicon glyphicon-log-out"></span></a></li>  
    </ul>
  </div>
</nav>
<div id="alertModal" class="modal" style="text-align: center;>	
	<div class="modal-content" style="text-align: center;">
		<img id="wating" src="/images/wating.gif" >		
	</div>
</div>
