# MyRPCTest
自定义实现rpc框架

## 技术选择
1. 网络传输：Bio Nio Netty
2. 序列化：JDK自带序列化 Kryo Protobuf
3. 代理 : 静态代理 动态代理JDK 动态代理CGLIB 
4. 注册中心 : Zookeeper Curator Alibaba Nacos 
5. 传输协议 : 自己构造 
6. 负载均衡 : 随机均衡策略 获取次数均衡策略 一致性哈希均衡策略 
7. 压缩机制 : BZip Deflater GZip Lz4 Zip 霍夫曼编码实现编解码器
