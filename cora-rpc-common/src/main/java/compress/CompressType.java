package compress;

//实现压缩的接口

/**
 * @author C0ra1
 */
public interface CompressType {
    //压缩方法
    byte[] compress(byte[] bytes);

    //解压方法
    byte[] deCompress(byte[] bytes);
}
