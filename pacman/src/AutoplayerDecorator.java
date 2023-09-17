package src;

public abstract class AutoplayerDecorator implements Autoplayer{
    protected Autoplayer autoplayer;
    Game game;
    PacActor pacActor;
    public AutoplayerDecorator(Autoplayer autoplayer, Game game, PacActor pacActor) {
        this.autoplayer = autoplayer;
        this.game = game;
        this.pacActor = pacActor;
    }

    @Override
    public void autoplay() {
        autoplayer.autoplay();
    }

}
