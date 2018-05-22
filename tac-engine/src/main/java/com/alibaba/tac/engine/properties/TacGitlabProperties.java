package com.alibaba.tac.engine.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jinshuan.li 06/05/2018 14:30
 */
@Data
@Component
@ConfigurationProperties(prefix = "tac.gitlab.config")
public class TacGitlabProperties {

    private String hostURL = "http://127.0.0.1/";

    private String token = "t_bj6gJywKH2fCkbWY7k";

    private String groupName = "tac-admin";

    private String userName = "tac-admin";

    private String password = "tac-admin";

    private String basePath = "/home/admin/tac/git_codes";

}
