# 阿里云盘列表程序 - Spring Boot

## 演示及开发环境

> https://pan.rawchen.com
>
> JDK 8 + IDEA 2021.2.3

## 使用

* 确保java8及以上环境，在[releases](https://github.com/rawchen/AliPan/releases)页面下载最新jar运行文件（最好下载最新源码后mvn打包）。
* 通过`java -jar AliPan.jar`运行该程序，默认访问地址：[http://localhost:8899](http://localhost:8899)。
* 通过以下refresh_token获取方法拿到后，粘贴到jar包同路径下生成的配置文件`AliPanConfig`和`AliPanConfigOpen`中（可记事本编辑）。

## 开发

* 确保java8及以上环境，下载项目后通过IDEA打开，可配置application.yml中的端口号等，通过Maven打包jar运行，没有IDEA也可配置Maven命令执行`mvn clean package`。

**refresh_token获取方法(两个都要获取一次)：**
> https://api.rawchen.com/alipan <br> https://pan.rawchen.com/token


## 功能（持续开发中）
* - [x] Thymeleaf模板引擎
* - [x] 展示文件夹与文件列表
* - [x] 文件在线查看（各类图片、文档、文本、音频、视频、PDF等）
* - [x] 分享链接
* - [x] 目录树
* - [x] README.md
* - [x] 目录树监听
* - [x] 右键弹出菜单
* - [x] 文件夹加密
* - [x] 后台自定义配置
* - [x] 后退监听
* - [x] RefreshToken续期
* - [x] 手机谷歌、夸克浏览器下载
* - [x] 调用PotPlayer
* - [x] 调用迅雷下载
* - [x] 缩略/列表模式切换
* - [ ] 批量下载
* - [ ] 批量压缩下载
* - [ ] 图片目录上下一张

## 高级

使用时如需对文件夹进行描述说明，渲染Markdown。可在此文件夹内上传名为`README.md`的文件（不区分大小写）。

使用时如需密码保护一个文件夹及可在电脑上创建名为`password`的无任何后缀的文本文件，并通过记事本打开输入你要设定的密码。然后上传到云盘需要加密的文件夹内。开发时在`application.yml`可自定义加密文件的名称。

开发时在`application.yml`中你可以自定义`parent_file_id`(要展示的根文件夹id，可通过对文件夹鼠标右键点击分享粘贴链接后查看id，默认为整个云盘根目录root)。

## 问题反馈

Q: 为什么要获取两个refresh_token存到两个文件？
<br>
A: 因为云盘开放平台支持标准OAuth2.0授权协议管理，易于管理大部分接口，所以使用的开放平台应用授权。但是原生文档预览接口开放平台并不提供需要原生接口支持，所以要获取原生refresh_token。

## 案例截图

![](https://cdn.jsdelivr.net/gh/rawchen/JsDelivr/static/AliPan/01.png)

![](https://cdn.jsdelivr.net/gh/rawchen/JsDelivr/static/AliPan/02.png)

![](https://cdn.jsdelivr.net/gh/rawchen/JsDelivr/static/AliPan/03.png)