### 释放sentos虚拟机占用的宿主机硬盘

因为虚拟机的磁盘是动态分配的，也就是你用多少它占用多少空间，但是呢。这个动态分配只会越来越大，不会缩小。所以当虚拟机用时间久了之后，宿主机的硬盘空间会越来越小，此时就算把虚拟机中的视频删了，但是虚拟磁盘扩大了，无法变回原来的大小，所以硬盘还是无法释放。所以我们可以通过`BoxManage`命令进行清理。

##### Linux环境

-   在虚拟机中执行以下命令，执行完后将虚拟机关机。

    ```shell
    sudo dd if=/dev/zero of=/empty
    sudo rm -f /empty
    ```

-   然后进入宿主机 `VBoxManage.exe` 安装目录，执行以下命令

    ```shell
    ##### E:\VMS\docker-vm\docker-vm.vdi 为虚拟机虚拟文件绝对路径
    VBoxManage.exe modifyhd E:\VMS\docker-vm\docker-vm.vdi --compact
    ```

    

    

