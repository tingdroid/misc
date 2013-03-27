package com.ting.scene;

public class Pointer {
	private float x = -1;
	private float y = -1;
	private float dx = 0;
	private float dy = 0;
	private float dz = 1;


	public void down(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void up() {
		this.x = -1;
		this.y = -1;
	}

	public void move(float x, float y) {
		if (!isDown()) return;
		dx += x - this.x;
		dy += y - this.y;
		this.x = x;
		this.y = y;
	}

	public void moveBy(float dx, float dy) {
		this.dx += dx;
		this.dy += dy;
	}

	public void zoomBy(float dz) {
		this.dz *= dz;
	}

	public boolean isDown() {
		return this.x != -1;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getDX() {
		float dx = this.dx;
		this.dx = 0;
		return dx;
	}

	public float getDY() {
		float dy = this.dy;
		this.dy = 0;
		return dy;
	}

	public float getDZ() {
		float dz = this.dz;
		this.dz = 1;
		return dz;
	}

}
