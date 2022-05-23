# wuyou-robot
基于[Simpler Robot](https://github.com/ForteScarlet/simpler-robot/tree/v2-dev)的机器人项目, [v3](https://github.com/wuyou-123/wuyou-robot-v3/)

# 如何开始?
1. 先学会`SpringBoot`框架
2. 所需环境: `Java1.8+`  `MySQL`  `Node.Js`  `Python3`
3. 打开`\src\main\resources\simbot-bots\`文件夹, 在里面创建一个`*.bot`文件, `*`可以是任意字符, [参考](https://www.yuque.com/simpler-robot/simpler-robot-doc/fk6o3e#iUKbX)
4. 打开`\src\main\resources\`文件夹, 在里面创建一个`application-*.yml`文件, 如果你会`springboot`, 应该能看懂这一步, 下面是本项目所需要的配置项
    | 配置名 | 描述 |
    | ---- | ---- |
    | MYSQL_HOST | 数据库地址 |
    | MYSQL_PORT | 数据库端口 |   
    | MYSQL_USERNAME | 数据库用户名 |   
    | MYSQL_PASSWORD | 数据库密码 |   
    | DATABASE | 数据库名, 项目的`\sql`目录有数据库结构文件 |   
    | QQ_UIN | QQ帐号, 因为项目里有点歌功能, 需要登录QQ音乐, 建议使用绿钻账号(没有的话我也没办法) |   
    | QQ_PWD | QQ密码, 原因同上 |
    | NET_EASE_UIN | 网易云账号, 原因还是同上, 本项目也接入了网易云音乐API :) |
    | NET_EASE_PWD | 网易云密码, 原因依然同上 |
    | TIANAPI_KEY | [天行数据](https://www.tianapi.com/)的`APIKEY`, 天行数据提供了很多API供开发者使用,本项目使用了其中的 [ip地址查询](https://www.tianapi.com/apiview/43) 以及 [天行机器人](https://www.tianapi.com/apiview/47) |
    
    复制模板
    ```yaml
    MYSQL_HOST: 127.0.0.1
    MYSQL_PORT: 3306
    MYSQL_USERNAME: root
    MYSQL_PASSWORD: 
    DATABASE: wuyou-robot
    QQ_UIN: 
    QQ_PWD: 
    NET_EASE_UIN: 
    NET_EASE_PWD: 
    TIANAPI_KEY: 
    ```
5. 创建`Python`环境(也可以不创建), 本项目需要使用`pip`安装`numpy` `PIL` `requests`
6. 其他的好像可能没有了
