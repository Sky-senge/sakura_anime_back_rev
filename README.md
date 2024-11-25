# sakura_anime_backend

樱花动漫(毕设)-后端

## 运行需求/Requirements

Java 17，

Maven

已经配置好的ffmpeg（需要N卡加速编码）

MariaDB数据库

#### 注意：

**application.properties现在不建议受Git更新，避免由于开发组成员更新导致的启动问题。取而代之的是同目录下的** ```application.properties模板.temp``` **文件，它会提供一个很好的模板，下载之后请根据它来配置你的properties文件。**

为了确保你的git会排除掉这个配置文件，请你先在克隆/下载项目的目录下，执行一行Git命令，确保排除它。

```bash
git update-index --assume-unchanged src/main/resources/application.properties
```

如果有需要确实要更新它，那么请替换为以下参数

```bash
git update-index --no-assume-unchanged
```

## API文档

### 用户部分控制器：

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
  "avatar": "string", //头像文件名称标识
  "email": "string", //电邮
  "username": "string", //唯一用户名
  "permission": 0, //权限级别[0:管理员，1:普通用户,2:已被封锁]
  "password": "string", //密码，这个字段永远为空，保护隐私
  "displayName": "string", //显示名称，昵称
  "remarks": "string" //个性签名
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

[GET] /api/anime/getDetail

**说明：** 获取当前已登录用户的用户详情，只要带Token即可。

**响应：**

```json
{
  "status": true, //请求是否成功
  "data": { //当前用户的详情
    "id": 0,
    "avatar": "string",
    "email": "string",
    "username": "string",
    "permission": 0,
    "password": "string",
    "displayName": "string",
    "remarks": "string"
  },
  "message": "string", //请求成功的消息
  "error": "string" //错误消息（如有）
}
```

## 动漫部分控制器：

[POST] /api/anime/createAnime

**说明：** 新建一个动漫资源表，需要【管理员】权限

**请求：**

```json
{
  "name": "string", //动漫名称
  "tags": [ //动漫的标签，是包含多个词的数组，下面是个例子
    "日系",
    "治愈"
  ],
  "description": "string", //动漫介绍
  "rating": 0, //评分，在1~10之间的一位小数点，例如 9.5
  "filePath": "string" //不用管，上传文件之后会自动加的，填啥字符串都一样
}
```

**响应：**

```json
{
  "status": true, //是否成功？
  "data": {},
  "message": "string", //成功的消息
  "error": "string"
}
```

[POST] /api/anime/updateAnime

**说明：** 更新一个动漫资源，需要【管理员】权限

**请求：**

```json
{
  "id": 0, //需要更新ID为什么的动漫？
  "name": "string", //动漫名称
  "tags": [
    "string"
  ],
  "description": "string", //动漫说明
  "rating": 0, //评分
  "releaseDate": "2024-11-24T08:38:34.261Z", //发行时间，SQL自动生成，无需填写
  "filePath": [ //文件存放相关
    {
      "episodes": 1, //第几集，剧场版写1
      "fileName": "string" //实际视频流的文件夹
    }
  ]
}
```

**响应：**

```json
{
  "status": true, //是否成功？
  "data": {},
  "message": "string", //成功的消息
  "error": "string"
}
```

[GET] /api/anime/getDetail/{id}

**说明：** 根据动漫ID获取具体信息

**请求：**{id} 是动漫ID

**响应：**

```json
{
  "status": true, //是否获取成功？
  "data": { //动漫信息的对象
    "id": 0,
    "name": "string",
    "tags": [
      "string"
    ],
    "description": "string",
    "rating": 0,
    "releaseDate": "2024-11-24T08:41:22.180Z", 
    "filePath": [
      {
        "episodes": 0,
        "fileName": "string"
      }
    ]
  },
  "message": "string", //获取成功后的信息
  "error": "string" //错误发生后的消息（可能没有）
}
```

[GET] /api/anime/getAnimeList

**说明：** 分页查询动漫列表，仅限登录使用。

**请求参数：** /api/anime/getAnimeList? `page=第几页` & `size=每页几个数据`

**响应(以下面为例的对象列表)：**

```json
{
  "status": true,
  "data": [
    {
      "id": 0,
      "name": "string",
      "tags": [
        "string"
      ],
      "description": "string",
      "rating": 0,
      "releaseDate": "2024-11-24T08:45:13.905Z",
      "filePath": [
        {
          "episodes": 0,
          "fileName": "string"
        }
      ]
    }
  ],
  "message": "string",
  "error": "string"
}
```

[GET] /api/anime/getAllAnime

**说明：** 一次性查询所有动漫列表，不建议使用，仅限【管理员】使用。

**请求参数：** 无

**响应(以下面为例的对象列表)：**

```json
{
  "status": true,
  "data": [
    {
      "id": 0,
      "name": "string",
      "tags": [
        "string"
      ],
      "description": "string",
      "rating": 0,
      "releaseDate": "2024-11-24T08:45:13.905Z",
      "filePath": [
        {
          "episodes": 0,
          "fileName": "string"
        }
      ]
    }
  ],
  "message": "string",
  "error": "string"
}
```

[GET] /api/anime/deleteAnime/{ID}

**说明：** 删除某个动漫，仅限【管理员】使用。

**注意：** 删除动漫会导致其名下所有评论联动删除，谨慎使用！！

**请求：** {ID} 为动漫实际ID

**响应（以下面为例的对象列表）：**

```json
{
  "status": true, //是否成功
  "data": {},
  "message": "string",
  "error": "string"
}
```

## 文件控制器：

[POST] /files/getAvatar/{filename}

**说明：** 获取头像文件

**请求：** filename为头像文件路径，头像可通过用户相关API查表或者getDetail来获取

[POST] /files/uploadAnime

**说明：** 更新一个动漫视频文件，需要【管理员】权限

**请求：**

格式： Form-data（表单上传）

| 请求字段     | 请求内容         |
| -------- | ------------ |
| animeId  | 对应动漫id，数字    |
| episodes | 集数，剧场版就1集，数字 |
| file     | 文件内容，二进制视频文件 |

**响应：**

```json
{
  "status": true, //是否成功？
  "data": "String", //成功的消息
  "message": "string", //成功的消息
  "error": "string"
}
```

[POST] /files/uploadAvatar

**说明：** 更新一个用户头像文件，需要登录，仅限传自己的头像。

**请求：**

格式： Form-data（表单上传）

| 请求字段   | 请求内容          |
| ------ | ------------- |
| userId | 目标用户ID，填写自己的。 |
| file   | 文件内容，二进制图像文件  |

**响应：**

```json
{
  "status": true, //是否成功？
  "data": "String", //成功的消息
  "message": "string", //成功的消息
  "error": "string"
}
```

[POST] /files/uploadCover

**说明：** 更新一个动漫头像文件，需要【管理员】登录，以及【已经上传】的动漫资源。

**请求：**

格式： Form-data（表单上传）

| 请求字段    | 请求内容          |
| ------- | ------------- |
| animeId | 对应已上传资源的动漫ID。 |
| file    | 文件内容，二进制图像文件  |

**响应：**

```json
{
  "status": true, //是否成功？
  "data": "String", //成功的消息
  "message": "string", //成功的消息
  "error": "string"
}
```

[POST] /files/modAvatar

**说明：** 管理员修改头像，仅限【管理员】权限使用。

**请求：**

格式： Form-data（表单上传）

| 请求字段   | 请求内容           |
| ------ | -------------- |
| userId | 目标用户ID，填谁的都可以。 |
| file   | 文件内容，二进制图像文件   |

**响应：**

```json
{
  "status": true, //是否成功？
  "data": "String", //成功的消息
  "message": "string", //成功的消息
  "error": "string"
}
```

[GET] /files/getVideo/{requirements}/playlist.m3u8

**说明：** 获取动漫的播放列表，m3u8切片流

**请求：** {requirements} 为动漫路径字段，可在注释1的字段中找到```fileName```部分

注释1：

```json
"filePath": [
      {
        "episodes": 0,
        "fileName": "string"
      }
```

**响应：** 正常来说，是m3u8的文件内容，错误则返回错误响应体。

[GET] /files/getVideo/{requirements}/{tsFileName}

**说明：** 获取动漫的播放列表，m3u8切片流

**请求：** {requirements} 为动漫路径字段，可在注释1的字段中找到`fileName`部分

{tsFileName}为切片的ts文件名称，一般播放器会自动从playlist.m3u8中获取并请求。

注释1：

```json
"filePath": [
      {
        "episodes": 0,
        "fileName": "string"
      }
```

**响应：** ``` 二进制文件```

[GET] /files/getCover/{requirements}

**说明：** 获取动漫的封面，Content-Type应为 ```image/``` 开头

**请求：** {requirements} 为动漫路径字段，可在注释1的字段中找到`fileName`部分

请仅使用第一集的fineName进行查询，其他集数默认不放置封面。

注释1：

```json
"filePath": [
      {
        "episodes": 0,
        "fileName": "string"
      }
```

**响应：** `二进制文件`
