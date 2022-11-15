$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/message/letter/send",
		{"toName":toName,"content":content},
		function (data) {
			console.log(data);
			$("#hintBody").text(data.msg);

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//重载当前页面
				location.reload();
			}, 2000);
		}
	);

}

function delete_msg() {
	// TODO 删除数据
	var id = $(this).val();
	$.get(
		CONTEXT_PATH + "/message/letter/delete/" + id,
		function (data) {
			console.log(data);

			$(this).parents(".media").remove();
			location.reload();
		}
	);

}