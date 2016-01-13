package uuxia.utils;


import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by uuxia-mac on 15/8/29.
 * <p/>
 * BlockingQueue定义的常用方法如下：
 * <p/>
 * add(anObject)：
 * 把anObject加到BlockingQueue里，如果BlockingQueue可以容纳，则返回true，否则抛出异常。
 * <p/>
 * offer(anObject)：
 * 表示如果可能的话，将anObject加到BlockingQueue里，即如果BlockingQueue可以容纳，则返回true，否则返回false。
 * <p/>
 * put(anObject)：
 * 把anObject加到BlockingQueue里，如果BlockingQueue没有空间，则调用此方法的线程被阻断直到BlockingQueue里有空间再继续。
 * <p/>
 * poll(time)：
 * 取走BlockingQueue里排在首位的对象，若不能立即取出，则可以等time参数规定的时间，取不到时返回null。
 * <p/>
 * take()：
 * 取走BlockingQueue里排在首位的对象，若BlockingQueue为空，阻断进入等待状态直到BlockingQueue有新的对象被加入为止。
 * <p/>
 * BlockingQueue有四个具体的实现类，根据不同需求，选择不同的实现类：
 * <p/>
 * ArrayBlockingQueue：
 * 规定大小的BlockingQueue，其构造函数必须带一个int参数来指明其大小。其所含的对象是以FIFO（先入先出）顺序排序的。
 * <p/>
 * LinkedBlockingQueue：
 * 大小不定的BlockingQueue，若其构造函数带一个规定大小的参数，生成的BlockingQueue有大小限制，若不带大小参数，所生成的BlockingQueue的大小由Integer.MAX_VALUE来决定。其所含的对象是以FIFO顺序排序的。
 * <p/>
 * PriorityBlockingQueue：
 * 类似于LinkedBlockingQueue,但其所含对象的排序不是FIFO，而是依据对象的自然排序顺序或者是构造函数所带的
 * Comparator决定的顺序。
 * <p/>
 * SynchronousQueue：
 * 特殊的BlockingQueue，对其的操作必须是放和取交替完成的。
 * <p/>
 * LinkedBlockingQueue和ArrayBlockingQueue比较起来，它们背后所用的数据结构不一样，导致LinkedBlockingQueue的数据吞吐量要大于ArrayBlockingQueue，但在线程数量很大时其性能的可预见性低于ArrayBlockingQueue。
 */
public abstract class BaseThread extends Thread {
    protected static boolean runnable = true;
    protected static BlockingQueue<PacketBuffer> inQueue = new LinkedBlockingQueue<PacketBuffer>();
    protected static BlockingQueue<PacketBuffer> outQueue = new LinkedBlockingQueue<PacketBuffer>();
    protected DatagramSocket datagramSocket;

    public void close() {
        runnable = false;
        if (datagramSocket != null && datagramSocket.isBound()
                && datagramSocket.isClosed() == false) {
            datagramSocket.close();
        }
    }
}
