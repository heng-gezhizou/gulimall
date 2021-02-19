package com.adtec.gulimall.product.web;

import com.adtec.gulimall.product.entity.CategoryEntity;
import com.adtec.gulimall.product.service.CategoryService;
import com.adtec.gulimall.product.vo.Catalog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product")
public class Index {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping({"/","/index.html"})
    public String index(Model model){
        List<CategoryEntity> list =  categoryService.getLevel1Categorys();
        model.addAttribute("categorys",list);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String,List<Catalog2Vo>> getCatalog(){
        Map<String,List<Catalog2Vo>> map = categoryService.getCatalog();
        return map;
    }

    @GetMapping("/lock")
    @ResponseBody
    public String testLock(){
        RLock lock = redissonClient.getLock("lock");
        lock.lock();
        try {
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
        return "Hello World!";
    }

    @GetMapping("/redis")
    @ResponseBody
    public String testRedis(){
        String name = redisTemplate.opsForValue().get("name");
        return name;
    }

    //写锁测试
    @GetMapping("/write")
    @ResponseBody
    public String write(){
        RReadWriteLock writeLock = redissonClient.getReadWriteLock("read_write");
        RLock lock = writeLock.writeLock();
        lock.lock();
        try{
            redisTemplate.opsForValue().set("read_write_test","读写锁测试");
            System.out.println("写锁线程id："+Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
        return "写锁释放了";
    }

    //读锁测试
    @GetMapping("/read")
    @ResponseBody
    public String read(){
        RReadWriteLock readLock = redissonClient.getReadWriteLock("read_write");
        String result = "";
        RLock lock = readLock.readLock();
        lock.lock();
        try{
            result = redisTemplate.opsForValue().get("read_write_test");
            System.out.println("读锁线程id："+Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
        return result;
    }

//    @GetMapping("/park")
//    @ResponseBody
//    public String park(){
//        RSemaphore park = redissonClient.getSemaphore("park");
//        park.acquire(3);
//    }

    //闭锁测试
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        //设置一个数，当这个数减为0时释放闭锁
        door.trySetCount(5L);
        //等待闭锁完成
        door.await();
        return "放假了。。。";
    }

    //闭锁测试
    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String go(@PathVariable("id") Long id) throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        //计数减一
        door.countDown();
        return id+"班学生都走完了。。。";
    }

    //信号量测试
    //信号量可用作分布式限流
    @GetMapping("/park")
    @ResponseBody
    public String park(){
        RSemaphore park = redissonClient.getSemaphore("park");
        boolean b = park.tryAcquire();
        if(b){
            return "还有车位，停车啦。。。";
        }else{
            return "没有车位了，去别的停车场停吧。。。";
        }
    }

    @GetMapping("/go")
    @ResponseBody
    public String go(){
        RSemaphore park = redissonClient.getSemaphore("park");
        //释放一个车位
        park.release();
        return "有车开走了，释放了一个车位。。";
    }

}
