package nro.server.service.model.entity.ai;

public interface AIStateHandler {

    void handle(AbstractAI ai);

    default void onEnter(AbstractAI ai) {}

    default void onExit(AbstractAI ai) {}

}