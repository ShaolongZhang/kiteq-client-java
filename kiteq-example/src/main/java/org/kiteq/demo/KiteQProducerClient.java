package org.kiteq.demo;

import com.google.protobuf.ByteString;
import org.kiteq.client.DefaultKiteClient;
import org.kiteq.client.message.Message;
import org.kiteq.client.message.MessageListener;
import org.kiteq.client.message.SendResult;
import org.kiteq.client.message.TxResponse;
import org.kiteq.protocol.KiteRemoting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.kiteq.protocol.KiteRemoting.Header;

/**
 * blackbeans at 2015-04-08.
 */
public class KiteQProducerClient {


    public static void main(String[] args) throws Exception {

        //设置Producer发送的消息topic
        List<String> publishTopics = new ArrayList<String>();
        publishTopics.add("trade");

        DefaultKiteClient client = new DefaultKiteClient();
        client.setZkHosts("localhost:2181");
        client.setPublishTopics(publishTopics);
        client.setGroupId("p-kiteq-group");
        client.setSecretKey("default");
        client.setListener(new MessageListener() {
            @Override
            public boolean onMessage(Message message) {
                /**
                 *  Consumer接收消息的入口
                 *   之后再显示返回 true的情况下,才认为消息消费成功
                 *   否则 抛异常或者false的情况下,KiteQ会重投
                 */


                return true;
            }

            @Override
            public void onMessageCheck(TxResponse tx) {
                /**
                 *  作为消息生产方独有的回调方法,用于处理2PC消息,回馈给KiteQ本地事务是否成功
                 *  通过
                 *  tx.Commit()或者tx.Rollback()进程或者抛出异常
                 */


            }
        });

        //不要忘记init
        client.init();


        Header header = Header.newBuilder()
                //消息ID生成不包含-的UUID
                .setMessageId(UUID.randomUUID().toString().replace("-", ""))
                //当前消息的TOPIC
                .setTopic("trade")
                //当前消息的MessageType
                .setMessageType("pay-succ")
                //当前生产者的分组ID
                .setGroupId("p-kiteq-group")
                //当前消息的失效时间 秒为单位
                .setExpiredTime(System.currentTimeMillis() / 1000 + TimeUnit.MINUTES.toSeconds(10))
                //消息投递的最大次数
                .setDeliverLimit(100)
                //消息是否为2PC消息
                .setCommit(true)
                //是否不需要存储直接投递
                .setFly(false).build();

        KiteRemoting.BytesMessage message = KiteRemoting.BytesMessage.newBuilder()
                .setHeader(header)
                .setBody(ByteString.copyFromUtf8("hello KiteQ"))
                .build();

        long start = System.currentTimeMillis();
        SendResult result = null;
        try {
            result = client.sendBytesMessage(message);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (null != result && result.isSuccess()) {
                long cost = System.currentTimeMillis() - start;
                if (cost > 100) {
                    //TOO LONG
                }
            } else if (null != result && !result.isSuccess()) {
                //Send Message Fail   Retry
            } else {
                //Send Message But No Result   Retry
            }
        }

    }
}
