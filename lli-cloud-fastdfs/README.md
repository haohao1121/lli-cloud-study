### FastDFS 简单用法  
#### 功能描述  
> 文件单个.批量上传功能  
> 采用MongoDB保存上传文件的相关信息,方便后期文件查找,当然也可以使用Redis或者MySQL处理.  
> 根据文件唯一号(Mongo主键)下载文件  
#### docker部署 
* 制作镜像,源码地址: https://github.com/happyfish100/fastdfs.git  
* 如果使用docker默认网络,会出现storage的IP地址映射宿主地址问题,可参照此方法处理(未测试),链接地址:https://www.cnblogs.com/zzsdream/p/11199374.html  
* 选择 docker 映射 host网络模式可以避免这个问题,或者k8s部署(未测试)