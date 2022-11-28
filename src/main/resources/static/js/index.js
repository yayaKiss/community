$(function(){
	$("#publishBtn").click(publish);
});

function publish() {

	//发送请求之前，将CSRF令牌设置到请求头中
	// var token = $("meta[name='_csrf']").attr("content");
	// var header = $("meta[name='_csrf_header']").attr("content");
	// $(document).ajaxSend(function (e,xhr,options){
	// 	xhr.setRequestHeader(header,token);
	// })

	$("#publishModal").modal("hide");
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/discuss/publish",
		{"title":title, "content":content},
		function (data){
			console.log(data);
			//设置提示框信息
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//2秒后提示框消失
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//如果成功,重新加载页面
				if(data.code == 200){
					window.location.reload();
				}
			}, 2000);
		}
	)
}