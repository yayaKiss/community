$(function (){
    $("#uploadForm").submit(upload);
});

function upload(){
    $.ajax({
        url: "http://upload-z2.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function (data){
            console.log(data);
            if(data && data.code == 200){
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {fileName: $("input[name='key']").val()},
                    function (data){
                        if(data.code == 200){
                            window.location.reload();
                        }else{
                            alert("头像更换失败!");
                        }
                    }
                );
            }else{
                alert("头像上传失败!");
            }
        }
    });

    return false;
}