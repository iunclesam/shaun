package com.sheep.zookeeper.javaapiclient;

import org.apache.zookeeper.*;

import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁
 */
public class DistributeLock implements Lock{
    private ZooKeeper zooKeeper;
    private static final String ROOT_LOCK = "/LOCK";
    private String lockId;

    public DistributeLock() throws Exception{
        this.zooKeeper = ZookeeperClient.getInstance();
    }

    @Override
    public void lock() {
        try {
            while (!tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS)){
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            return tryLock(ZookeeperClient.getSessionTimeout(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {

        try {
            lockId = zooKeeper.create(ROOT_LOCK + "/", new byte[]{},
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            System.out.println(Thread.currentThread().getName() + "成功创建了lock节点[" + lockId + "]，开始去竞争");

            List<String> children = zooKeeper.getChildren(ROOT_LOCK, true);

            SortedSet<String> nodes = new TreeSet<>();

            for(String child : children){
                nodes.add(ROOT_LOCK + "/" + child);
            }

            String head = nodes.first();
            if (lockId.equals(head)) {
                System.out.println(Thread.currentThread().getName() + "成功获取锁[" + lockId + "]");
                return true;
            }else{
                SortedSet<String> headSet = nodes.headSet(lockId);

                if(!headSet.isEmpty()){

                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                    String last = headSet.last();

                    zooKeeper.exists(last, new Watcher() {
                        @Override
                        public void process(WatchedEvent watchedEvent) {
                            if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                                countDownLatch.countDown();
                            }
                        }
                    });
                    countDownLatch.await(time, unit);

                    System.out.println(Thread.currentThread().getName() + "成功获取锁[" + lockId + "]");
                    return true;
                }
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void unlock() {
        try {
            zooKeeper.delete(lockId, -1);
            System.out.println(Thread.currentThread().getName() + "成功删除锁[" + lockId + "]");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    public static void main(String[] args) {
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                DistributeLock lock = null;
                try {
                    lock = new DistributeLock();

                    countDownLatch.countDown();
                    countDownLatch.await();
                    lock.lock();
                    System.out.println(Thread.currentThread().getName());
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (lock != null)
                        lock.unlock();
                }
            }).start();
        }
    }
}
