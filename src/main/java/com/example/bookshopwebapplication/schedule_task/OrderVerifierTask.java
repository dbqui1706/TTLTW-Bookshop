package com.example.bookshopwebapplication.schedule_task;

import com.example.bookshopwebapplication.dto.OrderDto;
import com.example.bookshopwebapplication.service.OrderHashService;
import com.example.bookshopwebapplication.service.OrderService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@WebListener
public class OrderVerifierTask implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    private int repeatInSeconds = 15;
    private int delayFirstRunInSeconds = 5;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            OrderService orderService = new OrderService();
            OrderHashService orderHashService = new OrderHashService();
            List<OrderDto> orders = orderService.getPart(orderService.count(), 0);
            for(OrderDto order : orders){
                if(order.getIsVerified() == 1){
                    int tampered = (orderHashService.verifyOrderById(order.getId())) ? 0 : 1;
                    order.setIsTampered(tampered);
                    orderService.update(order);
                }
            }
        };
        scheduler.scheduleAtFixedRate(task, delayFirstRunInSeconds, repeatInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
