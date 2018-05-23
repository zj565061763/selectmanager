# About
对选中和非选中状态进行了封装，可以方便的实现状态管理，当前支持的模式<br>
* 单选
* 单选必选
* 多选
* 多选必选

# Gradle
`implementation 'com.fanwe.android:selectmanager:1.0.9'`

# 效果
![](http://thumbsnap.com/i/sodYq9ca.gif?0522)
![](http://thumbsnap.com/i/vKHV9N5l.gif?0522)
![](http://thumbsnap.com/i/SQrJHAoa.gif?0522)
![](http://thumbsnap.com/i/Z9rxk88m.gif?0522)
<br>

1. 创建选择管理器
```java
private final FSelectManager<Button> mSelectManager = new FSelectManager<>();
```
