//let page = "/pages/mntr";
// let page = "/pages/mntr";
//
// $(".login-btn").on("click", function () {
//     let flag = false;
//     if ($('#id').val() == "") {
//         alert("아이디를 입력하세요");
//         return false;
//     } else if ($('#pwd').val() == "") {
//         alert("비밀번호를 입력하세요");
//         return false;
//     }
//     const sendData = {
//         userId: $("#id").val(),
//         password: $("#pwd").val()
//     }
//     $.ajax({
//         url: "/auth/generateToken",
//         type: "POST",
//         data: sendData,
//         success: function (resultData) {
//
//             let keys = Object.keys(resultData);
//
//             keys.forEach(key => {
//                 if (key === 'accessToken') {
//                     flag = true;
//                     let date=new Date();
//                     date.setDate(date.getDate() + 1000* 60 * 15);
//                     document.cookie = key + '=' + resultData[key]+';'+`Expires=${date.toUTCString()}`;
//
//                 }
//             });
//             //   document.location=page;
//
//             if (flag === true) {
//                 document.location = page;
//             } else {
//                 document.location.reload();
//
//             }
//
//         },
//         error : (jqXHR, textStatus, errorThrown) => {
//             if(jqXHR.status === 403) {
//                 comm.showAlert("아이디 혹은 비밀번호가 일치하지 않습니다.");
//             } else if(jqXHR.status === 500) {
//                 comm.showAlert("서버 내 에러가 발생했습니다.");
//             } else if(jqXHR.status === 0){
//                 comm.showAlert("네트워크가 연결되지 않았거나, 불안정합니다.");
//             }
//         }
//     });
//
//     return false;
// });
