interface AreaEvent {
    void process(Area area);
}

class AddPlayerEvent implements AreaEvent {
    private final Player player;

    public AddPlayerEvent(Player player) {
        this.player = player;
    }

    @Override
    public void process(Area area) {
        area.addPlayer(player);
    }
}

class RemovePlayerEvent implements AreaEvent {
    private final Player player;

    public RemovePlayerEvent(Player player) {
        this.player = player;
    }

    @Override
    public void process(Area area) {
        area.removePlayer(player);
    }
}


