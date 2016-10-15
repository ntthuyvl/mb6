var contents;
var id_changeds = [];

function FormatNumberLength(num, length) {
	var r = "" + num;
	while (r.length < length) {
		r = "0" + r;
	}
	return r;
}
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
		var $table = $(table_amar + ' table#datarp');
		$table.floatThead('destroy');
		$table.empty();
		// $table.html('');
		// $table.append('<thead>');
		row2 = '<thead><tr>';
		for ( var key2 in data[0]) {
			row2 = row2 + data[0][key2];
		}
		// alert(row2);
		$table.append(row2);
		// $(row2).appendTo(curtab + ' thead');
		$table.append('<tbody>');
		var $body = $(table_amar + ' table#datarp tbody');
		// $(curtab + ' tbody').html("");
		for (i = 1; i < data.length; i++) {
			row2 = '<tr>';
			for ( var key2 in data[0]) {
				row2 = row2 + data[i][key2];
			}
			$body.append(row2);
			// $(row2).appendTo(curtab + " tbody");
		}
		$('#datarp tr').each(function() {
			$(this).find("td.thc2").attr('contenteditable', 'true');
			$(this).find("td.thc2").css("color", "#bf384f");
			// $(this).find("td.thc2").css("font-weight", "bold");
			$(this).find("td.thc2").css("font-size", "1em");

			$(this).find("td.editable").css("color", "#bf384f");
			$(this).find("td.editable").attr('contenteditable', 'true');
			$(this).find("td.editable").css("font-weight", "bold");
			$(this).find("td.cnumber").css("text-align", "right");
			$(this).find('td.cnumber').each(function() {
				$(this).text(addCommas($(this).text()));
			});
		});

		$table.floatThead({
			scrollContainer : function($table) {
				return $table.closest('.wrapper');
				// return $("#content");
			}
		});
		$('#alertModal').hide();
		// $('input, select, button').prop('disabled', false);
	}
};

function call_back_ajax(data, status) { // alert(JSON.stringify(data));

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
		var $table = $(curtab + ' table#datarp');
		$table.floatThead('destroy');
		$table.empty();
		// $table.html('');
		// $table.append('<thead>');
		row2 = '<thead><tr>';
		for ( var key2 in data[0]) {
			row2 = row2 + data[0][key2];
		}
		// alert(row2);
		$table.append(row2);
		// $(row2).appendTo(curtab + ' thead');
		$table.append('<tbody>');
		var $body = $(curtab + ' table#datarp tbody');
		row2 = '<tr style="display:none;">';
		for ( var key2 in data[0]) {
			row2 = row2 + data[0][key2];
		}
		$body.append(row2);

		for (i = 1; i < data.length; i++) {
			row2 = '<tr>';
			for ( var key2 in data[0]) {
				row2 = row2 + data[i][key2];
			}
			$body.append(row2);
			// $(row2).appendTo(curtab + " tbody");
		}
		$('#datarp tr').each(function() {
			$(this).find("td.thc2").attr('contenteditable', 'true');
			$(this).find("td.thc2").css("color", "#bf384f");
			// $(this).find("td.thc2").css("font-weight", "bold");
			$(this).find("td.thc2").css("font-size", "1em");

			$(this).find("td.editable").css("color", "#bf384f");
			$(this).find("td.editable").attr('contenteditable', 'true');
			$(this).find("td.editable").css("font-weight", "bold");
			$(this).find("td.cnumber").css("text-align", "right");
			$(this).find('td.cnumber').each(function() {
				$(this).text(addCommas($(this).text()));
			});
		});
		$table.floatThead({
			scrollContainer : function($table) {
				// return $table.closest('.wrapper');
				return $("#content");
			}

		});
		// $('input, select, button').prop('disabled', false);
		$('#alertModal').hide();
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
function cNumberUpdate() {

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