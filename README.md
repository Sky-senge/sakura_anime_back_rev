# sakura_anime_backend

樱花动漫(毕设)-后端

## 运行需求/Requirements

Java 17，

Maven

已经配置好的ffmpeg（需要N卡加速编码）

MariaDB数据库

#### 注意：

**application.properties现在不会受Git更新，避免由于开发组成员更新导致的启动问题。取而代之的是同目录下的** ```application.properties模板.temp``` **文件，它会提供一个很好的模板，下载之后请根据它来配置你的properties文件。**

## API文档

### User部分控制器

[POST] /api/user/login

**说明：** 用户登录，无需权限

**请求：**

```json
{
    "username":"需要登录的用户名",
    "password":"登录的密码"
}
```

**响应：**

```json
{
  "status": true, //是否成功，基本上所有和文本/JSON返回相关的都由此状态
  "data": { //数据部分，如果接口有数据返回，那么会封装在data对象中
    "token": "string", //JWT Token，请放在名为“Authorization”的头部
    "userId": 0 //登录后的UID,最好把它存下来，接下来很多操作都需要
  },
  "message": "string", //接口本身的响应
  "error": "string" //如果发生错误，这里会出现错误原因（有时候可能会没有，建议检查status字段为准）
}
```

[POST] /api/user/register

**说明：** 用户注册，无需权限

**请求：**

```json
{
  "email": "string", //注册邮箱（唯一）
  "username": "string", //用户名（唯一）
  "permission": 1, //权限，新注册用户默认都是用户（1），你填什么数字都没用
  "password": "string", //密码
  "displayName": "string", //显示的名称，就像昵称一样
  "remarks": "string" //个性签名之类的
}
```

**响应：**

```json
{
  "status": true, //注册是否成功？
  "data": {},
  "message": "string", //注册成功的消息
  "error": "string"
}
```

[POST] /api/user/modPassword

**说明：** 用户修改密码，需要【本人】登录

**请求：**

```json
{
  "id": 0, //欲修改的UID，填登陆用户的，不然会拒
  "password": "string"
}
```

**响应：**

```json
{
  "status": true, //是否修改成功的状态
  "data": {},
  "message": "string", //成功后的提示词
  "error": "string" //错误发生的提示词，可能没有
}
```

[POST] /api/user/updateUser

**说明：** 更新用户信息用的，仅限【管理员】登录使用。

注意！针对管理员的请求不做校验，管理员提交时请不要使用null！否则真的会变成null

如果不需要更改密码，则可用把密码留空（空字符串，空格不算空字符串哦）

**请求：**

```json
{
  "id": 0,
  "avatar": "string",
  "email": "string",
  "username": "string",
  "permission": 0, //权限级别[0:管理员，1:普通用户,2:已被封锁]
  "password": "string",
  "displayName": "string",
  "remarks": "string"
}
```

**响应：**

```json
{
  "status": true, //是否修改成功的状态
  "data": {},
  "message": "string", //成功后的提示词
  "error": "string" //错误发生的提示词，可能没有
}
```

[GET] /api/user/deleteUser/{UID}

**说明：** 删除某个用户，仅限【管理员】使用。

**注意：** 删除用户会导致其名下所有评论联动删除，谨慎使用！！

**请求：** {UID替换为用户ID}

**响应（以下面为例的对象列表）：**

```json
{
  "status": true,
  "data": [
    {
      "id": 0,
      "avatar": "string",
      "email": "string",
      "username": "string",
      "permission": 0,
      "password": "string", //这个部分始终为null，保护用户隐私
      "displayName": "string",
      "remarks": "string"
    }
  ],
  "message": "string",
  "error": "string"
}
```

[GET] /api/user/getUserList

**说明：** 分页查询用户列表，仅限登录使用。

**请求：** /api/user/getUserList? ```page=第几页``` & ```size=每页几个数据```

**响应(以下面为例的对象列表)：**

```json
{
  "status": true,
  "data": [
    {
      "id": 0,
      "avatar": "string",
      "email": "string",
      "username": "string",
      "permission": 0,
      "password": "string", //这个部分始终为null，保护用户隐私
      "displayName": "string",
      "remarks": "string"
    }
  ],
  "message": "string",
  "error": "string"
}
```

[GET] /api/user/getAllUsers

**说明：** 查询用户全表，仅限【管理员】使用，但不建议用，还是用上面的分页查询罢。

**请求：** 如URL

**响应（以下面为例的对象列表）：**

```json
{
  "status": true,
  "data": [
    {
      "id": 0,
      "avatar": "string",
      "email": "string",
      "username": "string",
      "permission": 0,
      "password": "string", //这个部分始终为null，保护用户隐私
      "displayName": "string",
      "remarks": "string"
    }
  ],
  "message": "string",
  "error": "string"
}
```
