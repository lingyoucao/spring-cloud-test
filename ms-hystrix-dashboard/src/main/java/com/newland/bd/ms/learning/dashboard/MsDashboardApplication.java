package com.newland.bd.ms.learning.dashboard;

import com.newland.bd.ms.learning.dashboard.inti.TurbineMetris;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
@EnableHystrixDashboard
@EnableTurbine
public class MsDashboardApplication {
	// 直接监控某服务的hystrix: http://localhost:10000/hystrix.stream

	// DashBoard: http://localhost:10000/hystrix
	// 单台监控：在界面输入 http://localhost:10000/hystrix  还是
	// 多台监控（通过turbine）：http://localhost:10000/turbine.stream
	public static void main(String[] args) {
		SpringApplication.run(MsDashboardApplication.class, args);
		TurbineMetris.start();
	}
}
