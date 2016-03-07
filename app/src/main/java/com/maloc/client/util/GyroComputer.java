package com.maloc.client.util;

import java.util.Arrays;

import com.maloc.client.bean.AngularData;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
/**
 * 陀螺仪工具类，对陀螺仪输出进行积分，计算两步之间朝向的变化
 * @author xhw
 *
 */
public class GyroComputer {

	private float timestamp = 0;
	private double totalDegreeChanged = 0;
	
	private final float[] deltaRotationVector = new float[4];
	private float[] horVector = new float[3];
	private float angle[] = new float[3];
	
	public static final float EPSILON = 180;
	private static final float NS2S = 1.0f / 1000000000.0f;
	
	public void initialize()
	{
		this.totalDegreeChanged=0;
		this.timestamp=0;
	}
	
	public AngularData angleChangedAroundGravity(SensorEvent event,
			float gravityVector[]) {
		AngularData data=null;
		// This timestep's delta rotation to be multiplied by the current
		// rotation
		// after computing it from the gyro sample data.
		if (timestamp != 0) {
			data=new AngularData();
			final float dT = (event.timestamp - timestamp) * NS2S;
			data.dT=dT;
			// Axis of the rotation sample, not normalized yet.
			float axisX = event.values[0];
			float axisY = event.values[1];
			float axisZ = event.values[2];
			//angle[0] = event.values[0] * dT;
			//angle[1] = event.values[1] * dT;
			//angle[2] = event.values[2] * dT;
			// this.totalDegreeChanged+=angle[0]/Math.PI*180;
			// String angles=angle[0]+" "+angle[1]+" "+angle[2];
			// label.setText(angles);
			// Calculate the angular speed of the sample
			float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY
					* axisY + axisZ * axisZ);

			// Normalize the rotation vector if it's big enough to get the axis
			// (that is, EPSILON should represent your maximum allowable margin
			// of error)
			if (omegaMagnitude > EPSILON) {
				axisX /= omegaMagnitude;
				axisY /= omegaMagnitude;
				axisZ /= omegaMagnitude;
			}
			//totalDegreeChanged += (omegaMagnitude)/180*Math.PI;
			// Integrate around this axis with the angular speed by the timestep
			// in order to get a delta rotation from this sample over the
			// timestep
			// We will convert this axis-angle representation of the delta
			// rotation
			// into a quaternion before turning it into the rotation matrix.
			float thetaOverTwo = omegaMagnitude * dT / 2.0f;
			float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
			float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
			deltaRotationVector[0] = sinThetaOverTwo * axisX;
			deltaRotationVector[1] = sinThetaOverTwo * axisY;
			deltaRotationVector[2] = sinThetaOverTwo * axisZ;
			deltaRotationVector[3] = cosThetaOverTwo;

			float[] deltaRotationMatrix = new float[9];
			SensorManager.getRotationMatrixFromVector(deltaRotationMatrix,
					deltaRotationVector);
			// User code should concatenate the delta rotation we computed with
			// the current rotation
			// in order to get the updated rotation.
			// rotationCurrent = rotationCurrent * deltaRotationMatrix;
			
			int index[] = getSortIndex(gravityVector);
			if (gravityVector[index[2]] != 0 && gravityVector[index[1]] != 0) {

				horVector[index[2]] = -1.0f / gravityVector[index[2]];
				horVector[index[1]] = 1.0f / gravityVector[index[1]];
				horVector[index[0]] = 0;

				// info.append("gravityVector: "+gravityVector[0]+","+gravityVector[1]+","+gravityVector[2]+"\n");
				// info.append("horVector: "+horVector[0]+","+horVector[1]+","+horVector[2]+"\n");

				float[] current = VectorMultiplication(horVector,
						deltaRotationMatrix);

				float[] biject = BijectVector(current, gravityVector);

				double degreeChanged = intersectionAngle(horVector, biject);
				if (!degreeChangeDirection(horVector, biject, gravityVector))
					degreeChanged = -degreeChanged;
				// info.append("degreeChanged:"+degreeChanged+"\n");
				if (Math.abs(degreeChanged) >= 0)
					totalDegreeChanged += degreeChanged;
				data.totalAngleChanged=this.totalDegreeChanged;
				data.angleChanged=degreeChanged;
				// info.append("total degreeChanged:"+this.totalDegreeChanged);
			}
		}
		timestamp = event.timestamp;

		return data;
	}

	// 方向变化为顺时针返回true，逆时针返回false
	public boolean degreeChangeDirection(float horVector[], float biject[],
			float gravityVector[]) {
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

	// 求两个向量的夹角，单位是度
	private double intersectionAngle(float v1[], float v2[]) {
		double innerCross = 0;
		double sum1 = 0, sum2 = 0;
		for (int i = 0; i < v1.length; i++) {
			innerCross += v1[i] * v2[i];
			sum1 += v1[i] * v1[i];
			sum2 += v2[i] * v2[i];
		}

		double cos = innerCross / (Math.sqrt(sum1) * Math.sqrt(sum2));
		return Math.acos(cos);
	}

	// 求向量在水平面上的投影
	private float[] BijectVector(float[] current, float[] gravityVector) {

		float n[] = normalise(gravityVector);
		float h = dot(n, current);
		float hn[] = new float[3];
		for (int i = 0; i < 3; i++) {
			hn[i] = current[i] - n[i] * h;
		}
		return hn;
	}

	// 求两个向量的点积
	private float dot(float[] v1, float[] v2) {
		float innerCross = 0;
		for (int i = 0; i < v1.length; i++) {
			innerCross += v1[i] * v2[i];
		}

		return innerCross;
	}

	// 单位化向量
	private float[] normalise(float v1[]) {
		float v[] = new float[3];
		float sum = 0;
		for (int i = 0; i < v.length; i++) {
			v[i] = v1[i];
			sum += v[i] * v[i];
		}
		double module = Math.sqrt(sum);
		for (int i = 0; i < v.length; i++) {
			v[i] /= module;
		}
		return v;
	}

	// 返回一个数组成员的大小序号的一个数组
	private int[] getSortIndex(float[] gv) {

		int idx[] = new int[gv.length];
		Arrays.fill(idx, -1);
		int counter = 0;
		for (int i = 0; i < gv.length; i++) {
			counter = 0;
			for (int j = 0; j < gv.length; j++) {
				if (gv[i] > gv[j]) {
					counter++;
				}
			}
			//assert idx[counter]==-1;
			//while (idx[counter] != -1)
			//	counter++;
			idx[counter] = i;
		}
		return idx;
	}

	// 求一个3*3矩阵乘以一个3维向量
	private float[] VectorMultiplication(float[] hv, float[] deltaRotationMatrix) {

		float temp[] = new float[3];
		for (int i = 0; i < 3; i++) {
			float sum = 0;
			for (int j = 0; j < 3; j++) {
				sum += deltaRotationMatrix[i * 3 + j] * hv[j];
			}
			temp[i] = sum;
		}

		return temp;
	}
}
