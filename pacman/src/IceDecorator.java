package src;

public class IceDecorator extends AutoplayerDecorator{
    public IceDecorator(Autoplayer autoplayer, Game game, PacActor pacActor) {
        super(autoplayer, game, pacActor);
    }

    @Override
    public void autoplay() {
        super.autoplay();
        getIce();
    }

    private void getIce() {
        // TODO: make pacman eat ice
    }
}
