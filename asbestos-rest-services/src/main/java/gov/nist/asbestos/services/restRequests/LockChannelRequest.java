package gov.nist.asbestos.services.restRequests;

import gov.nist.asbestos.asbestosProxy.channel.ChannelControl;
import gov.nist.asbestos.client.Base.Request;
import gov.nist.asbestos.client.log.SimStore;
import gov.nist.asbestos.client.channel.ChannelConfig;
import gov.nist.asbestos.client.channel.ChannelConfigFactory;
import gov.nist.asbestos.simapi.simCommon.SimId;
import gov.nist.asbestos.simapi.simCommon.TestSession;
import org.apache.commons.io.IOUtils;
import java.util.logging.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
// 0 - empty
// 1 - app context
// 2 - "channelLock"
// Create a channel based on JSON configuration in request

public class LockChannelRequest {
    private static Logger log = Logger.getLogger(LockChannelRequest.class.getName());

    private Request request;

    public static boolean isRequest(Request request) {
        if (request.uriParts.size() == 3) {
            String uriPart2 = request.uriParts.get(2);
            return "channelLock".equals(uriPart2);
        }
        return false;
    }

    public LockChannelRequest(Request request) {
        request.setType(this.getClass().getSimpleName());
        this.request = request;
    }

    public void run() throws IOException {
        request.announce("LockChannel");
        String string = IOUtils.toString(request.req.getInputStream(), Charset.defaultCharset());   // json
        ChannelConfig channelConfigInRequest = ChannelConfigFactory.convert(string);

        ChannelConfig channelConfig = ChannelControl.channelConfigFromChannelId(request.externalCache, channelConfigInRequest.asFullId());
        if (channelConfig.isWriteLocked() != channelConfigInRequest.isWriteLocked()) {
            channelConfig.setWriteLocked(channelConfigInRequest.isWriteLocked());
            SimStore simStore = new SimStore(request.externalCache,
                    new SimId(new TestSession(channelConfig.getTestSession()),
                            channelConfig.asChannelId(),
                            channelConfig.getActorType(),
                            channelConfig.getEnvironment(),
                            true));
            simStore.create(channelConfig);
            log.info("Channel " + simStore.getChannelId().toString() + " write protect updated to: '" + channelConfig.isWriteLocked() + "'" );
        } else {
            log.info("Write protection was not modified because the configuration value is already the same.");
        }
        request.ok();
    }
}
