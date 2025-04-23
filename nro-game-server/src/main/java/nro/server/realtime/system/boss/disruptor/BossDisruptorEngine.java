package nro.server.realtime.system.boss.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Getter;
import nro.server.service.model.entity.ai.boss.Boss;

import java.util.concurrent.Executors;

public class BossDisruptorEngine {

    @Getter
    private static final BossDisruptorEngine instance = new BossDisruptorEngine();

    private final RingBuffer<BossAIEvent> ringBuffer;

    private BossDisruptorEngine() {
        Disruptor<BossAIEvent> disruptor = new Disruptor<>(
                new BossAIEventFactory(),
                2048,
                Executors.defaultThreadFactory(),
                ProducerType.MULTI,
                new SleepingWaitStrategy()
        );
        disruptor.handleEventsWith(new BossAIEventHandler());
        disruptor.start();

        ringBuffer = disruptor.getRingBuffer();
    }

    public void submit(Boss boss) {
        long sequence = ringBuffer.next();
        try {
            BossAIEvent event = ringBuffer.get(sequence);
            event.boss = boss;
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}