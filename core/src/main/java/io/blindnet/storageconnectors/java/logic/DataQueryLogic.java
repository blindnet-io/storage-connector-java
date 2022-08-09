package io.blindnet.storageconnectors.java.logic;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReply;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryCallback;
import io.blindnet.storageconnectors.java.exceptions.WebSocketException;
import io.blindnet.storageconnectors.java.ws.packets.InPacketDataQuery;
import io.blindnet.storageconnectors.java.ws.packets.OutPacketData;
import io.blindnet.storageconnectors.java.ws.packets.OutPacketDataReply;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class DataQueryLogic extends Logic {
    private static final int MAX_BLOCK_SIZE = 4194304;

    private final InPacketDataQuery packet;

    public DataQueryLogic(StorageConnectorImpl connector, InPacketDataQuery packet) {
        super(connector);

        this.packet = packet;
    }

    @Override
    public void run() throws WebSocketException {
        DataQueryReply reply;
        switch (packet.getAction()) {
            case GET:
                reply = getConnector().getDataQueryHandler().get(packet.getQuery());
                break;
            case DELETE:
                reply = getConnector().getDataQueryHandler().delete(packet.getQuery());
                break;
            default:
                throw new WebSocketException("Unknown data query action");
        }

        getConnector().sendPacket(new OutPacketDataReply(packet.getQuery().getRequestId(), reply.getType()));

        if(packet.getAction() == InPacketDataQuery.Action.GET && reply.getDataCallbackConsumer() != null) {
            reply.getDataCallbackConsumer().accept(new DataQueryCallback() {
                @Override
                public void sendData(byte[] data) {
                    sendData(ByteBuffer.wrap(data));
                }

                @Override
                public void sendData(ByteBuffer data) {
                    getConnector().getExecutorService().execute(() -> {
                        try {
                            do {
                                byte[] block = new byte[Math.min(MAX_BLOCK_SIZE, data.remaining())];
                                data.get(block);

                                getConnector().sendPacket(new OutPacketData(packet.getQuery().getRequestId(), block, !data.hasRemaining()));
                            } while(data.hasRemaining());
                        } catch (WebSocketException e) {
                            getConnector().onError(e);
                        }
                    });
                }

                @Override
                public void sendData(InputStream dataStream) {
                    getConnector().getExecutorService().execute(() -> {
                        try {
                            BufferedInputStream bis = new BufferedInputStream(dataStream);
                            boolean eof = false;

                            while(!eof) {
                                byte[] block = bis.readNBytes(MAX_BLOCK_SIZE);
                                bis.mark(1);
                                eof = bis.read() == -1;
                                bis.reset();

                                getConnector().sendPacket(new OutPacketData(packet.getQuery().getRequestId(), block, eof));
                            }
                        } catch(IOException e) {
                            getConnector().onError(e);
                        }
                    });
                }
            });
        }
    }
}
