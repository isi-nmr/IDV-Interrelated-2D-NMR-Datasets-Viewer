package IDV;

public class DataHolder {
    private static DataHolder instance = new DataHolder();
    double[] xArrTD;
    double[] yArrTD;
    double[][] dataTD;
    double[][] dataTDFit;
    double[][] dataTDRes;
    double[] xArrFD;
    double[] yArrFD;
    double[][] dataFD;
    double[][] dataFDFit;
    double[][] dataFDRes;
    double[] xArrPPM;


    public static DataHolder getInstance() {
        return instance;
    }

    public DataHolder() {
    }

    public void setDataTD(double[] xArr, double[] yArr, double[][] data) {
        this.xArrTD = xArr;
        this.yArrTD = yArr;
        this.dataTD = data;
    }
    public void setDataFD(double[] xArr, double[] yArr, double[][] data) {
        this.xArrFD = xArr;
        this.yArrFD = yArr;
        this.dataFD = data;
    }
    public void setDataPPM(double[] xArr, double[] yArr, double[][] data) {
        this.xArrPPM = xArr;
        this.yArrFD = yArr;
        this.dataFD = data;
    }
    public void setDataTDFit(double[] xArr, double[] yArr, double[][] data) {
        this.dataTDFit = data;
    }
    public void setDataFDFit(double[] xArr, double[] yArr, double[][] data) {
        this.dataFDFit = data;
    }
    public void setDataTDRes(double[] xArr, double[] yArr, double[][] data) {
        this.dataTDRes = data;
    }
    public void setDataFDRes(double[] xArr, double[] yArr, double[][] data) {
        this.dataFDRes = data;
    }
}
