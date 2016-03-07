package com.maloc.client.bean;

/**
 * 地磁向量
 * @author xhw
 *
 */
public class MagneticVector {

	float vector[];
	long timestamp;
	public MagneticVector(float[] vector,long timestamp) {
		super();
		this.vector = vector;
		this.timestamp=timestamp;
	}

	

	public MagneticVector(float x, float y, float z, long timestamp) {
		this.vector = new float[3];
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;
		this.timestamp = timestamp;
	}
	public MagneticVector(String str) {
		String vStrs[]=str.split(",");
		vector=new float[3];
		for(int i=0;i<3;i++)
		{
			vector[i]=Float.parseFloat(vStrs[i]);
		}
	}

	public MagneticVector(float[] vector2) {

		this(vector2,0l);
	}



	public String toString() {
		return vector[0] + "," + vector[1] + "," + vector[2];
	}

	public double module() {
		double sum = 0;
		for (int i = 0; i < 3; i++) {
			sum += vector[i] * vector[i];
		}
		return Math.sqrt(sum);
	}

	public double angle(MagneticVector other) {
		double innerCross = 0;
		double sum1 = 0, sum2 = 0;
		for (int i = 0; i < vector.length; i++) {
			innerCross += vector[i] * other.vector[i];
			sum1 += vector[i] * vector[i];
			sum2 += other.vector[i] * other.vector[i];
		}

		double cos = innerCross / (Math.sqrt(sum1) * Math.sqrt(sum2));
		if (degreeChangeDirection(this.vector, other.vector))
			return Math.acos(cos) / Math.PI * 180;
		else
			return -Math.acos(cos) / Math.PI * 180;
		// return cos*100;
	}

	float gravityVector[] = { 0, 0, 1 };

	// 方向变化为顺时针返回true，逆时针返回false
	public boolean degreeChangeDirection(float horVector[], float biject[]) {
		float crs[] = cross(horVector, biject);
		return isSameDirection(crs, gravityVector);
	}

	// 判读两个向量是否方向一致
	public boolean isSameDirection(float[] v1, float[] v2) {
		float f = dot(v1, v2);
		if (f > 0)
			return true;
		else
			return false;
	}

	// 求两个向量的叉乘
	public float[] cross(float[] v1, float[] v2) {
		if (v1.length != 3 || v2.length != 3)
			return null;
		float[] v = new float[3];
		v[0] = v2[2] * v1[1] - v2[1] * v1[2];
		v[1] = -(v2[2] * v1[0] - v2[0] * v1[2]);
		v[2] = v2[1] * v1[0] - v2[0] * v1[1];

		return v;
	}

	// 求两个向量的点积
	private float dot(float[] v1, float[] v2) {
		float innerCross = 0;
		for (int i = 0; i < v1.length; i++) {
			innerCross += v1[i] * v2[i];
		}

		return innerCross;
	}

	public float[] getVector() {
		return vector;
	}

	public void setVector(float[] vector) {
		this.vector = vector;
	}



	public long getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


}
