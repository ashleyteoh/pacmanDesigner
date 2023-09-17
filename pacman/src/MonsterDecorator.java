package src;

public class MonsterDecorator extends AutoplayerDecorator{

    public MonsterDecorator(Autoplayer autoplayer, Game game, PacActor pacActor) {
        super(autoplayer, game, pacActor);
    }

    @Override
    public void autoplay() {
        super.autoplay();
        avoidMonsters();
    }

    private void avoidMonsters() {
        // TODO: make pacman avoid monsters
    }
}
