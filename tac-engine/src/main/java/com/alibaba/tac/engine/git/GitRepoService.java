package com.alibaba.tac.engine.git;

import com.alibaba.tac.engine.properties.TacGitlabProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jinshuan.li 06/05/2018 14:27
 */
@Slf4j
@Service
public class GitRepoService {

    private final static String REMOTE_ORIGIN = "refs/remotes/origin/";

    private Pattern gitPattern = Pattern.compile("git@(?<hostName>[^:]+)(?<port>:[0-9]+:)?:(?<tail>.+)");

    @Resource
    private TacGitlabProperties tacGitlabProperties;

    /**
     * pull instance code
     *
     * @param gitURL
     * @param branch
     * @param projectName
     * @return
     */
    public String pullInstanceCode(String gitURL, String projectName, String branch) {

        String httpURL = change2Http(gitURL);

        String groupName = tacGitlabProperties.getGroupName();

        if (!localRepoExists(groupName, projectName, branch)) {
            cloneRepo(groupName, projectName, branch, httpURL);
        }

        // 拉分支数据

        return pullRepo(groupName, projectName, branch);

    }

    /**
     * 修改为http链接
     *
     * @param gitURL
     * @return
     */
    private String change2Http(String gitURL) {

        String realURL = gitURL;
        if (StringUtils.startsWithIgnoreCase(gitURL, "git@")) {
            Matcher matcher = gitPattern.matcher(gitURL);

            if (!matcher.matches()) {

                throw new IllegalStateException("invalid git url" + gitURL);

            }

            String hostName = matcher.group("hostName");
            String port = matcher.group("port");
            String tail = matcher.group("tail");

            if (StringUtils.isEmpty(port)) {
                realURL = String.format("http://%s/%s", hostName, tail);
            } else {
                realURL = String.format("http://%s:%s/%s", hostName, port, tail);
            }

        }

        return realURL;
    }

    /**
     * 拉取代码数据
     *
     * @param groupName
     * @param projectName
     * @param branch
     */
    private String pullRepo(String groupName, String projectName, String branch) {

        Git localGit = null;
        String localRepoDir = this.getLocalPath(groupName, projectName, branch);
        PullResult pullResult = null;
        try {
            localGit = Git.open(new File(localRepoDir));
            PullCommand pullCommand = localGit.pull();

            pullCommand.setRemote("origin").setCredentialsProvider(
                new UsernamePasswordCredentialsProvider(tacGitlabProperties.getUserName(),
                    tacGitlabProperties.getPassword())).setTimeout(30);

            pullResult = pullCommand.call();
            String header = localGit.getRepository().getBranch();
            //如果当前分支header引用和branch分支不一致，切换分支
            if (!branch.equals(header)) {
                //检测当前分支是否存在，不存在，返回异常
                Ref remoteRef = localGit.getRepository().exactRef(REMOTE_ORIGIN + branch);
                if (null == remoteRef) {
                    log.error("[Git Refresh Source] specified branch {} remote origin not exist.", branch);
                    throw new IllegalStateException("remote origin not exist " + branch);
                }
                localGit.checkout().setForce(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .setStartPoint(
                        REMOTE_ORIGIN + branch).setName(branch).call();
            }

        } catch (Exception e) {

            log.error("[Git Refresh Source] pull result error  path:{} {}", localRepoDir, e.getMessage(), e);

            throw new IllegalStateException("pull source failed " + e.getMessage());
        }
        if (null == pullResult || !pullResult.isSuccessful()) {
            log.error("[Git Refresh Source] pull result failed");
            throw new IllegalStateException("pull source failed " + localRepoDir);
        }

        return localRepoDir;

    }

    /**
     * clone 代码仓库
     *
     * @param groupName
     * @param projectName
     * @param branch
     */
    private String cloneRepo(String groupName, String projectName, String branch, String gitURL) {

        String remoteURL = gitURL;

        String localPath = getLocalPath(groupName, projectName, branch);

        CloneCommand cloneCommand = Git.cloneRepository().setURI(remoteURL).setBranch(branch).setDirectory(
            new File(localPath)).setTimeout(30);
        Git git = null;
        cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(tacGitlabProperties.getUserName(),
            tacGitlabProperties.getPassword()));

        try {
            git = cloneCommand.call();

            // 取分支
            String existBranch = git.getRepository().getBranch();
            if (!StringUtils.equals(branch, existBranch)) {
                throw new IllegalStateException(String.format("branch %s not exist", branch));
            }
        } catch (Exception e) {
            log.error("[Git Refresh Source] clone repository error . remote:{} {}", remoteURL, e.getMessage(), e);

            throw new IllegalStateException("clone repository error ." + e.getMessage());
        }

        return localPath;
    }

    /**
     * 本地数据是否存在
     *
     * @param groupName
     * @param projectName
     * @param branch
     * @return
     */
    public Boolean localRepoExists(String groupName, String projectName, String branch) {

        File file = new File(getLocalPath(groupName, projectName, branch) + ".git");

        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 本地代码路径
     *
     * @param groupName
     * @param projectName
     * @param branch
     * @return
     */
    private String getLocalPath(String groupName, String projectName, String branch) {

        StringBuilder localRepoDir = new StringBuilder(tacGitlabProperties.getBasePath());
        localRepoDir.append(File.separator).append(groupName).append(File.separator).append(projectName).append(
            File.separator).append(branch).append(File.separator);

        return localRepoDir.toString();
    }
}
