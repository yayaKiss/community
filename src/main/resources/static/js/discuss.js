function like(btn,entityType,entityId,entityUserId,postId){
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data){
            console.log(data);
            if(data.code == 200){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1 ? '已赞':'赞');
            }else{
                alert(data.msg);
            }
        }

    );
}