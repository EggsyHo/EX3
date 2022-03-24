package com.nwnu.kp.utils;

public class AlgorithmUtil {
    static int Weight[];
    static int Value[];
    static int m,n;
    static int f[];
    static int IPath[];
    static String SPath="";

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

    public static void FindPath(int Size) {
        while (Size>0) {
            for (int i=1;i<=n;i++) {
                if (IPath[i]==0) {
                    if (Size-Weight[i]>=0) {
                        if (f[Size-Weight[i]]+Value[i]==f[Size]) {
                            IPath[i]=1;
                            Size-=Weight[i];
                            break;
                        }
                    }
                }
            }
            Size--;
        }
    }

    public int DP() {
        f=new int[10010];
        for (int i=1;i<=n;i++) {
            for (int j=m;j>=Weight[i];j--) {
                f[j]=Math.max(f[j],f[j-Weight[i]]+Value[i]);
            }
        }
        IPath=new int[10010];
        for (int i=1;i<=n;i++) IPath[i]=0;
        FindPath(m);
        SPath="{";
        for (int i=1;i<=n;i++) {
            if (i!=n) SPath+=IPath[i]+",";
            else SPath+=IPath[i]+"}";
        }
        return f[m];
    }

    public String getSPath() {
        return SPath;
    }
}
