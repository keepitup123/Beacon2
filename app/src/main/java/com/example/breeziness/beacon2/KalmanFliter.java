package com.example.breeziness.beacon2;
/**
 * 朱红喜 2018-11-11
 * 卡尔曼滤波器
 * 转移矩阵，测量矩阵，控制向量(没有的话，就是0)，过程噪声协方差矩阵，测量噪声协方差矩阵，后验错误协方差矩阵，前一状态校正后的值，当前观察值
 *
 * X(k|k-1) = AX(k-1|k-1) + BU(k) + W(k)         线性差分方程
 * Z(k)=H X(k)+V(k)                               测量结果
 * X(k)是k时刻的系统状态，U(k)是k时刻对系统的控制量，
 * A和B是系统参数，对于多模型系统，它们为矩阵。而Z(k)是k时刻的测量值，H是测量系统的参数，
 * 对于多测量系统，H为矩阵。W(k)和V(k)分别表示过程和测量的噪声。
 * 他们被假设成高斯白噪声(White Gaussian Noise)，
 * 他们的协方差(covariance)分别是Q，R（这里我们假设他们不随系统状态变化而变化）
 *
 *
 * P(k|k-1) = AP(k-1|k-1)A' + Q(k)                 P(k|k-1)是X(k|k-1)对应的协方差，P(k-1|k-1)是X(k-1|k-1)对应的协方差，A’表示A的转置矩阵，Q是系统过程的协方差。
 *
 * X(k|k) = X(k|k-1) + Kg(k)[Z(k) - HX(k|k-1)]     其中Kg为卡尔曼增益(Kalman Gain)
 *
 * Kg(k)=P(k|k-1)H'/[HP(k|k-1)H' + R]              卡尔曼增益的计算
 *
 * P(k|k) = (I - Kg(k)H)P(k|k-1)                   更新k状态下X(k|k)的协方差，为了要令卡尔曼滤波器不断的运行下去直到系统过程结束
 *                                                 其中I为1的矩阵，对于单模型单测量，I=1。当系统进入k+1状态时，P(k|k)就是式子(8)的P(k-1|k-1)。这样，算法就可以自回归的运算下去
 *
 *
 */

import java.util.ArrayList;

public class KalmanFliter {

    private final double Q = 0.000001;   //过程噪声协方差   这个需要自己确定周围的噪声误差
    private final double R = 1.350;     //测量噪声协方差    这个需要自己测定周围的噪声误差  初始0.001


    private ArrayList<Double> dataArrayList;  //运算队列
    private int length;   //队列长度

    private double z[]; // data   //Z[K]  k时刻测量数据数组
    private double xhat[];  // X[k|k]  K时刻最优值
    private double xhatminus[];  //   X[k|k-1]由k-1时刻推测的结果
    private double P[];  //    P[k|k]   k时刻协方差
    private double Pminus[];  //   P[k|k-1]    k-时刻推测k协方差
    private double K[];  //  Kg   卡尔曼增益系数

    /**
     * 构造方法初始化相应的数组
     * @param arrayList
     */
    public KalmanFliter(ArrayList<Double> arrayList) {

        this.dataArrayList = arrayList;//实例化list

        this.length = arrayList.size();
        z = new double[length];
        xhat = new double[length];
        xhatminus = new double[length];
        P = new double[length];
        Pminus = new double[length];
        K = new double[length];

        xhat[0] = 0;//初始化成标准高斯分布  均值 = 0
        P[0] = 1.0;  //方差 = 1


        //将测到的数据塞入测量数组
        for (int i = 0; i < length; i++) {
            z[i] = (double) dataArrayList.get(i);
        }
    }

    /**
     * 计算方法体
     * @return
     */
    public ArrayList<Double> calc() {

        if (dataArrayList.size() < 2) {  //如果list元素只有一个直接返回
            return dataArrayList;
        }

        for (int k = 1; k < length; k++) {
            // X(k|k-1) = AX(k-1|k-1) + BU(k) + W(k),A=1,BU(k) = 0  其中B是控制系数，本系统无控制所以为B = 0，A为系统系数，这里暂时取A = 1
            xhatminus[k] = xhat[k - 1];//由k-1时刻推算k时刻的结果

            // P(k|k-1) = AP(k-1|k-1)A' + Q(k) ,A=1
            Pminus[k] = P[k - 1] + Q;  //从k-1时刻计算k时刻的协方差的

            // Kg(k)=P(k|k-1)H'/[HP(k|k-1)H' + R],H=1
            K[k] = Pminus[k] / (Pminus[k] + R);   //计算K时刻卡尔曼系数

            // X(k|k) = X(k|k-1) + Kg(k)[Z(k) - HX(k|k-1)], H=1        Z[k] = H*X[k]+V[K]   Z[K]是k时刻的测量值,H是测量系统的参数，对于多测量系统，H为矩阵
            xhat[k] = xhatminus[k] + K[k] * (z[k] - xhatminus[k]);   //计算K时刻的最优值

            //P(k|k) = (1 - Kg(k)H)P(k|k-1), H=1
            P[k] = (1 - K[k]) * Pminus[k];          //更新K时刻的协方差
        }

        for (int i = 0; i < length; i++) {
            dataArrayList.set(i, xhat[i]);
        }
        dataArrayList.remove(0);//去掉第一个数据，是初始值 运算从下标一开始
        return dataArrayList;
    }
}
