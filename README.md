# Face-Recognition-Health-Code-APP
健康码系统

本项目为基于RT-Thread与人脸识别系统的智能测温基站的手机APP源码

### 项目简介

本作品以STM32开发板作为内核，运行RT-Thread操作系统，并利用智云平台（zcloud）进行云存储。我们配套开发了Android程序和Web端管理员网页，实现了信息的查询。通过以上的系统，测温人只需要在手机APP上注册人脸信息，人脸信息就会保存到我们的后端数据库中。当某个测温站点探测到人脸信息后将上传给后端，后端将会在数据库中进行信息的匹配，之后通过匹配结果记录一个人的轨迹。当我们需要获取一个人的行程轨迹时，只需要登录Android手机应用，即可看到该人在什么时间出现在了什么地点，便于行程的统计，避免了行程、数据等的造假。

### APP实现功能

app主要实现移动客户端需求，包括注册，上传和下载人脸图片，健康码查询，行程轨迹以及体温检测的功能。

### API设置

### POST /api/user/login

登录

#### 请求

```json
{
    "username": "用户名",
    "password": "密码明文"
}
```

#### 回复

如果非200，则内容直接为错误文本

如果为200，则

```json
{
    "ok": true/false,
    "message": "ok为false时，表示错误信息"
}
```

### POST /api/user/register

注册

#### 请求

```json
{
    "username": "用户名",
    "password": "密码明文",
    "realname": "实名",
    "phone": "电话",
    "ID": "身份证号"
}
```

#### 回复

如果非200，则内容直接为错误文本

如果为200，则

```json
{
    "ok": true/false,
    "message": "ok为false时，表示错误信息"
}
```

### POST /api/user/query

查询记录，需要登录

#### 请求

无

#### 回复

如果非200，则内容直接为错误文本

如果为200，则

```json
{
    "ok": false,
    "message": "ok为false时，表示错误信息"
}
```

或

```json
{
    "ok": true,
    "data": [
        {
            "timestamp": "发生的时间戳, Number， 秒",
            "location": "地点",
            "temperature": "体温，number"
        }
    ]
}
```


### POST /api/user/upload_image

上传图片，需要登陆

#### 请求

form-data, 里面的第一个会被作为图片内容

#### 回复

如果非200，则内容直接为错误文本

如果为200，则

```json
{
    "ok": false,
    "message": "ok为false时，表示错误信息"
}
```

或

```json
{
    "ok": true,
}
```


### GET /api/user/get_image

下载图片，需要登陆
管理员可以下载所有人的，非管理员只能下载自己的

#### 请求

URL Query params

- uid: 要下载的用户ID。不填则为自己

#### 举例

GET /api/user/upload_image?uid=123，下载uid为123的用户的图片

#### 回复

如果非200，则内容直接为错误文本

如果为200，则返回内容为文件。


### POST /api/user/get_current_user

获取当前用户信息，需要登陆

#### 请求

无

#### 回复

如果非200，则内容直接为错误文本

如果为200，则

```json
{
    "ok": false,
    "message": "ok为false时，表示错误信息"
}
```

或

```json
{
    "ok": true,
    "data": {
        "uid": "用户ID，number",
        "username": "用户名",
        "realname": "实名",
        "phone": "电话",
        "ID": "身份证号"
    }
}
```

你仅需要修改 NetUtil.java 第32行public static String urlBase ="..."，加上你部署的网络接口即可使用


