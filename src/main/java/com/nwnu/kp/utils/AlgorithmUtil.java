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

    static int Ans=0;
    static int CW=0; //Current Weight
    static int CV=0; //Current Value

    public static double Bound(int Index) {
        double RemainW=m-CW;
        double CurrentV=CV;
        while (Index<n && Weight[Index]<=RemainW) {
            RemainW-=Weight[Index];
            CurrentV+=Value[Index];
            Index++;
        }
        if (Index<=n) {
            CurrentV+=(Value[Index]/Weight[Index])*RemainW;
        }
        return CurrentV;
    }

    public static void BackTrack(int Index) {
        if (Index>n) {
            Ans=CV;
            return;
        }
        if (CW+Weight[Index]<=m) {
            CW+=Weight[Index];
            CV+=Value[Index];
            IPath[Index]=1;
            BackTrack(Index+1);
            CW-=Weight[Index];
            CV-=Value[Index];
        }
        if (Bound(Index+1)>Ans) {
            BackTrack(Index+1);
        }
    }

    public int BackTrackAns() {
        SPath="{";
        IPath=new int[10010];
        for (int i=1;i<=n;i++) IPath[i]=0;
        BackTrack(1);
        for (int i=1;i<=n;i++) {
            if (i!=n) {
                SPath+=IPath[i]+",";
            } else {
                SPath+=IPath[i]+"}";
            }
        }
        return Ans;
    }

    static int Suffix[];

    public static void SwapInt(int Data[],int a,int b) {
        int temp=Data[a];
        Data[a]=Data[b];
        Data[b]=temp;
    }

    public static void DataSort() {
        Suffix=new int[10010];
        for (int i=1;i<=n-1;i++) Suffix[i]=i;
        for (int i=1;i<=n-1;i++) {
            for (int j=i+1;j<=n;j++) {
                if ((double)(Value[i]/Weight[i])<(double)(Value[j]/Weight[j])) {
                    SwapInt(Suffix,i,j);
                    SwapInt(Weight,i,j);
                    SwapInt(Value,i,j);
                }
            }
        }
    }

    public static int Greedy() {
        DataSort();
        IPath=new int[10010];
        for (int i=1;i<=n;i++) IPath[i]=0;
        int Size=m;
        int Ans=0;
        for (int i=1;i<=n;i++) {
            if (Size>Weight[i]) {
                Size-=Weight[i];
                Ans+=Value[i];
                IPath[Suffix[i]]=1;
            } else {
                break;
            }
        }
        SPath="{";
        for (int i=1;i<=n;i++) {
            if (i!=n) SPath+=IPath[i]+",";
            else SPath+=IPath[i]+"}";
        }
        return Ans;
    }

    public String getSPath() {
        return SPath;
    }
}
