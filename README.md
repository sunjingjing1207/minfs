### Minfs_student实现

#### dataServer
* 开放read，write接口
* 定期向zk汇报自身情况，包括是否存活，文件数量，磁盘使用量
* 定期删除无效文件
### metaServer
* 将元数据存储到zk中
* 定期向zk汇报自身情况
* 开放文件和文件夹操作接口
* 支持三副本读写
* 支持文件负载均衡
* 定期fsck+recovery
* 多实例部署，主服务故障后自动切换
### easyClient
* 封装metaServer和DataServer的SDK