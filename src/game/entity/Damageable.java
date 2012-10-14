package game.entity;

public interface Damageable {
	public void applyHealthDelta(int _delta);
	public int getTotalHealth();
	public int getCurrentHealth();
}
