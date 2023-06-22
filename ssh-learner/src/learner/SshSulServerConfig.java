package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulServerConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.config.MapperConnectionConfig;

public class SshSulServerConfig extends SulServerConfig  implements SshMapperAddress {

    @Override
    public void applyDelegate(MapperConnectionConfig config) {
    }

}
