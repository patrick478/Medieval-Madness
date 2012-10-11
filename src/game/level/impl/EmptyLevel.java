package game.level.impl;

import java.util.ArrayList;

import game.entity.Entity;
import game.level.AbstractLevelPlanner;
import game.level.Floor;
import game.level.Level;

public class EmptyLevel extends AbstractLevelPlanner{

	public EmptyLevel(long _seed) {
		super(_seed);
	}

	@Override
	public Level designLevel(Floor _floor) {
		return new Level(_floor, new ArrayList<Entity>());
	}

}
