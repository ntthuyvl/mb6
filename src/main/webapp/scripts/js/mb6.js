var contents;
var id_changeds = [];


function call_back_ajax2(data, status, table_amar) {
	// alert(JSON.stringify(data));
	var error = "";
	try {
		error = data[0].error;
	} catch (Exception) {
		error = null;
	}

	if (error != null) {
		alert(error);
		self.location = "/j_spring_security_logout";
	} else if (data.length > 0) {
		$(table_amar + ' tbody').html("");
//		var row2 = '<tr>';
//		for ( var key2 in data[0]) {
//			row2 = row2 + data[0][key2];
//		}
//		row2 = row2 + "</th></tr>";
//		$(table_amar + ' table thead').html(row2);

		for (i = 0; i < data.length; i++) {
			row2 = '<tr>';
			for ( var key2 in data[0]) {
				row2 = row2 + data[i][key2];
			}
			row2 = row2 + "</td>";
			$(row2).appendTo(table_amar + " tbody");
		}
		$('input').prop('disabled', false);
	}
};

function call_back_ajax(data, status) {
	// alert(JSON.stringify(data));
	var error = "";
	try {
		error = data[0].error;
	} catch (Exception) {
		error = null;
	}

	if (error != null) {
		alert(error);
		self.location = "/j_spring_security_logout";
	} else if (data.length > 0) {
		$(curtab + ' tbody').html("");
		/*
		 * var row2 = '<tr>'; for ( var key2 in data[0]) { row2 = row2 +
		 * data[0][key2]; } row2 = row2 + "</th></tr>"; $(curtab + ' table
		 * thead').html(row2);
		 */
		for (i = 0; i < data.length; i++) {
			row2 = '<tr>';
			for ( var key2 in data[0]) {
				row2 = row2 + data[i][key2];
			}
			row2 = row2 + "</td>";
			$(row2).appendTo(curtab + " tbody");
		}
		$('#datarp tr').each(function() {
			$(this).find("td.thc2").attr('contenteditable', 'true');
			$(this).find("td.thc2").css("color", "#bf384f");
			//$(this).find("td.thc2").css("font-weight", "bold");
			$(this).find("td.thc2").css("font-size", "1em");		
			
			$(this).find("td.editable").css("color", "#bf384f");
			$(this).find("td.editable").attr('contenteditable', 'true');
			$(this).find("td.editable").css("font-weight", "bold");
			$(this).find("td.cnumber").css("text-align", "right");			 
			$(this).find('td.cnumber').each (function() {
				$(this).text(addCommas($(this).text()));			
				});
			
			
			
			//var number_value =parseFloat($(this).html());
			//var input_number = "<input type=\"number\" step=\"10000\" style =\"background-color: transparent;border: 0; height:90%; width: 90%; margin: 0;\"  value=\""+$(this).find("td.thc2").html()+"\">"
			//$(this).find("td.thc2").html(input_number);   				
			
		});

		$('input, select').prop('disabled', false);
	}
};

function tab_present() {
	$("#content").find("[id^='tab']").hide(); // Hide all content
	$("#tabs li:first").attr("id", "current"); // Activate the first tab
	$("#content #tab1").fadeIn(); // Show first tab's content
	$('#tabs a').click(function(e) {
		e.preventDefault();
		if ($(this).closest("li").attr("id") == "current") { // detection for
			// current tab
			return;
		} else {
			$("#content").find("[id^='tab']").hide(); // Hide all content
			$("#tabs li").attr("id", ""); // Reset id's
			$(this).parent().attr("id", "current"); // Activate this
			$('#' + $(this).attr('name')).fadeIn(); // Show content for the
			// current tab
			curtab = '#' + $(this).attr('name');
			load_tab_data();
		}
	});
};

function convertDate(nv_date) {
	if (nv_date != null && nv_date != "")
		return nv_date.substring(6, 10) + "-" + nv_date.substring(3, 5) + "-"
				+ nv_date.substring(0, 2);
	else
		return null;
}
function replaceNull(value, key) {
	return (value == null) ? key : value
}
function cNumberUpdate()
{
	
}


function addCommas(nStr) {
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}