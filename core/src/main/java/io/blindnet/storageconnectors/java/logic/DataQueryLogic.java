package io.blindnet.storageconnectors.java.logic;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;
import io.blindnet.storageconnectors.java.datarequests.DataRequest;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestCallback;
import io.blindnet.storageconnectors.java.exceptions.WebSocketException;
import io.blindnet.storageconnectors.java.ws.packets.InPacketDataRequest;
import io.blindnet.storageconnectors.java.ws.packets.OutPacketData;
import io.blindnet.storageconnectors.java.ws.packets.OutPacketDataRequestReply;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class DataQueryLogic extends Logic {
    private static final int MAX_BLOCK_SIZE = 4194304;

    private final InPacketDataRequest packet;

    public DataQueryLogic(StorageConnectorImpl connector, InPacketDataRequest packet) {
        super(connector);

        this.packet = packet;
    }

    @Override
    public void run() throws WebSocketException {
        DataRequestReply reply = getConnector().getDataRequestHandler().handle(packet.getRequest());

        getConnector().sendPacket(new OutPacketDataRequestReply(packet.getRequest().getRequestId(), reply.getType()));

        if(packet.getRequest().getAction() == DataRequest.Action.GET && reply.getDataCallbackConsumer() != null) {
            reply.getDataCallbackConsumer().accept(new DataRequestCallback() {
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

                                getConnector().sendPacket(new OutPacketData(packet.getRequest().getRequestId(), block, !data.hasRemaining()));
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

                                getConnector().sendPacket(new OutPacketData(packet.getRequest().getRequestId(), block, eof));
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
