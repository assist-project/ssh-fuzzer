package learner;

import com.beust.jcommander.Parameter;

public class SshMapperConfig {

    @Parameter(names = "-sshMapperAddress", required = true, description = "The address of the SSH mapper. Format: ip:port")
    private String mapperAddress;

    @Parameter(names = "-sshMapperCommand", required = false, description = "The command used to launch the SSH mapper at the start of each learning experiment")
    private String mapperCommand;

    @Parameter(names = "-sshMapperStartWait", required = false, description = "The time (ms) to wait for the SSH mapper process to start")
    private Long mapperStartWait = 1000L;

    public String getMapperAddress() {
        return mapperAddress;
    }

    public String getMapperCommand() {
        return mapperCommand;
    }

    public Long getMapperStartWait() {
        return mapperStartWait;
    }
}
