package com.ardorcraft.player;

import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.Vector3;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardorcraft.control.FlyControl;
import com.ardorcraft.control.WalkControl;
import com.ardorcraft.world.BlockWorld;

public class PlayerWithPhysics extends PlayerWithCollision {
	private boolean jump = false;
	private double time;
	private final double stepTime = 1.0 / 100.0;
	private boolean walking = false;
	private final Vector3 damping = new Vector3(0.85, 1.0, 0.85);
	private final double gravity = -0.3;
	private final double jumpForce = 9;
	private final double speed = 0.9;

	private final WalkControl walkControl;
	private final FlyControl flyControl;

	public PlayerWithPhysics(final LogicalLayer logicalLayer) {
		flyControl = FlyControl.setupTriggers(this, logicalLayer,
				Vector3.UNIT_Y, false);
		// flyControl.setMoveSpeed(50);
		flyControl.disable();
		walkControl = WalkControl.setupTriggers(this, logicalLayer,
				Vector3.UNIT_Y, false);
	}

	@Override
	public void update(final BlockWorld blockScene, final ReadOnlyTimer timer) {
		final double tpf = timer.getTimePerFrame();

		time += tpf;

		int ticks = 0;
		while (time > stepTime) {
			final double step = stepTime;

			getVelocity().addLocal(getAcceleration().multiply(speed, null));
			if (walking) {
				getVelocity().addLocal(0, gravity, 0);
				if (jump && isOnGround()) {
					getVelocity().addLocal(0, jumpForce, 0);
				}
			}
			getPosition().addLocal(getVelocity().multiply(step, null));

			getVelocity().multiplyLocal(damping);

			// Update collision etc
			super.update(blockScene, timer);

			time -= stepTime;
			ticks++;
			jump = false;
		}
		if (ticks > 0) {
			getAcceleration().set(0, 0, 0);
		}
		jump = false;
	}

	double t = 0;

	public void jump() {
		if (!jump && isOnGround()) {
			jump = true;
		}
	}

	public boolean isWalking() {
		return walking;
	}

	public void setWalking(final boolean walking) {
		this.walking = walking;
		if (isWalking()) {
			walkControl.enable();
			flyControl.disable();
		} else {
			walkControl.disable();
			flyControl.enable();
		}
	}

	public WalkControl getWalkControl() {
		return walkControl;
	}

	public FlyControl getFlyControl() {
		return flyControl;
	}
}