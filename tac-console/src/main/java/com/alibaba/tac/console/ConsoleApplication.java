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

package com.alibaba.tac.console;

import com.alibaba.tac.console.sdk.MenuOptionHandler;
import com.alibaba.tac.engine.bootlaucher.BootJarLaucherUtils;
import com.alibaba.tac.engine.code.CodeLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * @author jinshuan.li 07/02/2018 11:18
 */
@SpringBootApplication(scanBasePackages = "${tac.app.scan.packages}")
@PropertySources({@PropertySource("application.properties"), @PropertySource("tac-console.properties")})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(ConsoleBeanConfig.class)
@Slf4j
public class ConsoleApplication implements CommandLineRunner {

    @Resource
    private ApplicationArguments applicationArguments;

    @Resource
    private MenuOptionHandler menuOptionHandler;

    public static void main(String[] args)
        throws Exception {

        // parse the args
        ApplicationArguments arguments = new DefaultApplicationArguments(args);
        boolean help = arguments.containsOption(ConsoleConstants.MENU_HELP);
        if (help) {
            MenuOptionHandler.printUsage();
            return;
        }

        // the code must execute before spring start
        JarFile bootJarFile = BootJarLaucherUtils.getBootJarFile();
        if (bootJarFile != null) {
            BootJarLaucherUtils.unpackBootLibs(bootJarFile);
            log.debug("the temp tac lib folder:{}", BootJarLaucherUtils.getTempUnpackFolder());
        }


        // get command args and start spring boot
        Boolean webEnv = false;
        String additionProfile = ConsoleConstants.ADDDITION_PROFILE_SIMPLE;
        if (arguments.containsOption(ConsoleConstants.OPTION_ADMIN)) {
            webEnv = true;
            additionProfile = ConsoleConstants.ADDDITION_PROFILE_ADMIN;
        }

        SpringApplication springApplication = new SpringApplication(ConsoleApplication.class);
        springApplication.setWebEnvironment(webEnv);
        springApplication.setBannerMode(Banner.Mode.OFF);

        if (!webEnv) {
            // command model
            springApplication.setAdditionalProfiles(additionProfile);
        } else {
            // web model
            springApplication.setAdditionalProfiles(additionProfile);
        }

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
                registry.addMapping("/api/**").allowedMethods("GET", "POST", "PUT", "DELETE");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                super.addResourceHandlers(registry);
                registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
            }
        };
    }

    @Bean
    public ExitCodeGenerator exitCodeGenerator() {

        return new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                return 0;
            }
        };
    }

    @Override
    public void run(String... args) throws Exception {

        // handle the command
        Boolean webEnv = false;
        if (applicationArguments.containsOption(ConsoleConstants.OPTION_ADMIN)) {
            webEnv = true;
        }
        if (!webEnv) {
            menuOptionHandler.handleMenuOption();
        }

    }

}
