/**
  * Object to store GTEx Data.
  */
public class GTExData {

  private String rsid;
  private double pval;
  private String expr;

  public GTExData(String rsid, double pval, String expr) {
    this.rsid = rsid;
    this.pval = pval;
    this.expr = expr;
  }

  public String getRSID() {
    return this.rsid;
  }

  public double getPvalue() {
    return this.pval;
  }

  public String getExpr() {
    return this.expr;
  }

  public void setExpr(String expr) {
    this.expr = expr;
  }

}
