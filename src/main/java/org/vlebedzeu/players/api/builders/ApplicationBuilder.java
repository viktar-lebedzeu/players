package org.vlebedzeu.players.api.builders;

import org.apache.commons.lang3.StringUtils;
import org.vlebedzeu.players.api.Channel;
import org.vlebedzeu.players.api.impl.DefaultChannelImpl;
import org.vlebedzeu.players.api.impl.MessageSourceStaticImpl;
import org.vlebedzeu.players.api.players.PrimaryPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class ApplicationBuilder {
    private final String channelOpt;
    private final String playersOpt;
    private final String messageOpt;

    private Channel channel;

    public ApplicationBuilder(String channelOpt, String playersOpt, String messageOpt) {
        this.channelOpt = channelOpt;
        this.playersOpt = playersOpt;
        this.messageOpt = messageOpt;
    }

    public ApplicationBuilder build() {
        // Check for different types of channels
        String[] messages = StringUtils.split(messageOpt, ",");
        MessageSourceStaticImpl messageSource = new MessageSourceStaticImpl(messages);

        List<String> playerIds = Arrays.stream(StringUtils.split(playersOpt, ","))
                .collect(Collectors.toList());
        DefaultChannelImpl channelImpl = new DefaultChannelImpl();
        String primaryId = playerIds.remove(0);
        channelImpl.initPrimaryPlayer(primaryId, messageSource, 10);
        channelImpl.initSecondaryPlayers(playerIds);
        channel = channelImpl;
        return this;
    }

    public void start() {
        channel.start();
    }
}
