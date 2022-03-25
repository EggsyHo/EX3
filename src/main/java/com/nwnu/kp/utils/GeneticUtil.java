package com.nwnu.kp.utils;

import org.springframework.boot.SpringApplication;

import java.io.File;
import java.util.Random;

public class GeneticUtil {
    static int Weight[];
    static int Value[];
    static int m,n;
    static int f[];
    static int IPath[];
    static String SPath="";
    static int CLength; //染色体长度
    static int Scale; //种群规模
    static int Gen; //运行代数
    static int BestG; //最佳出现代数
    static int BestLength; //最佳编码价值；
    static int BestTour[]; //最佳编码
    static int BestStr[];
    static int OldPop[][]; //初始种群、父代种群
    static int NewPop[][]; //新种群、子代种群
    static int Fit[]; //种群适应度
    static int FitStr[];
    static double Prob[]; //种群个体累计概率
    static double ProbC; //交叉概率
    static double ProbM; //变异概率
    static int t; //当前代数
    static Random Ran;

    public GeneticUtil(int s,int l,int g,double pc,double pm) {
        Scale=s;
        CLength=l;
        Gen=g;
        ProbC=pc;
        ProbM=pm;
    }

    public void SaveData(int mNow,int nNow,String[] WeightList,String[] ValueList) {
        m=mNow;
        n=nNow;
        Weight=new int[10010];
        Value=new int[10010];
        for (int i=0;i<WeightList.length;i++) {
            Weight[i+1]=Integer.valueOf(WeightList[i]);
            Value[i+1]=Integer.valueOf(ValueList[i]);
        }
    }

    private void Init() {
        BestLength=0;
        BestTour=new int[CLength];
        BestStr=new int[CLength];
        IPath=new int[CLength];
        BestG=0;
        t=0;
        NewPop=new int[Scale+1][CLength];
        OldPop=new int[Scale+1][CLength];
        Fit=new int[Scale];
        FitStr=new int[Scale];
        Prob=new double[Scale];
        Ran=new Random(System.currentTimeMillis());
    }

    /*
    初始化种群
     */
    private void InitGroup() {
        for (int k=0;k<Scale;k++) {
            for (int i=0;i<CLength;i++) {
                OldPop[k][i]=Ran.nextInt(65535)%2;
            }
        }
    }

    private int Evaluate(int Chrom[]) {
        int v=0;
        int w=0;
        for (int i=0;i<CLength;i++) {
            if (Chrom[i]==1) {
                v+=Value[i];
                w+=Weight[i];
            }
        }
        if (w>m) {
            return 0;
        } else {
            return v;
        }
    }

    /*
    计算累计概率
     */
    private void CountProb() {
        double SumFit=0.0;
        int TempF[]=new int[Scale];
        for (int i=0;i<Scale;i++) {
            TempF[i]=Fit[i];
            SumFit+=TempF[i];
        }
        Prob[0]=(double)(TempF[0]/SumFit);
        for (int i=1;i<Scale;i++) {
            Prob[i]=(double)(TempF[i]/SumFit+Prob[i-1]);
        }
    }

    /*
    挑选最优个体，复制到子代
     */
    private void SeleBest() {
        int MaxEval,MaxId;
        int MaxStr[]=null;
        MaxId=0;
        MaxEval=Fit[0];
        for (int i=1;i<Scale;i++) {
            if (MaxEval<Fit[i]) {
                MaxEval=Fit[i];
                MaxId=i;
            }
        }
        if (BestLength<MaxEval) {
            BestLength=MaxEval;
            BestG=t;
            for (int k=0;k<CLength;k++) {
                BestTour[k]=OldPop[MaxId][k];
            }
        }
        CopyGro(0,MaxId);
    }

    /*
    复制染色体
     */
    private void CopyGro(int from,int to) {
        for (int i=0;i<CLength;i++) {
            NewPop[from][i]=OldPop[to][i];
        }
    }

    /*
    赌轮选择策略
     */
    private void Sele() {
        int SeleID;
        double Ran1;
        for (int i=1;i<Scale;i++) {
            Ran1=(double)(Ran.nextInt(65535)%1000/1000.0);
            int k;
            for (k=0;k<Scale;k++) {
                if (Ran1<=Prob[k]) {
                    break;
                }
            }
            SeleID=k;
            CopyGro(i,SeleID);
        }
    }

    /*
    进化
     */
    private void Evo() {
        SeleBest();
        Sele();
        double r;
        for (int k=0;k<Scale;k+=2) {
            r=Ran.nextDouble();
            if (r<ProbC) {
                Cross(k,k+1);
            } else {
                r=Ran.nextDouble();
                if (r<ProbM) {
                    Conv(k);
                }
                r=Ran.nextDouble();
                if (r<ProbM) {
                    Conv(k+1);
                }
            }
        }
    }

    /*
    两点交叉算子
     */
    private void Cross(int k1,int k2) {
        int Ran1,Ran2;
        Ran1=Ran.nextInt(65535)%CLength;
        Ran2=Ran.nextInt(65535)%CLength;
        while (Ran1==Ran2) {
            Ran2=Ran.nextInt(65535)%CLength;
        }
        if (Ran1>Ran2) {
            int Temp=Ran1;
            Ran1=Ran2;
            Ran2=Temp;
        }
        int Flag=Ran2-Ran1+1;
        for (int i=0,j=Ran1;i<Flag;i++,j++) {
            int Temp=NewPop[k1][j];
            NewPop[k1][j]=NewPop[k2][j];
            NewPop[k2][j]=Temp;
        }
    }

    /*
    多次对换变异算子
     */
    private void Conv(int k) {
        int Ran1,Ran2,Count;
        Count=Ran.nextInt(65535)%CLength;
        for (int i=0;i<Count;i++) {
            Ran1=Ran.nextInt(65535)%CLength;
            Ran2=Ran.nextInt(65535)%CLength;
            while (Ran1==Ran2) {
                Ran2=Ran.nextInt(65535)%CLength;
            }
            int Temp=NewPop[k][Ran1];
            NewPop[k][Ran1]=NewPop[k][Ran2];
            NewPop[k][Ran2]=Temp;
        }
    }

    private void RunGenetic() {
        InitGroup();
        for (int i=0;i<Scale;i++) {
            Fit[i]=Evaluate(OldPop[i]);
        }
        CountProb();
        for (int i=0;i<Gen;i++) {
            Evo();
            for (int k=0;k<Scale;k++) {
                for (int j=0;j<CLength;j++) {
                    OldPop[k][j]=NewPop[k][j];
                }
            }
            for (int k=0;k<Scale;k++) {
                Fit[k]=Evaluate(OldPop[k]);
            }
            CountProb();
        }
    }

    public int Genetic() {
        Init();

        SPath="{";
        for (int i=0;i<CLength;i++) {
            if (i!=CLength-1) SPath+=BestTour[i]+",";
            else SPath+=BestTour[i]+"}";
        }
        return BestLength;
    }

    public String getSPath() {
        return SPath;
    }
}
