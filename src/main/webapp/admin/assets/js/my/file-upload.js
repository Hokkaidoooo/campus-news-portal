/**
 * 上传文件，基于ajaxFileUpload代码
 */
jQuery.extend({
	createUploadIframe : function(id, uri) {
		//create frame
		var frameId = 'jUploadFrame' + id;
		if (window.ActiveXObject) {
			if (jQuery.browser.version == "9.0"|| jQuery.browser.version == "10.0") {
				var io = document.createElement('iframe');
				io.id = frameId;
				io.name = frameId;
			} else if (jQuery.browser.version == "6.0"|| jQuery.browser.version == "7.0"|| jQuery.browser.version == "8.0") {
				var io = document.createElement('<iframe id="'+ frameId + '" name="' + frameId + '" />');
				if (typeof uri == 'boolean') {
					io.src = 'javascript:false';
				} else if (typeof uri == 'string') {
					io.src = uri;
				}
			}
		} else {
			var io = document.createElement('iframe');
			io.id = frameId;
			io.name = frameId;
		}
		io.style.position = 'absolute';
		io.style.top = '-1000px';
		io.style.left = '-1000px';
		document.body.appendChild(io);
		return io
	},
	createUploadForm : function(id, fileElementId) {
		//create form
		var formId = 'jUploadForm' + id;
		var fileId = 'jUploadFile' + id;
		var form = $('<form  action="" method="POST" name="' + formId
				+ '" id="' + formId
				+ '" enctype="multipart/form-data"></form>');
		var oldElement = $('#' + fileElementId);
		var newElement = $(oldElement).clone();
		$(oldElement).attr('id', fileId);
		$(oldElement).before(newElement);
		$(oldElement).appendTo(form);
		//set attributes
		$(form).css('position', 'absolute');
		$(form).css('top', '-1200px');
		$(form).css('left', '-1200px');
		$(form).appendTo('body');
		return form;
	},
	ajaxFileUpload : function(s) {
		// TODO introduce global settings, allowing the client to modify them for all requests, not only timeout		
		s = jQuery.extend({}, jQuery.ajaxSettings, s);
		var id = s.fileElementId;
		var form = jQuery.createUploadForm(id, s.fileElementId);
		var io = jQuery.createUploadIframe(id, s.secureuri);
		var frameId = 'jUploadFrame' + id;
		var formId = 'jUploadForm' + id;
		// Watch for a new set of requests
		if (s.global && !jQuery.active++) {
			jQuery.event.trigger("ajaxStart");
		}
		var requestDone = false;
		// Create the request object
		var xml = {}
		if (s.global)
			jQuery.event.trigger("ajaxSend", [ xml, s ]);
		// Wait for a response to come back
		var uploadCallback = function(isTimeout) {
			var io = document.getElementById(frameId);
			try {
				if (io.contentWindow) {
					xml.responseText = io.contentWindow.document.body ? io.contentWindow.document.body.innerHTML
							: null;
					xml.responseXML = io.contentWindow.document.XMLDocument ? io.contentWindow.document.XMLDocument
							: io.contentWindow.document;

				} else if (io.contentDocument) {
					xml.responseText = io.contentDocument.document.body ? io.contentDocument.document.body.innerHTML
							: null;
					xml.responseXML = io.contentDocument.document.XMLDocument ? io.contentDocument.document.XMLDocument
							: io.contentDocument.document;
				}
			} catch (e) {
				jQuery.handleError(s, xml, null, e);
			}
			if (xml || isTimeout == "timeout") {
				requestDone = true;
				var status;
				try {
					status = isTimeout != "timeout" ? "success"
							: "error";
					// Make sure that the request was successful or notmodified
					if (status != "error") {
						// process the data (runs the xml through httpData regardless of callback)
						var data = jQuery.uploadHttpData(xml,
								s.dataType);
						// If a local callback was specified, fire it and pass it the data
						if (s.success)
							s.success(data, status);

						// Fire the global callback
						if (s.global)
							jQuery.event.trigger("ajaxSuccess", [ xml,
									s ]);
					} else
						jQuery.handleError(s, xml, status);
				} catch (e) {
					status = "error";
					jQuery.handleError(s, xml, status, e);
				}
				// The request was completed
				if (s.global)
					jQuery.event.trigger("ajaxComplete", [ xml, s ]);
				// Handle the global AJAX counter
				if (s.global && !--jQuery.active)
					jQuery.event.trigger("ajaxStop");
				// Process result
				if (s.complete)
					s.complete(xml, status);
				jQuery(io).unbind()
				setTimeout(function() {
					try {
						$(io).remove();
						$(form).remove();

					} catch (e) {
						jQuery.handleError(s, xml, null, e);
					}

				}, 100)
				xml = null
			}
		}
		// Timeout checker
		if (s.timeout > 0) {
			setTimeout(function() {
				// Check to see if the request is still happening
				if (!requestDone)
					uploadCallback("timeout");
			}, s.timeout);
		}
		try {
			// var io = $('#' + frameId);
			var form = $('#' + formId);
			$(form).attr('action', s.url);
			$(form).attr('method', 'POST');
			$(form).attr('target', frameId);
			if (form.encoding) {
				form.encoding = 'multipart/form-data';
			} else {
				form.enctype = 'multipart/form-data';
			}
			$(form).submit();
		} catch (e) {
			jQuery.handleError(s, xml, null, e);
		}
		if (window.attachEvent) {
			document.getElementById(frameId).attachEvent('onload',
					uploadCallback);
		} else {
			document.getElementById(frameId).addEventListener('load',
					uploadCallback, false);
		}
		return {
			abort : function() {
			}
		};
	},
	uploadHttpData : function(r, type) {
		var data = !type;
		data = type == "xml" || data ? r.responseXML : r.responseText;
		// If the type is "script", eval it in global context
		if (type == "script")
			jQuery.globalEval(data);
		// Get the JavaScript object, if JSON is used.
		if (type == "json")
			eval("data = " + data);
		// evaluate scripts within html
		if (type == "html")
			jQuery("<div>").html(data).evalScripts();
		//alert($('param', data).each(function(){alert($(this).attr('value'));}));
		return data;
	}
})

/**
 * 上传单张图片
 * @param obj
 * @param id
 */
function xUploadPic(obj,id){
	$("#fileToPic").click();
	$("#fileToPic").change(function(e){
		$.ajaxFileUpload({
			url:ink.home+"admin/ajax/pic.htm",
			secureuri:false,
			fileElementId:"fileToPic",
			dataType:"text",
			success:function(data,status){
				$("#"+id).val(data);
				$("#fileToPic").val('');
			}
		});
	});
}

/**
 * 一次选择多张图片添加
 */
function plxtpsc(obj,id){
	$("#fileToPic").click();
	$("#fileToPic").change(function(e){
		$.ajaxFileUpload({
			url:ink.home+"admin/ajax/plpic.htm",
			secureuri:false,
			fileElementId:"fileToPic",
			dataType:"text",
			success:function(data,status){
				$("#"+id).val(data);
				$("#fileToPic").val('');
			}
		});
	});
}

//上传图片，并加水印
function syxtpsc(obj, id, syid){
	$("#fileToPic").click();
	$("#fileToPic").change(function(e){
		$.ajaxFileUpload({
			url:ink.home+"admin/ajax/sypic.htm",
			secureuri:false,
			fileElementId:"fileToPic",
			dataType:"text",
			success:function(data,status){
				var dindex = data.indexOf("=====");
				$("#"+id).val(data.substring(0, dindex));
				$("#"+syid).val(data.substring(dindex+5));
				$("#fileToPic").val('');
			}
		});
	});
}

function xwdsc(obj,id){
	$("#fileToDoc").change(function(e){
		var v=$("#fileToDoc").val();
		if(v){
			obj.innerHTML="上传文档";
			obj.title="文档"+v+"将被上传";
		}else{
			obj.innerHTML="选择文档";
			obj.title="还没选择文档";
		}
	});
	if(!$("#fileToDoc").val()){
		obj.innerHTML="选择文档";
		$("#fileToDoc").click();
		return;
	}
	$.ajaxFileUpload({
		url:ink.home+"admin/ajax/doc.htm",
		secureuri:false,
		fileElementId:"fileToDoc",
		dataType:"text",
		success:function(data,status){
			$("#"+id).val(data);
			obj.innerHTML="选择文档";
		}
	});
}