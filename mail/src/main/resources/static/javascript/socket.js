function createSocket(userId) {
    let socket = new WebSocket('ws://localhost:8083/audioEmail/schedule/'+userId)
    socket.addEventListener('open', function (event) {
        console.log('WebSocket connect successfully');
        console.log("Url: ws:localhost:8083/audioEmail/schedule/{userid}")
    });
    // 添加消息接收事件监听器
    return socket
}


function sendSchedule(socket, expression, user_id) {
    console.log(expression)
    const message = {
        destination: 'audioEmail/schedule/' + user_id,
        body: JSON.stringify(expression)
    }
    socket.send(JSON.stringify(message))
    console.log("Send msg:" + expression)
}

function getSchedule() {

}


function scheduledTime(mailsId) {
    // Read the Mail
}