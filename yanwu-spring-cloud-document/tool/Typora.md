#### Typora插入图片居左

>   直接在使用的主题`css`文件的末尾加上以下代码，然后关闭`Typora`重新打开即可
>
>   ![image-20210203153436819](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/02/2021020315343636.png)

```css
p .md-image:only-child {
    width: auto;
    text-align: left;
}
```



#### Typora设置背景图片

>   打开主题文件夹，新建背景图片存放文件夹，将背景图片放到新建的文件夹中，然后在主题`css`文件添加以下代码，最后关掉`Typora`重新打开即可生效
>
>   ![image-20210203154934221](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/02/2021020315493434.png)

```css
content {
  background: url("./image/{xxxxx.png}") no-repeat 55% 90% transparent;
  background-size: cover;
}
```
