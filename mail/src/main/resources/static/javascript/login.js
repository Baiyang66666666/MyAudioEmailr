
function selectComponent(){
    if (checkDeviceIdCookie()) {
        sendRequest()
    } else {
        generateDeviceId()
        sendRequest()
    }
}

function sendRequest() {
    let deviceId = getDeviceId()
    fetch('/audioEmail/login/deviceId', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({"deviceId":deviceId})
    })
        .then(response => {
            if (response.ok) {
                //TODO 登录成功
                // console.log("登陆成功！");
                // var accountList = response.account;
                window.location.href = '/audioEmail/login/setAccount';
            } else {
                //TODO 登录失败
                console.error("登录失败！");
            }
        })
        .catch(error => {
            // 处理请求异常
            console.error("请求失败：" + error.message);
        });
}

function getDeviceId() {
    let name = "deviceId";
    console.log(document.cookie)
    const value = "; " + document.cookie;
    const parts = value.split("; " + name + "=");
    if (parts.length === 2) {
        return parts.pop().split(";").shift();
    }
}

function generateDeviceId() {
    const now = new Date();
    const dateStr = now.getFullYear().toString().padStart(4, '0')
        + (now.getMonth() + 1).toString().padStart(2, '0')
        + now.getDate().toString().padStart(2, '0')
        + now.getHours().toString().padStart(2, '0')
        + now.getMinutes().toString().padStart(2, '0');
    const characters = 'abcdefghijklmnopqrstuvwxyz';
    let randomStr = '';
    for (let i = 0; i < 5; i++) {
        randomStr += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    // Generate Device IdyyyymmddHHMM-randomStr(len=5)
    const deviceId = dateStr + '-' + randomStr;
    //Set expire
    let date = new Date();
    date.setTime(date.getTime() + (30 * 24 * 60 * 60 * 1000 ));
    let expires = "expires=" + date.toUTCString();
    document.cookie = "deviceId=" + deviceId + ";" + expires + ";path=/";
}

function checkDeviceIdCookie() {
    let cookieValue = "; " + document.cookie;
    let matches = cookieValue.match(/; deviceId=([^;]+)/);
    if (matches) {
        return true;
    } else {
        return false;
    }
}