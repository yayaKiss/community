$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	$.post(
		CONTEXT_PATH + "/follow",
		{"entityType":3,
			"entityId": $(btn).prev().val()},
		function (data){
			console.log(data);
			if(data.code == 200){
				window.location.reload();
			}else{
				alert(data.msg);
			}
		}
	);
	// 关注
	// $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	// // 取消关注
	// $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");

}