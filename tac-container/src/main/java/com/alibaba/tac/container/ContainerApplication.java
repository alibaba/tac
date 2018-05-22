/*
 *   MIT License
 *
 *   Copyright (c) 2016 Alibaba Group
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.alibaba.tac.container;

import com.alibaba.tac.engine.bootlaucher.BootJarLaucherUtils;
import com.alibaba.tac.engine.code.CodeLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author jinshuan.li 07/02/2018 11:18
 */
@SpringBootApplication(scanBasePackages = {"com.alibaba.tac"})
@PropertySources({@PropertySource("application.properties")})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(ContainerBeanConfig.class)
@Slf4j
public class ContainerApplication {

    public static void main(String[] args) throws Exception {

        // the code must execute before spring start
        JarFile bootJarFile = BootJarLaucherUtils.getBootJarFile();
        if (bootJarFile != null) {
            BootJarLaucherUtils.unpackBootLibs(bootJarFile);
            log.debug("the temp tac lib folder:{}", BootJarLaucherUtils.getTempUnpackFolder());
        }
        SpringApplication springApplication = new SpringApplication(ContainerApplication.class);

        springApplication.setWebEnvironment(true);
        springApplication.setBannerMode(Banner.Mode.OFF);

        springApplication.addListeners(new ApplicationListener<ApplicationEnvironmentPreparedEvent>() {
            @Override
            public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
                CodeLoadService.changeClassLoader(event.getEnvironment());
            }
        });
        springApplication.run(args);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**");
            }
        };
    }
}
