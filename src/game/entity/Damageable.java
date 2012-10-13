package game.entity;

public interface Damageable {
	public void applyDamage(double _damage);
	public double getTotalHealth();
	public double getCurrentHealth();
}
